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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
 * Fragment for User sign in
 * Removed unnecessary boilerplate code
 */
public class UserSignInFragment extends Fragment {

    @BindView(R.id.signIn_button) Button signInButton;
    @BindView(R.id.not_register_button) Button notRegisteredButton;
    @BindView(R.id.phoneNumber_textView) TextView mPhoneNumber;

    private static final String TAG = "SIGNIN_FRAGMENT";
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private boolean mSignInButtonPushed = false;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuthUtil firebaseAuthUtil;

    public UserSignInFragment() {
        // Required empty public constructor
    }

    public static UserSignInFragment newInstance() {
        UserSignInFragment fragment = new UserSignInFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "On create");

        // Restore instance state
        if (savedInstanceState != null) {
            onActivityCreated(savedInstanceState);
        }

        // Initialize Firebase Auth
        firebaseAuthUtil = new FirebaseAuthUtil(getActivity());
        firebaseAuthUtil.initializeFirebaseAuth();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mSignInButtonPushed) {
                    mSignInButtonPushed = false;
                    Log.v(TAG, "onAuthStateChanged in sign");
                    FirebaseAuth auth = firebaseAuthUtil.getFirebaseAuth();
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null && user.getDisplayName() != null) {
                        EventListFragment listFragment = new EventListFragment();
                        FragmentNavigationUtil.addFragmentToScreen(
                                getActivity(),
                                listFragment,
                                R.id.fragment_place,
                                FragmentNavigationUtil.HOME_SCREEN
                        );
                    } else {
                        Toast.makeText(getActivity(), "User not registered!", Toast.LENGTH_SHORT).show();
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
        View view = inflater.inflate(R.layout.fragment_user_sign_in, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.signIn_button) void signIn(View v){
        EditText phoneNumberEditText = (EditText) getActivity().findViewById(R.id.phoneNumber_textView);
        String phoneNumber = phoneNumberEditText.getText().toString();
        Log.v(TAG, "signIn btn pushed with code: " + phoneNumber);
        if(isValidPhoneNumber(phoneNumber)) {
            mSignInButtonPushed = true;
            firebaseAuthUtil.startPhoneNumberVerification(phoneNumber);
        }
    }

    @OnClick(R.id.not_register_button) void signUp(View v){
        Log.v(TAG, "notRegisteredBtn pushed");
        UserRegistrationFragment userRegistrationFragment = new UserRegistrationFragment();
        FragmentNavigationUtil.addFragmentToScreen(getActivity(), userRegistrationFragment, R.id.fragment_place, FragmentNavigationUtil.HOME_SCREEN);
    }

    @Override
    public void onStart(){
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuthUtil.getFirebaseAuth().getCurrentUser();

        if(currentUser != null){
            Log.d(TAG, "Current user logged in");

            EventListFragment listFragment = new EventListFragment();
            FragmentNavigationUtil.addFragmentToScreen(
                    getActivity(),
                    listFragment,
                    R.id.fragment_place,
                    FragmentNavigationUtil.HOME_SCREEN
            );
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber){
        Pattern pattern = Pattern.compile("^\\+[0-9]{11}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        if(!matcher.find()){
            Toast.makeText(getActivity(), "Invalid phone number format!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
