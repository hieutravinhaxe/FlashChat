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
import android.util.Log;
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
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.call_api.calling.CallingActivity;
import com.hieu.doan.flashchat.call_api.calling.Utils;
import com.hieu.doan.flashchat.Adapters.MessagesAdapter;
import com.hieu.doan.flashchat.Models.Message;
import com.hieu.doan.flashchat.R;
import com.hieu.doan.flashchat.call_api.notification.Service.MyResponse;
import com.stringee.StringeeClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    private ImageView imageView, sendBtn, sendImage, attachBtn, btnback, callVideo;
    private TextView textView;
    private EditText msgBox;
    private RecyclerView recyclerView;
    private ArrayList<Message> messages =new ArrayList<>();
    private MessagesAdapter adapter;
    ProgressDialog dialog, dialog1;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Boolean isCalling = false;
    String sendRoom, receiveRoom, sendID, receiveID, name, image;
    User userCurrent = MainActivity.userCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        isCalling = false;
        imageView = findViewById(R.id.imageAvatar);
        btnback = findViewById(R.id.btnBack);
        textView = findViewById(R.id.title);
        recyclerView = findViewById(R.id.recyclerView);
        sendBtn = findViewById(R.id.sendBtn);
        msgBox = findViewById(R.id.msgBox);
        sendImage = findViewById(R.id.image);
        attachBtn = findViewById(R.id.attach);
        callVideo = findViewById(R.id.callVideo);

        dialog = new ProgressDialog(this);
        dialog.setMessage("??ang t???i h??nh l??n");
        dialog.setCancelable(false);

        dialog1 = new ProgressDialog(this);
        dialog1.setCancelable(false);
        dialog1.setMessage("??ang t???i file l??n");

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        adapter = new MessagesAdapter(this, messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);

        name = getIntent().getStringExtra("name");
        receiveID = getIntent().getStringExtra("uID");
        if (receiveID == null || receiveID.isEmpty()) {
            receiveID = getIntent().getStringExtra("receiveID");
        }
        sendID = FirebaseAuth.getInstance().getUid();
        if (sendID.isEmpty() || sendID == null) {
            sendID = getIntent().getStringExtra("sendID");
        }
        image = getIntent().getStringExtra("image");
        Log.d("chatchit", name + "//" + receiveID + "//" + sendID + "//" + image);

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
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Message message = dataSnapshot.getValue(Message.class);
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        textView.setText(name);
        try {
            if (image.equals("undefined")) {
                imageView.setImageResource(R.drawable.profile);
            } else {
                Glide.with(this).load(image).into(imageView);
            }
        } catch (Exception e) {

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
                startActivityForResult(intent, 1212);
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgText = msgBox.getText().toString();
                Log.d("token send",msgText );
                if (msgText.equals("")) {

                } else {
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

                                    database.getReference("users").child(receiveID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            User u = snapshot.getValue(User.class);
                                            try {
                                                MyResponse.sendNotifications(u.getToken(), "B???n c?? tin nh???n m???i t??? " + userCurrent.getName(), msgText);
                                            }
                                            catch (Exception ex){

                                            }
                                            Log.d("token send", u.getToken());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });
                        }
                    });
                    msgBox.setText("");
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                }
            }
        });

        callVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringeeClient client = MainActivity.client;
                if (client.isConnected()) {
                    Intent intent = new Intent(ChatActivity.this, CallingActivity.class);
                    intent.putExtra("from", client.getUserId());
                    intent.putExtra("to", receiveID);
                    intent.putExtra("is_video_call", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    //Put for chat
                    intent.putExtra("name", name);
                    intent.putExtra("receiveID", receiveID);
                    intent.putExtra("sendID", sendID);
                    intent.putExtra("image", image);
                    startActivity(intent);
                } else {
                    Utils.reportMessage(ChatActivity.this, "Kh??ng th??? k???t n???i video call. Vui l??ng th??? l???i sau!");
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1212) {
            if (data != null) {
                if (data.getData() != null) {
                    Uri file = data.getData();
                    final String f = file.getLastPathSegment();
                    final String fileName = f.substring(f.lastIndexOf("/") + 1);
                    Calendar calendar = Calendar.getInstance();
                    final StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                    dialog1.show();
                    reference.putFile(file).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
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
                                        lastMsgObj.put("lastMsg", message.getMsg().equals("file123456hvcseblhvjblasfv") ? "File" : message.getMsg());
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

        if (requestCode == 1234) {
            if (data != null) {
                if (data.getData() != null) {
                    Uri selectImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    final StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                    dialog.show();
                    reference.putFile(selectImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
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
                                        lastMsgObj.put("lastMsg", message.getMsg().equals("photofefededeofkt") ? "H??nh ???nh" : message.getMsg());
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
                                                //Send notification
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

        if (requestCode == 999) {
            isCalling = true;
        }
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        msgBox.setText("");
    }
}