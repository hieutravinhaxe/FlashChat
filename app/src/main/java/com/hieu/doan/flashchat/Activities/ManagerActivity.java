package com.hieu.doan.flashchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hieu.doan.flashchat.R;

public class ManagerActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menuManager);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuChat:
                        //Toast.makeText(getApplicationContext(), "chat", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.menuFriend:
                        //Toast.makeText(getApplicationContext(), "call", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), CallActivity.class));
                        overridePendingTransition(0,0);
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