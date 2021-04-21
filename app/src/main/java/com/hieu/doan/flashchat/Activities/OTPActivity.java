package com.hieu.doan.flashchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hieu.doan.flashchat.R;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    Button btnGenerateOTP, btnSignIn;
    EditText etPhoneNumber, etOTP;
    String phoneNumber, otp, email, pass;
    FirebaseAuth auth;
    ShowDialog showDialog;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);
        findViews();
        StartFirebaseLogin();
        try {
            phoneNumber = getIntent().getStringExtra("phoneNum");
            email = getIntent().getStringExtra("email");
            pass = getIntent().getStringExtra("password");
        }
        catch (Exception e){

        }

        btnGenerateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(OTPActivity.this, phoneNumber, Toast.LENGTH_SHORT).show();
                    // [START start_phone_auth]
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(auth)
                                    .setPhoneNumber(phoneNumber)
                                    .setTimeout(60L, TimeUnit.SECONDS)
                                    .setActivity(OTPActivity.this)
                                    .setCallbacks(mCallback)
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp = etOTP.getText().toString();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);

                signInWithPhoneAuthCredential(credential);
            }
        });
    }


    private void findViews() {
        auth = FirebaseAuth.getInstance();
        btnSignIn = findViewById(R.id.btnRegis);
        etOTP = findViewById(R.id.OTPNumber);
        btnGenerateOTP = findViewById(R.id.btnGenOtp);
        showDialog  = new ShowDialog(this);
    }

    private void StartFirebaseLogin() {

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d("TAG", "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("TAG", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("TAG", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                verificationCode = verificationId;
                mResendToken = token;
            }
        };
        // [END phone_auth_callbacks]
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            showDialog.show("Đăng nhập thành công!");
                            FirebaseUser user = task.getResult().getUser();
                            register();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {

    }


    private void register() {
        //final User u = new User(fname.getText().toString(), email.getText().toString(), phoneNumber.getText().toString(), pwd.getText().toString());

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent t = new Intent(OTPActivity.this, ProfileActivity.class);
                            t.putExtra("phoneNumber", phoneNumber);
                            startActivity(t);
                            finish();
                            /*database.collection("Users")
                                    .document()
                                    .set(u).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                }
                            });*/
                            Toast.makeText(getApplicationContext(), "Successfull", Toast.LENGTH_SHORT).show();

                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(), "Email đã tồn tại", Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

