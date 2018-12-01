package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

    private static final String TAG = "REGISTRATION_FRAGMENT";
    private static final String KEY_FIRST_NAME = "key_first_name";
    private static final String KEY_LAST_NAME = "key_last_name";
    private static final String KEY_PHONE_NUMBER = "key_phone_number";
    private boolean mSignUpButtonPushed = false;

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mPhoneNumber;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuthUtil firebaseAuthUtil;

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
                if(mSignUpButtonPushed) {
                    mSignUpButtonPushed = false;
                    Log.v(TAG, "onAuthStateChanged in reg");
                    FirebaseAuth auth = firebaseAuthUtil.getFirebaseAuth();
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        setUserProfile();
                    }
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

        return view;
    }

    @OnClick(R.id.register_button) void signUp(View v){
        mSignUpButtonPushed = true;
        mFirstName = (EditText) getActivity().findViewById(R.id.firstName_editText);
        mLastName = (EditText) getActivity().findViewById(R.id.lastName_editText);
        mPhoneNumber = (EditText) getActivity().findViewById(R.id.phoneNumber_editText);
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
        String displayName = mFirstName.getText().toString() + mLastName.getText().toString();
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
                            Toast.makeText(getActivity(),"You have successfully registered and logged in!",Toast.LENGTH_SHORT).show();

                            EventListFragment listFragment = new EventListFragment();
                            FragmentNavigationUtil.addFragmentToScreen(
                                    getActivity(),
                                    listFragment,
                                    R.id.fragment_place,
                                    FragmentNavigationUtil.HOME_SCREEN
                            );
                        }
                        else{
                            Toast.makeText(getActivity(),"Something went wrong during registration!",Toast.LENGTH_SHORT).show();

                            UserRegistrationFragment userRegistrationFragment = new UserRegistrationFragment();
                            FragmentNavigationUtil.addFragmentToScreen(getActivity(), userRegistrationFragment, R.id.fragment_place, FragmentNavigationUtil.HOME_SCREEN);
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
