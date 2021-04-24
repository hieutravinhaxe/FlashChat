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
                FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean t = true;
                                for(DataSnapshot snapshot1: snapshot.getChildren()){
                                    User u = snapshot1.getValue(User.class);
                                    if(u.getEmail().equals(email.getText().toString())){
                                        t = false;
                                    }
                                }
                                if(!t){
                                    Toast.makeText(getApplicationContext(), "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                                }
                                else if (validation() && t) {
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
            Toast.makeText(this, "Không được để trống số điện thoại", Toast.LENGTH_SHORT).show();
            return false;
        } else if (phone.length() != 10&&phone.length() != 12) {
            Toast.makeText(this, "Số điện thoại không đúng định dạng!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Không được để trống số mật khẩu", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu không được ngắn hơn 6 kí tự", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!rePassword.equals(password)) {
            Toast.makeText(this, "Mật khẩu xác thực không trùng khớp", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
