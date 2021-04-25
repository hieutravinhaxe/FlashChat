package com.hieu.doan.flashchat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.hieu.doan.flashchat.Adapters.GroupMessagesAdapter;
import com.hieu.doan.flashchat.Adapters.MessagesAdapter;
import com.hieu.doan.flashchat.Models.Message;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {

    private ArrayList<Message> messages;
    private GroupMessagesAdapter adapter;
    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private ProgressDialog dialog, dialog1;
    private EditText msgBox;
    private ImageView sendBtn, imageGroup, sendImage,sendFile, btnAddMember, btnListMember;
    private TextView groupName;
    String sendID, groupID, imageUri, senderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        msgBox = findViewById(R.id.msgBoxGroup);
        sendBtn = findViewById(R.id.sendBtnGroup);
        groupName = findViewById(R.id.title);
        recyclerView = findViewById(R.id.recyclerViewGroup);
        sendImage = findViewById(R.id.imageSendGroup);
        imageGroup = findViewById(R.id.imageAvatar);
        btnAddMember = findViewById(R.id.btnAddMember);
        btnListMember = findViewById(R.id.btnListMember);
        sendFile = findViewById(R.id.attachGroup);

        messages = new ArrayList<>();
        sendID = FirebaseAuth.getInstance().getUid();
        adapter = new GroupMessagesAdapter(this, messages, getIntent().getStringExtra("groupId"));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        //groupID sẽ được truyền qua intent
        Intent t = getIntent();
        groupID = t.getStringExtra("groupId");
        groupName.setText(t.getStringExtra("groupName"));

        imageUri = t.getStringExtra("imageUri");
        if(imageUri.equals("undefined")){
            imageGroup.setImageResource(R.drawable.teamwork);
        }
        else{

            Glide.with(this).load(imageUri).into(imageGroup);
        }

        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang tải hình lên");
        dialog.setCancelable(false);

        dialog1 = new ProgressDialog(this);
        dialog1.setCancelable(false);
        dialog1.setMessage("Đang tải file lên");

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        database.getReference()
                .child("users")
                .child(sendID)
                .child("name")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            senderName = task.getResult().getValue().toString();
                        }
                    }
                });

        database.getReference()
                .child("public")
                .child(groupID)
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

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1234);
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
                    Message message = new Message(msgText,sendID, date.getTime());
                    message.setSenderName(senderName);
                    msgBox.setText("");

                    HashMap<String, Object> lastMsgObj = new HashMap<>();
                    lastMsgObj.put("lastMsg", message.getMsg());
                    lastMsgObj.put("lastMsgTime", message.getTimestamp());

                    database.getReference().child("public").child(groupID).updateChildren(lastMsgObj);

                    database.getReference()
                            .child("public")
                            .child(groupID)
                            .child("messages")
                            .push()
                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    recyclerView.scrollToPosition(adapter.getItemCount()==0?0:adapter.getItemCount()-1);
                }
            }
        });

        sendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 1212);
            }
        });

        btnListMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(getApplicationContext(), ListMemberActivity.class);
                t.putExtra("groupName", groupName.getText().toString());
                t.putExtra("groupImage", imageUri);
                t.putExtra("groupID", groupID);
                startActivity(t);
            }
        });

        btnAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddMemberActivity.class);
                intent.putExtra("groupName", groupName.getText().toString());
                intent.putExtra("groupImage", imageUri);
                intent.putExtra("groupID", groupID);
                startActivity(intent);
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
                                        message.setSenderName(senderName);
                                        message.setFileUri(pathFile);
                                        message.setFileName(fileName);
                                        message.setMsg("file123456hvcseblhvjblasfv");

                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMsg", message.getMsg().equals("file123456hvcseblhvjblasfv")?"File":message.getMsg());
                                        lastMsgObj.put("lastMsgTime", message.getTimestamp());

                                        database.getReference().child("public").child(groupID).updateChildren(lastMsgObj);

                                        database.getReference()
                                                .child("public")
                                                .child(groupID)
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
                                        message.setSenderName(senderName);
                                        message.setImageUri(pathImage);
                                        message.setMsg("photofefededeofkt");

                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMsg", message.getMsg().equals("photofefededeofkt")?"Hình ảnh":message.getMsg());
                                        lastMsgObj.put("lastMsgTime", message.getTimestamp());

                                        database.getReference().child("public").child(groupID).updateChildren(lastMsgObj);

                                        database.getReference()
                                                .child("public")
                                                .child(groupID)
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
                        }
                    });
                }
            }
        }
        recyclerView.scrollToPosition(adapter.getItemCount()-1);
        msgBox.setText("");
    }
}