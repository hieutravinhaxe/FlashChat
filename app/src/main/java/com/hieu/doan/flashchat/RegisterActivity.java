package com.hieu.doan.flashchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    EditText phoneNumber, pwd, rePwd, email;
    Button btnRegister;
    TextView changeLogin;
    FirebaseAuth fAuth;
    FirebaseFirestore database;
    String userID;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        phoneNumber = findViewById(R.id.phoneEditText);
        pwd = findViewById(R.id.pwdEditText);
        rePwd = findViewById(R.id.rePwdEditText);
        btnRegister = findViewById(R.id.btnRegis);
        changeLogin = findViewById(R.id.loginTextView);
        email = findViewById(R.id.emailEditText);
        fAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        changeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validation()){
                    /*Intent i = new Intent(RegisterActivity.this, OTPActivity.class);
                    i.putExtra("phoneNum", phoneNumber.getText().toString());
                    i.putExtra("password", pwd.getText().toString());
                    startActivity(i);*/
                    register();
                }
            }
        });
    }
    private  void register(){
        //final User u = new User(fname.getText().toString(), email.getText().toString(), phoneNumber.getText().toString(), pwd.getText().toString());

        String emailText = email.getText().toString();
        String password  = pwd.getText().toString();
        fAuth.createUserWithEmailAndPassword(emailText, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
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

                        }else{
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(getApplicationContext(), "Email đã tồn tại", Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    private boolean validation() {
        String phone = phoneNumber.getText().toString().trim();
        String password = pwd.getText().toString().trim();
        String rePassword = rePwd.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Không được để trống số điện thoại", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Không được để trống số mật khẩu", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(password.length() < 6){
            Toast.makeText(this, "Mật khẩu không được ngắn hơn 6 kí tự", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!rePassword.equals(password)){
            Toast.makeText(this, "Mật khẩu xác thực không trùng khớp", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}