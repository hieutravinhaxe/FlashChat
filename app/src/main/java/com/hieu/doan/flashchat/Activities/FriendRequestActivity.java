package com.hieu.doan.flashchat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieu.doan.flashchat.Adapters.FriendRequestAdapter;
import com.hieu.doan.flashchat.Models.Friends;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;

import java.util.ArrayList;

public class FriendRequestActivity extends AppCompatActivity {
    private ImageView btnBack;
    private RecyclerView recyclerView;
    private ArrayList<User> friendRequests;
    private ArrayList<User> users;
    private FriendRequestAdapter adapter;
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerView);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendRequestActivity.this, FriendsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        friendRequests = new ArrayList<User>();
        users = new ArrayList<User>();

        adapter = new FriendRequestAdapter(this, friendRequests);
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    User u = snapshot1.getValue(User.class);
                    users.add(u);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference().child("users").child(auth.getUid()).child("friends")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        friendRequests.clear();

                        for(DataSnapshot snapshot1: snapshot.getChildren()){

                            String status = snapshot1.child("status").getValue().toString();
                            final String userID = snapshot1.getKey();

                            Log.d("chien", status);

                            if (status.equals("0")){
                                for (int i = 0; i < users.size(); i++){
                                    if(userID.equals(users.get(i).getId())){
                                        friendRequests.add(users.get(i));
                                    }
                                }
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
