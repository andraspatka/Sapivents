package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.user;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.User;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event.EventListFragment;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.FragmentNavigationUtil;

public class UserProfileEditFragment extends Fragment {

    public static final int PICK_IMAGE = 1;
    private static final String TAG = "usr_edit_fragment";

    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;
    private static User mActualUser = null;
    private FirebaseStorage mStorage;

    @BindView(R.id.user_profile_save_btn) Button mSaveButton;
    @BindView(R.id.usr_profile_fname_editText) EditText mFirstName;
    @BindView(R.id.usr_profile_lname_editText) EditText mLastName;
    @BindView(R.id.user_profile_image) ImageView mProfilePicture;

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
                            .apply(new RequestOptions().circleCropTransform())
                            .into(mProfilePicture);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.v(TAG, "Error loading profile picure");
                }
            });

            mFirstName.setText(mActualUser.getFirstName());
            mLastName.setText(mActualUser.getLastName());

        }

        return view;
    }

    @OnClick(R.id.user_profile_save_btn) void saveProfileDatas(View v){
        Log.v(TAG, "save btn pushed");
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
                StorageReference storageRef = mStorage.getReference();
                StorageReference imageRef = storageRef.child("images/profilePictures/" + mFirebaseUser.getPhoneNumber());
                UploadTask uploadTask = imageRef.putFile(item.getUri());
                Log.v(TAG, item.getUri().toString());

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.v(TAG, "image upload failed");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

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
                });

            }
        }
    }

}
