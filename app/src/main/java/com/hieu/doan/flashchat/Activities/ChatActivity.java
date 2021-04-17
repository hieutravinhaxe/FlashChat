package com.hieu.doan.flashchat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.hieu.doan.flashchat.Adapters.MessagesAdapter;
import com.hieu.doan.flashchat.Models.Message;
import com.hieu.doan.flashchat.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    private ImageView imageView, sendBtn, sendImage, attachBtn;
    private TextView textView;
    private EditText msgBox;
    private RecyclerView recyclerView;
    private ArrayList<Message> messages;
    private MessagesAdapter adapter;
    ProgressDialog dialog, dialog1;
    FirebaseDatabase database;
    FirebaseStorage storage;

    String sendRoom, receiveRoom, sendID, receiveID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        imageView = findViewById(R.id.imageAvatar);
        textView  = findViewById(R.id.nameAvatar);
        recyclerView = findViewById(R.id.recyclerView);
        sendBtn = findViewById(R.id.sendBtn);
        msgBox = findViewById(R.id.msgBox);
        sendImage = findViewById(R.id.image);
        attachBtn = findViewById(R.id.attach);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang tải hình lên");
        dialog.setCancelable(false);

        dialog1 = new ProgressDialog(this);
        dialog1.setCancelable(false);
        dialog1.setMessage("Đang tải file lên");

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        messages = new ArrayList<>();
        adapter = new MessagesAdapter(this, messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount()-1);

        String name = getIntent().getStringExtra("name");
        receiveID = getIntent().getStringExtra("uID");
        sendID = FirebaseAuth.getInstance().getUid();
        String image = getIntent().getStringExtra("image");

        sendRoom = sendID + receiveID;
        receiveRoom = receiveID + sendID;

        database.getReference()
                .child("chats")
                .child(sendRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            Message message = dataSnapshot.getValue(Message.class);
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(adapter.getItemCount()-1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        textView.setText(name);
        if(image.equals("undefined")){
            imageView.setImageResource(R.drawable.profile);
        }
        else{
            Glide.with(this).load(image).into(imageView);
        }

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1234);
            }
        });

        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent,1212);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgText = msgBox.getText().toString();
                if(msgText.equals("")){

                }
                else{
                    Date date = new Date();
                    final Message message = new Message(msgText, sendID, date.getTime());

                    HashMap<String, Object> lastMsgObj = new HashMap<>();
                    lastMsgObj.put("lastMsg", message.getMsg());
                    lastMsgObj.put("lastMsgTime", message.getTimestamp());

                    database.getReference().child("chats").child(sendRoom).updateChildren(lastMsgObj);
                    database.getReference().child("chats").child(receiveRoom).updateChildren(lastMsgObj);

                    database.getReference()
                            .child("chats")
                            .child(sendRoom)
                            .child("messages")
                            .push()
                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            database.getReference()
                                    .child("chats")
                                    .child(receiveRoom)
                                    .child("messages")
                                    .push()
                                    .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    msgBox.setText("");
                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1212){
            if(data != null){
                if(data.getData() != null){
                    Uri file = data.getData();
                    final String f = file.getLastPathSegment();
                    final  String fileName = f.substring(f.lastIndexOf("/")+1);
                    Calendar calendar = Calendar.getInstance();
                    final StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis()+"");
                    dialog1.show();
                    reference.putFile(file).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                dialog1.dismiss();
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String pathFile = uri.toString();
                                        String msgText = msgBox.getText().toString();

                                        msgBox.setText("");
                                        Date date = new Date();
                                        final Message message = new Message(msgText, sendID, date.getTime());
                                        message.setFileUri(pathFile);
                                        message.setFileName(fileName);
                                        message.setMsg("file123456hvcseblhvjblasfv");

                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMsg", message.getMsg().equals("file123456hvcseblhvjblasfv")?"File":message.getMsg());
                                        lastMsgObj.put("lastMsgTime", message.getTimestamp());

                                        database.getReference().child("chats").child(sendRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("chats").child(receiveRoom).updateChildren(lastMsgObj);

                                        database.getReference()
                                                .child("chats")
                                                .child(sendRoom)
                                                .child("messages")
                                                .push()
                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                database.getReference()
                                                        .child("chats")
                                                        .child(receiveRoom)
                                                        .child("messages")
                                                        .push()
                                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        adapter.notifyDataSetChanged();
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
            }
        }

        if(requestCode == 1234){
            if(data != null){
                if(data.getData() != null){
                    Uri selectImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    final StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis()+ "");
                    dialog.show();
                    reference.putFile(selectImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                dialog.dismiss();
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String pathImage = uri.toString();
                                        String msgText = msgBox.getText().toString();

                                        msgBox.setText("");
                                        Date date = new Date();
                                        final Message message = new Message(msgText, sendID, date.getTime());
                                        message.setImageUri(pathImage);
                                        message.setMsg("photofefededeofkt");

                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMsg", message.getMsg().equals("photofefededeofkt")?"Hình ảnh":message.getMsg());
                                        lastMsgObj.put("lastMsgTime", message.getTimestamp());

                                        database.getReference().child("chats").child(sendRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("chats").child(receiveRoom).updateChildren(lastMsgObj);

                                        database.getReference()
                                                .child("chats")
                                                .child(sendRoom)
                                                .child("messages")
                                                .push()
                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    database.getReference()
                                                            .child("chats")
                                                            .child(receiveRoom)
                                                            .child("messages")
                                                            .push()
                                                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                                adapter.notifyDataSetChanged();
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
            }
        }
        recyclerView.scrollToPosition(adapter.getItemCount()-1);
        msgBox.setText("");
    }
}