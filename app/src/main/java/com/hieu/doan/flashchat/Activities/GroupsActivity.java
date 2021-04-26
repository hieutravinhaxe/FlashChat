package com.hieu.doan.flashchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieu.doan.flashchat.Adapters.GroupsAdapter;
import com.hieu.doan.flashchat.Models.Friends;
import com.hieu.doan.flashchat.Models.Group;
import com.hieu.doan.flashchat.R;

import java.util.ArrayList;


public class GroupsActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private GroupsAdapter adapter;
    private ArrayList<Group> groups;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        floatingActionButton = findViewById(R.id.floatingBtnAdd);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menuGroup);
        recyclerView = findViewById(R.id.recyclerView);

        groups = new ArrayList<Group>();
        adapter = new GroupsAdapter(this, groups);
        recyclerView.setAdapter(adapter);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading your groups...");
        dialog.show();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateGroupActivity.class));
                overridePendingTransition(0,0);
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
                        startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.menuGroup:
                        /*startActivity(new Intent(getApplicationContext(), GroupsActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;*/
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

        database.getReference().child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                groups.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    final Group g = snapshot1.getValue(Group.class);
                    //check user is a most of group's members
                    database.getReference()
                            .child("groups")
                            .child(g.getId())
                            .child("members")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot17) {
                                    for(DataSnapshot snapshot12: snapshot17.getChildren()){
                                        Friends t = snapshot12.getValue(Friends.class);
                                        if(t.getId().equals(FirebaseAuth.getInstance().getUid())){
                                            groups.add(g);
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog.dismiss();
    }
}