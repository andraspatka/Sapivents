package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.user;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans.User;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.FragmentNavigationUtil;

/**
 * Fragment for editing, viewing User account information
 * Removed unnecessary boilerplate code
 */
public class UserProfileViewFragment extends Fragment {

    @BindView(R.id.user_profile_sign_out_btn) Button mSignOutButton;
    @BindView(R.id.user_profile_events_btn) Button mEventsButton;
    @BindView(R.id.user_profile_edit_btn) Button mEditButton;
    @BindView(R.id.user_profile_first_name) TextView mFirstName;
    @BindView(R.id.user_profile_last_name) TextView mLastName;
    @BindView(R.id.user_profile_phone_number) TextView mPhoneNumber;
    @BindView(R.id.user_profile_image) ImageView mProfilePicture;
    @BindView(R.id.user_view_img_progress) ProgressBar mProgressBar;

    private static final String TAG = "usr_profile_fragment";
    private DatabaseReference mDatabase;
    private FirebaseUser mFirebaseUser;
    private User mUser;
    private FirebaseStorage mStorage;

    public UserProfileViewFragment() {
        // Required empty public constructor
    }

    public static UserProfileViewFragment newInstance(String param1, String param2) {
        UserProfileViewFragment fragment = new UserProfileViewFragment();
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
        View view = inflater.inflate(R.layout.fragment_user_profile_view, container, false);
        ButterKnife.bind(this,view);

        Log.v(TAG, "on create view");

        //If a user is logged in
        if(mFirebaseUser != null){

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

            mDatabase = FirebaseDatabase.getInstance().getReference("users");
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User actualUser = dataSnapshot.child(mFirebaseUser.getPhoneNumber()).getValue(User.class);
                    mUser = actualUser;
                    mFirstName.setText(actualUser.getFirstName());
                    mLastName.setText(actualUser.getLastName());
                    mPhoneNumber.setText(mFirebaseUser.getPhoneNumber());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.v(TAG, databaseError.getMessage());
                }
            });

        }else{

            mSignOutButton.setEnabled(false);

        }

        return view;
    }

    /**
     * Callback method for the signOut button
     * Signs out the firebase user
     * Adds the userSignInFragment as a single fragment
     * @param v
     */
    @OnClick(R.id.user_profile_sign_out_btn) void signOut(View v){
        //Sign out the user
        FirebaseAuth.getInstance().signOut();
        //Add the UserSignInFragment as a single fragment
        UserSignInFragment userSignInFragment = new UserSignInFragment();
        FragmentNavigationUtil.addAsSingleFragment(
                getActivity(),
                userSignInFragment,
                R.id.fragment_place,
                FragmentNavigationUtil.HOME_SCREEN);
        //Set the padding to the R.id.fragment_place FrameLayout
        FrameLayout fragmentPlace = (FrameLayout) getActivity().findViewById(R.id.fragment_place);
        BottomNavigationView bottomNav = (BottomNavigationView) getActivity().findViewById(R.id.bottom_nav);
        fragmentPlace.setPadding(0,0,0,0);
        //Make the bottom navigation invisible
        bottomNav.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.user_profile_events_btn) void listMyEvents(View v){
        UserEventListFragment userEventListFragment = new UserEventListFragment();
        FragmentNavigationUtil.addFragmentToScreen(
                getContext(),
                userEventListFragment,
                R.id.fragment_place,
                FragmentNavigationUtil.ACCOUNT_SCREEN
                );
    }

    @OnClick(R.id.user_profile_edit_btn) void editProfile(View v){
        UserProfileEditFragment userProfileEditFragment = UserProfileEditFragment.newInstance(mUser);
        FragmentNavigationUtil.addFragmentToScreen(
                getContext(),
                userProfileEditFragment,
                R.id.fragment_place,
                FragmentNavigationUtil.ACCOUNT_SCREEN
        );
    }

}
