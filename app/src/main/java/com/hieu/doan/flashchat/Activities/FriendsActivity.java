package com.hieu.doan.flashchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieu.doan.flashchat.Adapters.FriendsAdapter;
import com.hieu.doan.flashchat.Models.Friends;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_LONG;

public class FriendsActivity extends AppCompatActivity implements AddFriendDialog.AddfriendDialogListener {
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private ArrayList<Friends> listFriends;
    private ArrayList<User> users;
    private FriendsAdapter adapter;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private ImageView add;
    private ImageView requests;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);



        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menuFriends);
        recyclerView = findViewById(R.id.recyclerView);

        add = findViewById(R.id.add);
        requests = findViewById(R.id.requests);

        listFriends = new ArrayList<Friends>();
        users = new ArrayList<User>();

        adapter = new FriendsAdapter(this, listFriends);
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddFriendDialog();
            }
        });

        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendsActivity.this, FriendRequestActivity.class);
                startActivity(intent);
                finish();
            }
        });

        database.getReference()
                .child("users")
                .child(auth.getUid())
                .child("friends")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listFriends.clear();
                        for(DataSnapshot snapshot1: snapshot.getChildren()) {
<<<<<<< HEAD
                            String st = snapshot1.child("status").getValue().toString();

=======
                            String friend = snapshot.getValue().toString();
                            int length = friend.length();
                            String st = snapshot1.child("status").getValue().toString();
                            //char status  = friend.charAt(length - 2);
                            Log.d("chientran", String.valueOf(st));
>>>>>>> 5f5edd7c6b4824184fb91571a12e4e18fb1300f4
                            final String userID = snapshot1.getKey();
                            if (st.equals("1")){

                                database.getReference().child("users").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                            User u = dataSnapshot.getValue(User.class);
                                            if (u.getId().equals(userID)) {
                                                Friends f = new Friends(u.getName(), u.getImage(), u.getId());
                                                listFriends.add(f);
                                            }
                                        }
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            else if(st.equals("0")){
                                requests.setColorFilter(ContextCompat.getColor(FriendsActivity.this,
                                        R.color.red));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



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
                    case R.id.menuFriends:
                        //Toast.makeText(getApplicationContext(), "call", Toast.LENGTH_SHORT).show();
<<<<<<< HEAD
//                        startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
//                        overridePendingTransition(0,0);
//                        finish();
=======
                        /*startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;*/
                    case R.id.menuGroup:
                        startActivity(new Intent(getApplicationContext(), GroupsActivity.class));
                        overridePendingTransition(0,0);
                        finish();
>>>>>>> 5f5edd7c6b4824184fb91571a12e4e18fb1300f4
                        return true;
                    case R.id.menuManager:
                        //Toast.makeText(getApplicationContext(), "manager", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), ManagerActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });


    }

    private void openAddFriendDialog() {
        AddFriendDialog addFriendDialog = new AddFriendDialog();

        addFriendDialog.show(getSupportFragmentManager(), "Add friend dialog");
    }

    @Override
    public void applyText(final String email) {

        database.getReference()
                .child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snapshot1: snapshot.getChildren()){
                            User u = snapshot1.getValue(User.class);
                            if(email.equals(u.getEmail())){
                                database.getReference()
                                        .child("users")
                                        .child(auth.getUid())
                                        .child("friends")
                                        .child(u.getId())
                                        .child("status")
                                        .setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(FriendsActivity.this, "Has sent a friend request", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else {
                                Toast.makeText(FriendsActivity.this, "Email does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}
