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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hieu.doan.flashchat.R;

public class LoginActivity extends AppCompatActivity {
    EditText email, pwd;
    Button login;
    TextView register;
    TextView resetPwd;
    FirebaseAuth fAuth;
    ProgressDialog dialog;
    ShowDialog showDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        showDialog = new ShowDialog(this);
        email = findViewById(R.id.emailEditText);
        pwd = findViewById(R.id.pwdEditText);
        register = findViewById(R.id.regisTextView);
        login = findViewById(R.id.btnLogin);
        resetPwd = findViewById(R.id.resetPwd);
        fAuth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Wait just moment");
        dialog.setCancelable(false);

//        try {
//            Intent i = getIntent();
//            String text = i.getStringExtra("key");
//            email.setText(text.split(">>>")[0]);
//            pwd.setText(text.split(">>>")[1]);
//            showDialog.show("Vui lòng xác nhận mail trước khi đăng nhập!");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        resetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                String emailUser = email.getText().toString();
                String password = pwd.getText().toString();
                fAuth.signInWithEmailAndPassword(emailUser, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

//    private void checkEmail(String email) {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        boolean emailVerified = user.isEmailVerified();
//
//    }
}
