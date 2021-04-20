package com.hieu.doan.flashchat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hieu.doan.flashchat.R;

public class ChangePasswordActivity extends AppCompatActivity {
    Button btnSave;
    EditText pwdOld, pwdNew, rePwdNew;
    FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        btnSave = findViewById(R.id.btnSave);
        pwdOld = findViewById(R.id.pwdOld);
        pwdNew = findViewById(R.id.pwdNew);
        rePwdNew = findViewById(R.id.rePwdNew);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validation()){

                    final String passwordOld = pwdOld.getText().toString();
                    final String passwordNew = pwdNew.getText().toString();
                    auth = FirebaseAuth.getInstance();
                    final FirebaseUser user = auth.getCurrentUser();

                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), passwordOld);

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.updatePassword(passwordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Intent intent = new Intent(ChangePasswordActivity.this, ManagerActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                    Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });


    }

    private boolean validation() {
        String passwordOld = pwdOld.getText().toString().trim();
        String passwordNew = pwdNew.getText().toString();
        String rePasswordNew = rePwdNew.getText().toString();

        if(TextUtils.isEmpty(passwordOld) || TextUtils.isEmpty(passwordNew) || TextUtils.isEmpty(rePasswordNew)){
            Toast.makeText(this, "Phẩi điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(passwordNew.length() < 6 || passwordOld.length() < 6){
            Toast.makeText(this, "Mật khẩu không được ngắn hơn 6 kí tự", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!passwordNew.equals(rePasswordNew)){
            Toast.makeText(this, "Mật khẩu xác thực không trùng khớp", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
