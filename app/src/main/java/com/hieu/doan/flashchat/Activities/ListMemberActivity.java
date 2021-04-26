package com.hieu.doan.flashchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieu.doan.flashchat.Adapters.ListConverAdapter;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;

import java.util.ArrayList;

public class ListMemberActivity extends AppCompatActivity {

    private ImageView imageViewAvatarGroup, exitGroup;
    private TextView tvGroupName;
    private RecyclerView recyclerView;
    private ArrayList<User> listMember;
    private ListConverAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_member);

        imageViewAvatarGroup = findViewById(R.id.groupAvatar);
        exitGroup = findViewById(R.id.exitGroup);
        tvGroupName = findViewById(R.id.groupNameListMember);
        recyclerView = findViewById(R.id.RCVListMember);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listMember = new ArrayList<>();
        adapter = new ListConverAdapter(this, listMember);
        recyclerView.setAdapter(adapter);

        tvGroupName.setText(getIntent().getStringExtra("groupName"));
        if(getIntent().getStringExtra("groupImage").equals("undefined")){
            imageViewAvatarGroup.setImageResource(R.drawable.teamwork);
        }
        else{
            Glide.with(this).load(getIntent().getStringExtra("groupImage")).into(imageViewAvatarGroup);
        }

        exitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showVerifyDialog();
            }
        });

        FirebaseDatabase.getInstance().getReference()
                .child("groups")
                .child(getIntent().getStringExtra("groupID"))
                .child("members")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listMember.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    //Log.i("CHECK",snapshot1.getValue().toString());
                    User u  = snapshot1.getValue(User.class);
                    if(!u.getId().equals(FirebaseAuth.getInstance().getUid())){
                        listMember.add(u);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showVerifyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận");
        builder.setMessage("Bạn có muốn rời khỏi nhóm không?");
        builder.setCancelable(false);
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(MainActivity.this, "Không thoát được", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                FirebaseDatabase.getInstance().getReference()
                        .child("groups")
                        .child(getIntent().getStringExtra("groupID"))
                        .child("members")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot3) {
                                for(DataSnapshot dataSnapshot: snapshot3.getChildren()){
                                    String id = dataSnapshot.getValue().toString();
                                    if(id.equals(FirebaseAuth.getInstance().getUid())){
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("groups")
                                                .child(getIntent().getStringExtra("groupID"))
                                                .child("members")
                                                .child(dataSnapshot.getKey())
                                                .removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        startActivity(new Intent(ListMemberActivity.this,GroupsActivity.class));
                                                        finish();
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
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}