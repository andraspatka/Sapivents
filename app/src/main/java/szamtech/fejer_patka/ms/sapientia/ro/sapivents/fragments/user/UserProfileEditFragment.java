package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.User;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event.EventListFragment;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.FragmentNavigationUtil;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.LoadingDialogUtil;

public class UserProfileEditFragment extends Fragment {

    public static final int PICK_IMAGE = 1;
    private static final String TAG = "usr_edit_fragment";

    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;
    private static User mActualUser = null;
    private FirebaseStorage mStorage;
    private String mProfilePictureUri;

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

        }

        return view;
    }

    @OnClick(R.id.user_profile_save_btn) void saveProfileDatas(View v){
        Log.v(TAG, "save btn pushed");
        if(isValidEditTextDatas()){

            StorageReference storageRef = mStorage.getReference();
            StorageReference imageRef = storageRef.child("images/profilePictures/" + mFirebaseUser.getPhoneNumber());

            final LoadingDialogUtil loadingDialog = new LoadingDialogUtil(getContext());
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
            });
        }
    }

    @OnClick(R.id.user_profile_image) void changeImage(View v){
        Log.v(TAG, "changeImage");
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(photoPickerIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {

            ClipData clipData = data.getClipData();

            if(clipData != null){

                ClipData.Item item = clipData.getItemAt(0);
                mProfilePictureUri = item.getUri().toString();
                Glide.with(getActivity())
                        .load(item.getUri())
                        .apply(new RequestOptions().circleCropTransform())
                        .into(mProfilePicture);

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

}
