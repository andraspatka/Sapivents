package szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import szamtech.fejer_patka.ms.sapientia.ro.sapivents.R;

public class FirebaseAuthUtil {

    private static final String TAG = "FIREBASE_AUTH_UTILS";
    private final Context mContext;
    private LoadingDialogUtil mLoadingDialog = null;

    private boolean isCodeSent = false;

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private PhoneAuthProvider mPhoneAuth;
    private FirebaseAuth mAuth;

    public FirebaseAuthUtil(Context context) {
        mContext = context;
    }

    public void initializeFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        createCallback();
    }

    private void createCallback() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            //Called when auto-retrieval is possible
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted: " + credential.getSignInMethod() + " " + credential.getProvider());

                mVerificationInProgress = false;

                if(mLoadingDialog != null){
                    mLoadingDialog.endDialog();
                }

                signInWithPhoneAuthCredential(credential);
            }
            //Called when the timeout duration for auto-retrieval ended
            @Override
            public void onCodeAutoRetrievalTimeOut(String verificationId){
                Log.v(TAG, "---onCodeAuthRetrievalTimeOut---");
                verificationDialog();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.v(TAG, "onVerificationFailed", e);

                if(mLoadingDialog != null){
                    mLoadingDialog.endDialog();
                }

                mVerificationInProgress = false;

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(mContext,"Invalid request!",Toast.LENGTH_SHORT).show();
                } else {
                    if (e instanceof FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                        Toast.makeText(mContext, "The SMS quota for the project has been exceeded!", Toast.LENGTH_SHORT).show();
                    }
                    else if (e instanceof FirebaseNetworkException){
                        Toast.makeText(mContext, "Network error!", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                isCodeSent = true;

                if(mLoadingDialog != null){
                    mLoadingDialog.endDialog();
                }

                verificationDialog().show();

            }
        };
    }

    public void startPhoneNumberVerification(String phoneNumber) {

        mLoadingDialog = new LoadingDialogUtil(mContext);
        mLoadingDialog.setDialogText("Connecting to database...");
        mLoadingDialog.showDialog();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                5,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                (Activity) mContext,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        Log.d(TAG, "verify: " + code);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.v(TAG, "signInWithCredential: success");

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.v(TAG, "signInWithCredential: failure" + task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                // The verification code entered was invalid
                                Toast.makeText(mContext,"The verification code entered was invalid!",Toast.LENGTH_SHORT).show();
                                verificationDialog().show();

                            }
                            else if(task.getException() instanceof FirebaseNetworkException){

                                Toast.makeText(mContext, "Network error!", Toast.LENGTH_SHORT).show();

                            }
                            else{

                                Toast.makeText(mContext, "Something went wrong during authentication!", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });
    }

    private Dialog verificationDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        // Get the layout inflater
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.sign_in_dialog, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.w(TAG, "Sign In btn pressed in sign in dialog");

                        EditText editText = (EditText) view.findViewById(R.id.sign_in_dialog_validation_code);
                        verifyPhoneNumberWithCode(mVerificationId, editText.getText().toString());

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Log.w(TAG, "Cancel btn pressed in sign in dialog");

                    }
                });
        builder.setCancelable(false);
        return builder.create();
    }

    public void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                5,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                (Activity) mContext,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    public void signOut() {
        mAuth.signOut();
    }

    public boolean getVerificationInProgress() {
        return mVerificationInProgress;
    }

    public FirebaseAuth getFirebaseAuth(){
        return mAuth;
    }

    public void myAddAuthStateListener(FirebaseAuth.AuthStateListener authStateListener){
        mAuth.addAuthStateListener(authStateListener);
    }

}
