package com.hieu.doan.flashchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieu.doan.flashchat.Models.Group;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;

import java.util.ArrayList;

public class ManagerActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ImageView avt, logout;
    private TextView name, phone, email;
    private Button btnEdit, btnChangePwd;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menuManager);

        avt = findViewById(R.id.avt);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        btnEdit = findViewById(R.id.btnEdit);
        logout = findViewById(R.id.logout);
        btnChangePwd = findViewById(R.id.btnChangePwd);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        database.getReference().child("users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                name.setText(user.getName());
                phone.setText(user.getPhone());
                email.setText(user.getEmail());
                if (user.getImage().equals("undefined")) {
                    avt.setImageResource(R.drawable.profile);
                } else {
                    Glide.with(ManagerActivity.this).load(user.getImage()).into(avt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManagerActivity.this, EditProfileActivity.class);
                startActivity(intent);

            }
        });

        btnChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManagerActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ManagerActivity.this);
                builder.setTitle("Đăng xuất");
                builder.setMessage("bạn có đăng xuất?");

                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        auth.signOut();
                        Intent intent = new Intent(ManagerActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuChat:
                        //Toast.makeText(getApplicationContext(), "chat", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.menuFriends:
                        //Toast.makeText(getApplicationContext(), "call", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.menuGroup:
                        //Toast.makeText(getApplicationContext(), "call", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), GroupsActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.menuManager:
                        //Toast.makeText(getApplicationContext(), "manager", Toast.LENGTH_SHORT).show();
                        /*startActivity(new Intent(getApplicationContext(), ManagerActivity.class));
                        overridePendingTransition(0,0);
                        finish();*/
                        return true;
                }
                return false;
            }
        });
    }
}