package com.hieu.doan.flashchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieu.doan.flashchat.Adapters.FriendsAdapter;
import com.hieu.doan.flashchat.Adapters.ListFriendsAddToGroup;
import com.hieu.doan.flashchat.Models.Friends;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;

import java.util.ArrayList;

public class AddMemberActivity extends AppCompatActivity {

    private ImageView imageViewAvatarGroup, exitGroup;
    private TextView tvGroupName;
    private RecyclerView recyclerView;
    private ArrayList<Friends> listFriends;
    private ListFriendsAddToGroup adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_member);

        imageViewAvatarGroup = findViewById(R.id.groupAvatar);
        exitGroup = findViewById(R.id.exitGroup);
        exitGroup.setVisibility(View.GONE);//dùng chung exit với phần member nên ẩn đi
        tvGroupName = findViewById(R.id.groupNameListMember);
        recyclerView = findViewById(R.id.RCVListMember);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listFriends = new ArrayList<Friends>();
        adapter = new ListFriendsAddToGroup(this, listFriends,getIntent().getStringExtra("groupID"));
        recyclerView.setAdapter(adapter);

        tvGroupName.setText(getIntent().getStringExtra("groupName"));
        if(getIntent().getStringExtra("groupImage").equals("undefined")){
            imageViewAvatarGroup.setImageResource(R.drawable.teamwork);
        }
        else{
            Glide.with(this).load(getIntent().getStringExtra("groupImage")).into(imageViewAvatarGroup);
        }

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("friends")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listFriends.clear();
                        for(DataSnapshot snapshot1: snapshot.getChildren()) {
                            String status = snapshot1.child("status").getValue().toString();
                            String friend = snapshot.getValue().toString();

                            final String userID = snapshot1.getKey();
                            if (status.equals("1")){

                                FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                        for(DataSnapshot dataSnapshot: snapshot2.getChildren()){
                                            User u = dataSnapshot.getValue(User.class);
                                            if (u.getId().equals(userID)) {
                                                final Friends f = new Friends(u.getName(), u.getImage(), u.getId(), u.getEmail());
                                                //kiểm tra tiếp có trong nhóm chưa.
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("groups")
                                                        .child(getIntent().getStringExtra("groupID"))
                                                        .child("members")
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot11) {
                                                                boolean check =false;
                                                                for(DataSnapshot snapshot22: snapshot11.getChildren()){
                                                                    Friends c = snapshot22.getValue(Friends.class);
                                                                    if(c.getId().equals(f.getId())){
                                                                        check = true;
                                                                    }
                                                                }
                                                                if (!check) {
                                                                    listFriends.add(f);
                                                                }
                                                                adapter.notifyDataSetChanged();
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}