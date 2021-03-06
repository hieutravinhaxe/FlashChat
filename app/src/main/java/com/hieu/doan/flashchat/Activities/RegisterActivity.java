package com.hieu.doan.flashchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    EditText phoneNumber, pwd, rePwd, email;
    Button btnRegister;
    TextView changeLogin;
    FirebaseAuth fAuth;
    FirebaseFirestore database;
    ArrayList<User> list = new ArrayList<>();
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
                //Toast.makeText(getApplicationContext(), "click", Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                list.clear();
                                for(DataSnapshot snapshot1: snapshot.getChildren()){
                                    User u = snapshot1.getValue(User.class);
                                    if(u.getEmail().equals(email.getText().toString())){
                                        list.add(u);
                                    }
                                }
                                if(list.size()>0){
                                    Toast.makeText(getApplicationContext(), "Email ???? t???n t???i", Toast.LENGTH_SHORT).show();
                                }
                                else if (validation() && list.size()==0) {
                                    Intent i = new Intent(RegisterActivity.this, OTPActivity.class);
                                    i.putExtra("phoneNum", phoneNumber.getText().toString());
                                    i.putExtra("email", email.getText().toString());
                                    i.putExtra("password", pwd.getText().toString());
                                    startActivity(i);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });
    }


    private boolean validation() {

        String phone = phoneNumber.getText().toString().trim();
        String password = pwd.getText().toString().trim();
        String rePassword = rePwd.getText().toString().trim();
        final String Email = email.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Kh??ng ???????c ????? tr???ng s??? ??i???n tho???i", Toast.LENGTH_SHORT).show();
            return false;
        } else if (phone.length() != 10&&phone.length() != 12) {
            Toast.makeText(this, "S??? ??i???n tho???i kh??ng ????ng ?????nh d???ng!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Kh??ng ???????c ????? tr???ng s??? m???t kh???u", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 6) {
            Toast.makeText(this, "M???t kh???u kh??ng ???????c ng???n h??n 6 k?? t???", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!rePassword.equals(password)) {
            Toast.makeText(this, "M???t kh???u x??c th???c kh??ng tr??ng kh???p", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
