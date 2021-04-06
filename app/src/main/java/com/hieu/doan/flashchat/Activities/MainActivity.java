package com.hieu.doan.flashchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieu.doan.flashchat.Adapters.ListConverAdapter;
import com.hieu.doan.flashchat.R;
import com.hieu.doan.flashchat.Models.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database;
    RecyclerView recyclerView;
    FirebaseAuth auth;
    ArrayList<User> listConver ;
    ListConverAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        listConver = new ArrayList<User>();

        adapter = new ListConverAdapter(this, listConver);
        recyclerView.setAdapter(adapter);

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listConver.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User u = dataSnapshot.getValue(User.class);
                    if(!u.getId().equals(auth.getUid())){
                        listConver.add(u);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
