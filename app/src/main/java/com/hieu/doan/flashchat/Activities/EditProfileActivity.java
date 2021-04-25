package com.hieu.doan.flashchat.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;

import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {

    private TextView emailTextView;
    private EditText nameEditText, phoneEditText;
    private ImageView imageView, btnBack;
    private Button btnSave;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private ArrayList<User> user;
    Uri LinkImage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameEditText = findViewById(R.id.nameEditText);
        emailTextView = findViewById(R.id.emailTextView);
        phoneEditText = findViewById(R.id.phoneEditText);
        imageView = findViewById(R.id.imageView);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        database.getReference().child("users").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                nameEditText.setText(user.getName());
                phoneEditText.setText(user.getPhone());
                emailTextView.setText(user.getEmail());
                if(user.getImage().equals("undefined")){
                    imageView.setImageResource(R.drawable.profile);
                }else {
                    Glide.with(EditProfileActivity.this).load(user.getImage()).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, ManagerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent();
                t.setAction(Intent.ACTION_GET_CONTENT);
                t.setType("image/*");
                startActivityForResult(t, 40);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validation()){
                    if(LinkImage != null){
                        final StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());
                        reference.putFile(LinkImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUri = uri.toString();
                                            final String uId = auth.getUid();
                                            String phone = phoneEditText.getText().toString();
                                            String userName = nameEditText.getText().toString();
                                            String email = auth.getCurrentUser().getEmail();
                                            User user = new User(uId,email, phone, imageUri, userName);
                                            database.getReference()
                                                    .child("users")
                                                    .child(auth.getUid())
                                                    .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    startActivity(new Intent(EditProfileActivity.this, ManagerActivity.class));
                                                    finish();
                                                    Toast.makeText(EditProfileActivity.this, "Chỉnh sửa thành công",
                                                            Toast.LENGTH_LONG).show();
                                                }


                                            });
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else{
                        database.getReference()
                                .child("users")
                                .child(auth.getUid())
                                .child("image").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String imageUri =  snapshot.getValue().toString();
                                final String uId = auth.getUid();
                                String phone = phoneEditText.getText().toString();
                                String userName = nameEditText.getText().toString();
                                String email = auth.getCurrentUser().getEmail();

                                User user = new User(uId,email, phone, imageUri, userName);
                                database.getReference()
                                        .child("users")
                                        .child(auth.getUid())
                                        .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(EditProfileActivity.this, ManagerActivity.class));
                                        finish();
                                    }
                                });
                                Toast.makeText(EditProfileActivity.this, "Chỉnh sửa thành công",
                                        Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!= null){
            if(data.getData() != null){
                imageView.setImageURI(data.getData());
                LinkImage = data.getData();
            }
        }
    }

    private boolean validation() {
        String phone = phoneEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Không được để trống số điện thoại", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Không được để trống Tên", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
