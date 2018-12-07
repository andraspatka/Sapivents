package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.user;

import android.app.Activity;
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
    private Activity mActivity;

    public UserSignInFragment() {
        // Required empty public constructor
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
        mActivity = getActivity();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mSignInButtonPushed) {
                    mSignInButtonPushed = false;
                    Log.v(TAG, "onAuthStateChanged in sign");
                    FirebaseAuth auth = firebaseAuthUtil.getFirebaseAuth();
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null && user.getDisplayName() != null) {
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
