package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.user;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.FirebaseAuthUtil;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils.FragmentNavigationUtil;

/**
 * Fragment for User sign in
 * Removed unnecessary boilerplate code
 */
public class UserSignInFragment extends Fragment {

    @BindView(R.id.signIn_button) Button signInButton;
    @BindView(R.id.register_button) Button signUpButton;
    @BindView(R.id.phoneNumber_textView) TextView mPhoneNumber;

    private static final String TAG = "SIGNIN_FRAGMENT";
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

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
        firebaseAuthUtil.createCallback();
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
        EditText phoneNumber = (EditText) getActivity().findViewById(R.id.phoneNumber_textView);
        Log.v(TAG, "signIn btn pushed with code: " + phoneNumber.getText().toString());
        firebaseAuthUtil.startPhoneNumberVerification(phoneNumber.getText().toString());
    }

    @OnClick(R.id.register_button) void signUp(View v){
        Log.v(TAG, "signUp btn pushed with code: ");
        UserRegistrationFragment userRegistrationFragment = new UserRegistrationFragment();
        FragmentNavigationUtil.addFragmentOnTop(getActivity(), userRegistrationFragment, R.id.fragment_place);
    }

    @Override
    public void onStart(){
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuthUtil.getFirebaseUser();

        if(currentUser != null){
            Log.d(TAG, "Current user logged in");
        }
    }

}
