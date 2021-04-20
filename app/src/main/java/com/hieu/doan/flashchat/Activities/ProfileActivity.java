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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hieu.doan.flashchat.R;
import com.hieu.doan.flashchat.Models.User;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ImageView imageView;
    EditText name;
    Button btnCon;
    Uri LinkImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        imageView = findViewById(R.id.imageView);
        name = findViewById(R.id.nameEditText);
        btnCon = findViewById(R.id.btnContinue);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent();
                t.setAction(Intent.ACTION_GET_CONTENT);
                t.setType("image/*");
                startActivityForResult(t, 40);
            }
        });

        btnCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fname = name.getText().toString();
                if(fname.isEmpty()){
                    Toast.makeText(ProfileActivity.this, "Vui lòng điền tên người dùng", Toast.LENGTH_SHORT).show();
                    return;
                }
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
                                        Intent t = getIntent();
                                        String phone = t.getStringExtra("phoneNumber");
                                        String userName = name.getText().toString();
                                        String email = auth.getCurrentUser().getEmail();

                                        User user = new User(uId,email, phone, imageUri, userName);
                                        database.getReference()
                                                .child("users")
                                                .child(uId)
                                                .setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                }
                else{
                    final String uId = auth.getUid();
                    String phone = auth.getCurrentUser().getPhoneNumber();
                    String userName = name.getText().toString();
                    String email = auth.getCurrentUser().getEmail();

                    User user = new User(uId,email, phone, "undefined", userName);
                    database.getReference()
                            .child("users")
                            .child(uId)
                            .setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                                    finish();
                                }
                            });
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
}