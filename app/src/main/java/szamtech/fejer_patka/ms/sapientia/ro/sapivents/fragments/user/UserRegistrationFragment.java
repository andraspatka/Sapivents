package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.user;

import android.app.Activity;
import android.content.Context;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.event.EventListFragment;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.FirebaseAuthUtil;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.FragmentNavigationUtil;

/**
 * A fragment for user registration
 * Removed unnecessary boilerplate code
 */
public class UserRegistrationFragment extends Fragment {

    @BindView(R.id.register_button) Button signUpButton;
    @BindView(R.id.firstName_editText) EditText mFirstName;
    @BindView(R.id.lastName_editText) EditText mLastName;
    @BindView(R.id.phoneNumber_editText) EditText mPhoneNumber;

    private static final String TAG = "REGISTRATION_FRAGMENT";
    private static final String KEY_FIRST_NAME = "key_first_name";
    private static final String KEY_LAST_NAME = "key_last_name";
    private static final String KEY_PHONE_NUMBER = "key_phone_number";
    private boolean mSignUpButtonPushed = false;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuthUtil firebaseAuthUtil;
    private Activity mActivity;

    public UserRegistrationFragment() {
        // Required empty public constructor
    }

    public static UserRegistrationFragment newInstance() {
        UserRegistrationFragment fragment = new UserRegistrationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "On create");

        // Initialize Firebase Auth
        firebaseAuthUtil = new FirebaseAuthUtil(getActivity());
        firebaseAuthUtil.initializeFirebaseAuth();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mSignUpButtonPushed){
                    Log.v(TAG, "onAuthStateChanged in reg");
                    FirebaseAuth auth = firebaseAuthUtil.getFirebaseAuth();
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        setUserProfile();
                    }
                    mSignUpButtonPushed = false;
                }
            }
        };

        firebaseAuthUtil.myAddAuthStateListener(mAuthListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_registration, container, false);
        ButterKnife.bind(this, view);
        mActivity = getActivity();
        return view;
    }

    @OnClick(R.id.register_button) void signUp(View v){
        mSignUpButtonPushed = true;
        Log.v(TAG, "signUp btn pushed");

        createUser(mPhoneNumber.getText().toString());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_FIRST_NAME, mFirstName.getText().toString());
        outState.putString(KEY_LAST_NAME, mLastName.getText().toString());
        outState.putString(KEY_PHONE_NUMBER, mPhoneNumber.getText().toString());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            mFirstName.setText(savedInstanceState.getString(KEY_FIRST_NAME));
            mLastName.setText(savedInstanceState.getString(KEY_LAST_NAME));
            mPhoneNumber.setText(savedInstanceState.getString(KEY_PHONE_NUMBER));
        }
    }

    private void createUser(String phoneNumber){
        firebaseAuthUtil.startPhoneNumberVerification(phoneNumber);
    }

    private void setUserProfile(){
        String displayName = mFirstName.getText().toString() + " " + mLastName.getText().toString();
        FirebaseUser user = firebaseAuthUtil.getFirebaseAuth().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "New user profile set up");
                            Toast.makeText(mActivity,"You have successfully registered and logged in!",Toast.LENGTH_SHORT).show();

                            //The user registered successfully, so redirect him to the EventListFragment
                            //Set the bottom and top padding
                            int bottomPaddingInPixels = (int) mActivity.getResources().getDimension(R.dimen.bottom_nav_height);
                            int topPaddingInPixels = (int) mActivity.getResources().getDimension(R.dimen.logo_height);
                            //Get the fragment_place FrameLayout
                            FrameLayout fragmentPlace = (FrameLayout) mActivity.findViewById(R.id.fragment_place);
                            //Get the bottom nav
                            BottomNavigationView bottomNav = (BottomNavigationView) mActivity.findViewById(R.id.bottom_nav);
                            //Set home as the selected bottom navigation item
                            bottomNav.setSelectedItemId(R.id.menu_home);
                            //Set the padding to the R.id.fragment_place FrameLayout
                            fragmentPlace.setPadding(0,topPaddingInPixels,0,bottomPaddingInPixels);
                            //Make the bottom navigation visible
                            bottomNav.setVisibility(View.VISIBLE);

                            //Add the fragment
                            EventListFragment listFragment = new EventListFragment();
                            FragmentNavigationUtil.addAsSingleFragment(
                                    mActivity,
                                    listFragment,
                                    R.id.fragment_place,
                                    FragmentNavigationUtil.HOME_SCREEN
                            );
                        }
                        else{
                            Toast.makeText(mActivity,"Something went wrong during registration!",Toast.LENGTH_SHORT).show();
                            mFirstName.setText("");
                            mLastName.setText("");
                            mPhoneNumber.setText("");
                        }
                    }
                });
    }

    private boolean isValidEditTextDatas(){
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String phoneNumber = mPhoneNumber.getText().toString();

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

        Pattern phoneNumberPattern = Pattern.compile("^\\+[0-9]{11}$");
        Matcher phoneNumberMatcher = phoneNumberPattern.matcher(phoneNumber);
        if(!phoneNumberMatcher.find()){
            Toast.makeText(getActivity(), "Invalid phone number format!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
