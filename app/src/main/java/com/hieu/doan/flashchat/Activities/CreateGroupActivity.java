package com.hieu.doan.flashchat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hieu.doan.flashchat.Models.Friends;
import com.hieu.doan.flashchat.Models.Group;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;

import java.util.Calendar;

public class CreateGroupActivity extends AppCompatActivity {
    private Uri imageGroupUri;
    private ImageView imageView;
    private Button btnCreate;
    private EditText groupName;
    private Calendar calendar = Calendar.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        imageView = findViewById(R.id.imageGroup);
        btnCreate = findViewById(R.id.btnCreateGroup);
        groupName = findViewById(R.id.groupName);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Wait a moment");

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(groupName.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Chưa nhập tên group", Toast.LENGTH_SHORT).show();
                }
                else{
                    dialog.show();
                    final String groupId = calendar.getTimeInMillis()+"";
                    final StorageReference reference = storage.getReference().child("Profiles").child(groupId);
                    if(imageGroupUri != null){
                        reference.putFile(imageGroupUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUri = uri.toString();

                                            Group g = new Group(groupId, groupName.getText().toString(), imageUri);

                                            database.getReference()
                                                    .child("groups")
                                                    .child(g.getId())
                                                    .setValue(g)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Friends f = new Friends(auth.getCurrentUser().getDisplayName(), null, auth.getUid(),null);
                                                            database.getReference().child("groups")
                                                                    .child(groupId)
                                                                    .child("members")
                                                                    .child(auth.getUid())
                                                                    .setValue(f)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            dialog.dismiss();
                                                                            startActivity(new Intent(CreateGroupActivity.this, GroupsActivity.class));
                                                                            finish();
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else {
                        Group g = new Group(groupId, groupName.getText().toString(), "undefined");

                        database.getReference()
                                .child("groups")
                                .child(g.getId())
                                .setValue(g)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Friends f = new Friends(auth.getCurrentUser().getDisplayName(), null, auth.getUid(),null);
                                        database.getReference().child("groups")
                                                .child(groupId)
                                                .child("members")
                                                .child(auth.getUid())
                                                .setValue(f)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        dialog.dismiss();
                                                        startActivity(new Intent(CreateGroupActivity.this, GroupsActivity.class));
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                    }
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent();
                t.setAction(Intent.ACTION_GET_CONTENT);
                t.setType("image/*");
                startActivityForResult(t, 1122);
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