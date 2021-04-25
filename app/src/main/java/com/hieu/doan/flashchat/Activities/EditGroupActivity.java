package com.hieu.doan.flashchat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.hieu.doan.flashchat.Models.Friends;
import com.hieu.doan.flashchat.Models.Group;
import com.hieu.doan.flashchat.R;

public class EditGroupActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button btnCreate;
    private EditText groupName;
    private String imageGroup, groupID, groupTen;
    private Uri imageGroupUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        imageView = findViewById(R.id.imageGroup);
        btnCreate = findViewById(R.id.btnCreateGroup);
        groupName = findViewById(R.id.groupName);

        imageGroup = getIntent().getStringExtra("groupImage");
        groupID = getIntent().getStringExtra("groupID");
        groupTen = getIntent().getStringExtra("groupName");
        groupName.setText(groupTen);

        if(imageGroup.equals("undefined")){
            imageView.setImageResource(R.drawable.teamwork);
        }
        else {
            Glide.with(getApplicationContext()).load(imageGroup).into(imageView);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent();
                t.setAction(Intent.ACTION_GET_CONTENT);
                t.setType("image/*");
                startActivityForResult(t, 1122);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(groupName.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Chưa nhập tên group", Toast.LENGTH_SHORT).show();
                }
                else {
                    FirebaseDatabase.getInstance().getReference()
                            .child("groups")
                            .child(groupID)
                            .child("name")
                            .setValue(groupName.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        if (imageGroupUri != null) {
                                            FirebaseStorage.getInstance().getReference().child("Profiles").child(groupID).putFile(imageGroupUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        FirebaseStorage.getInstance().getReference().child("Profiles").child(groupID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                String imageUri = uri.toString();

                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child("groups")
                                                                        .child(groupID)
                                                                        .child("imageUri")
                                                                        .setValue(imageUri)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                            }
                                                                        });
                                                            }
                                                        });

                                                    }
                                                }
                                            });
                                        }
                                        startActivity(new Intent(getApplicationContext(),GroupsActivity.class));
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1122){
            if(data!= null){
                if(data.getData() != null){
                    imageView.setImageURI(data.getData());
                    imageGroupUri = data.getData();
                }
            }
        }
    }
}