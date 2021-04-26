package com.hieu.doan.flashchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;

public class ForgotPassword extends AppCompatActivity {
    private TextView changeLogin, changeRegis;
    private Button btnResetPwd;
    private EditText emailText;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ProgressDialog dialog;
    private int e;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        changeLogin = findViewById(R.id.loginTv);
        changeRegis = findViewById(R.id.regisTextView);
        btnResetPwd = findViewById(R.id.btnResetPwd);
        emailText = findViewById(R.id.emailEditText);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        changeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        changeRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });

        btnResetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailText.getText().toString();
                if(email.equals("")){
                    Toast.makeText(getApplicationContext(), "Chưa nhập email", Toast.LENGTH_SHORT).show();
                }
                else{
                    dialog.show();
                    database.getReference().child("users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            e = 505;
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                User u = dataSnapshot.getValue(User.class);
                                if(email.equals(u.getEmail())) {
                                    auth.sendPasswordResetEmail(email)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        dialog.dismiss();
                                                        Toast.makeText(getApplicationContext(),"Đã gửi mật khẩu khôi phục vào email của bạn", Toast.LENGTH_LONG).show();
                                                        e = 200;
                                                    }else{
                                                        dialog.dismiss();
                                                        Toast.makeText(getApplicationContext(),"Failed to reset password", Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            });
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(e ==505){
                    Toast.makeText(getApplicationContext(),"Không tìm thấy người dùng này", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}