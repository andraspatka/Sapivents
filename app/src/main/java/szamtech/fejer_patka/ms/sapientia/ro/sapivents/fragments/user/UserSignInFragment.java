package szamtech.fejer_patka.ms.sapientia.ro.sapivents.fragments.user;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;

/**
 * Fragment for User sign in
 * Removed unnecessary boilerplate code
 */
public class UserSignInFragment extends Fragment {

    @BindView(R.id.signIn_button) Button signInButton;
    @BindView(R.id.phoneNumber_textView) TextView mPhoneNumber;

    private static final String TAG = "SIGNIN_FRAGMENT";
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    private FirebaseAuth mAuth;

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
        mAuth = FirebaseAuth.getInstance();

        createCallback();
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
        Log.v(TAG, "signIn btn pushed");

        startPhoneNumberVerification(mPhoneNumber.getText().toString());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
        }
    }

    private void createCallback() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;

                Dialog dialog = verificationDialog();
                dialog.show();

                signInWithPhoneAuthCredential(credential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(getActivity(),"Invalid request!",Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(getActivity(),"The SMS quota for the project has been exceeded!",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

            }
        };

    }
    @Override
    public void onStart(){
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            Log.d(TAG, "Current user logged in");
        }
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        Log.d(TAG, "verify: " + code);
        //PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        //signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            if(task.getResult().getAdditionalUserInfo().isNewUser()){
                                Log.d(TAG, "New user");
                            }
                            else{
                                Log.d(TAG, "Registered user");
                            }

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure");
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getActivity(),"The verification code entered was invalid!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public Dialog verificationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.sign_in_dialog, null);
        builder.setView(inflater.inflate(R.layout.sign_in_dialog, null))
                // Add action buttons
                .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.w(TAG, "Sign In btn pressed in sign in dialog");
                        EditText editText = (EditText) view.findViewById(R.id.sign_in_dialog_validation_code);
                        String code = editText.getText().toString();
                        verifyPhoneNumberWithCode(mVerificationId, code);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.w(TAG, "Cancel btn pressed in sign in dialog");
                    }
                });
        return builder.create();
    }
}
