package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.user;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.User;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.FragmentNavigationUtil;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.LoadingDialogUtil;

public class UserProfileEditFragment extends Fragment {

    public static final int PICK_IMAGE = 0;
    public static final int TAKE_IMAGE = 1;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST = 1;
    private static final String TAG = "usr_edit_fragment";

    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;
    private static User mActualUser = null;
    private FirebaseStorage mStorage;
    private String mProfilePictureUri;
    private boolean imgButtonPressed = false;

    LoadingDialogUtil loadingDialog;

    @BindView(R.id.user_profile_save_btn) Button mSaveButton;
    @BindView(R.id.usr_profile_fname_editText) EditText mFirstName;
    @BindView(R.id.usr_profile_lname_editText) EditText mLastName;
    @BindView(R.id.user_profile_image) ImageView mProfilePicture;
    @BindView(R.id.user_edit_img_progress) ProgressBar mProgressBar;

    public UserProfileEditFragment() {
        // Required empty public constructor
    }

    public static UserProfileEditFragment newInstance(User user) {
        UserProfileEditFragment fragment = new UserProfileEditFragment();
        mActualUser = user;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorage = FirebaseStorage.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(szamtech.fejer_patka.ms.sapientia.ro.sapivents.R.layout.fragment_user_profile_edit, container, false);
        ButterKnife.bind(this,view);

        if(mActualUser != null){

            StorageReference imgRef = mStorage.getReference();
            imgRef.child("images/profilePictures/" + mFirebaseUser.getPhoneNumber()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.v(TAG, "Profile picture loaded!");
                    Glide.with(getActivity())
                            .load(uri)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    mProgressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    mProgressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .apply(new RequestOptions().circleCropTransform())
                            .into(mProfilePicture);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.v(TAG, "Error loading profile picure");
                    Glide.with(getActivity())
                            .load(R.drawable.ic_launcher_background)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    mProgressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    mProgressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .apply(new RequestOptions().circleCropTransform())
                            .into(mProfilePicture);
                }
            });

            mFirstName.setText(mActualUser.getFirstName());
            mLastName.setText(mActualUser.getLastName());

            //asking for camera permission
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);


            }

        }

        return view;
    }

    @OnClick(R.id.user_profile_save_btn) void saveProfileDatas(View v){
        Log.v(TAG, "save btn pushed");
        if(isValidEditTextDatas() && imgButtonPressed){

            StorageReference storageRef = mStorage.getReference();
            StorageReference imageRef = storageRef.child("images/profilePictures/" + mFirebaseUser.getPhoneNumber());

            loadingDialog = new LoadingDialogUtil(getContext());
            loadingDialog.setDialogText("Uploading changes...");
            loadingDialog.showDialog();

            UploadTask uploadTask = imageRef.putFile(Uri.parse(mProfilePictureUri));

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.v(TAG, "image upload failed");
                    loadingDialog.endDialog();
                    Toast.makeText(getActivity(), "Uploading changes failed, please try again!", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    uploadUserData();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    loadingDialog.setDialogText("Upload is " + progress + "% done");

                }

            });
        }
        else{

            loadingDialog = new LoadingDialogUtil(getContext());
            loadingDialog.setDialogText("Uploading changes...");
            loadingDialog.showDialog();

            uploadUserData();

        }
    }

    @OnClick(R.id.user_profile_image) void changeImage(View v){
        Log.v(TAG, "changeImage");
        imgButtonPressed = true;

        //creating option for dialog
        CharSequence colors[] = new CharSequence[] {"Take Photo", "Choose from Library"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo");
        builder.setItems(colors, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 0){

                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(getActivity(), "You have no permission for using camera!", Toast.LENGTH_SHORT).show();

                    }
                    else{

                        //taking photo with camera
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, TAKE_IMAGE);

                    }


                }
                else{
                    if(which == 1){
                        //selecting a photo from gallery
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , PICK_IMAGE);

                    }
                }

            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    Log.v(TAG, "Cancel btn pressed on choose photo dialog");
                }

            });
        builder.show();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && data != null) {

            Uri selectedImage = null;

            switch (requestCode) {

                case PICK_IMAGE:

                    selectedImage = data.getData();
                    mProfilePictureUri = selectedImage.toString();
                    Glide.with(getActivity())
                            .load(selectedImage)
                            .apply(new RequestOptions().circleCropTransform())
                            .into(mProfilePicture);

                    break;

                case TAKE_IMAGE:

                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    selectedImage = getImageUri(getContext(), imageBitmap);

                    mProfilePictureUri = selectedImage.toString();
                    Glide.with(getActivity())
                            .load(selectedImage)
                            .apply(new RequestOptions().circleCropTransform())
                            .into(mProfilePicture);


                    break;

            }

        }

    }

    private boolean isValidEditTextDatas(){
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();

        Pattern namePattern = Pattern.compile("^[a-zA-Z]+[-]?[a-zA-Z]+$");
        Matcher firstNameMatcher = namePattern.matcher(firstName);
        if(!firstNameMatcher.find()){
            Toast.makeText(getActivity(), "Invalid first name format!", Toast.LENGTH_SHORT).show();
            return false;
        }

        Matcher lastNameMatcher = namePattern.matcher(lastName);
        if(!lastNameMatcher.find()){
            Toast.makeText(getActivity(), "Invalid last name format!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    void uploadUserData(){

        mDatabase = FirebaseDatabase.getInstance().getReference("users/" + mFirebaseUser.getPhoneNumber());
        Map newUserData = new HashMap();
        newUserData.put("firstName", mFirstName.getText().toString());
        newUserData.put("lastName", mLastName.getText().toString());
        mDatabase.updateChildren(newUserData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        loadingDialog.endDialog();
                        Toast.makeText(getActivity(), "User data changed!", Toast.LENGTH_SHORT).show();

                        //The user registered successfully, so redirect him to the EventListFragment
                        //Set the bottom and top padding
                        int bottomPaddingInPixels = (int) getActivity().getResources().getDimension(R.dimen.bottom_nav_height);
                        int topPaddingInPixels = (int) getActivity().getResources().getDimension(R.dimen.logo_height);
                        //Get the fragment_place FrameLayout
                        FrameLayout fragmentPlace = (FrameLayout) getActivity().findViewById(R.id.fragment_place);
                        //Get the bottom nav
                        BottomNavigationView bottomNav = (BottomNavigationView) getActivity().findViewById(R.id.bottom_nav);
                        //Set home as the selected bottom navigation item
                        bottomNav.setSelectedItemId(R.id.menu_account);
                        //Set the padding to the R.id.fragment_place FrameLayout
                        fragmentPlace.setPadding(0,topPaddingInPixels,0,bottomPaddingInPixels);
                        //Make the bottom navigation visible
                        bottomNav.setVisibility(View.VISIBLE);

                        //Add the fragment
                        UserProfileViewFragment userProfileViewFragment = new UserProfileViewFragment();
                        FragmentNavigationUtil.addAsSingleFragment(
                                getActivity(),
                                userProfileViewFragment,
                                R.id.fragment_place,
                                FragmentNavigationUtil.ACCOUNT_SCREEN
                        );

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.endDialog();
                        Toast.makeText(getActivity(), "Something went wrong, please try again!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
