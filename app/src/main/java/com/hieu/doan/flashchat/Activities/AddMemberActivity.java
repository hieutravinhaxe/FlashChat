package com.hieu.doan.flashchat.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.hieu.doan.flashchat.Adapters.FriendsAdapter;
import com.hieu.doan.flashchat.Models.Friends;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;

import java.util.ArrayList;

public class AddMemberActivity extends AppCompatActivity {

    private ImageView imageViewAvatarGroup, exitGroup;
    private TextView tvGroupName;
    private RecyclerView recyclerView;
    private ArrayList<Friends> listMember;
    private FriendsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_member);

        imageViewAvatarGroup = findViewById(R.id.groupAvatar);
        exitGroup = findViewById(R.id.exitGroup);
        exitGroup.setVisibility(View.GONE);//dùng chung exit với phần member nên ẩn đi
        tvGroupName = findViewById(R.id.groupNameListMember);
        recyclerView = findViewById(R.id.RCVListMember);
        listMember = new ArrayList<Friends>();
        adapter = new FriendsAdapter(this, listMember);
        recyclerView.setAdapter(adapter);

        tvGroupName.setText(getIntent().getStringExtra("groupName"));
        if(getIntent().getStringExtra("groupImage").equals("undefined")){
            imageViewAvatarGroup.setImageResource(R.drawable.teamwork);
        }
        else{
            Glide.with(this).load(getIntent().getStringExtra("groupImage")).into(imageViewAvatarGroup);
        }

    }
}