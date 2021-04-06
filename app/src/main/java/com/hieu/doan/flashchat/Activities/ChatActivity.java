package com.hieu.doan.flashchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieu.doan.flashchat.Adapters.MessagesAdapter;
import com.hieu.doan.flashchat.Models.Message;
import com.hieu.doan.flashchat.R;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private ImageView imageView, sendBtn;
    private TextView textView;
    private EditText msgBox;
    private RecyclerView recyclerView;
    private ArrayList<Message> messages;
    private MessagesAdapter adapter;
    FirebaseDatabase database;

    String sendRoom, receiveRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        imageView = findViewById(R.id.imageAvatar);
        textView  = findViewById(R.id.nameAvatar);
        recyclerView = findViewById(R.id.recyclerView);
        sendBtn = findViewById(R.id.sendBtn);
        msgBox = findViewById(R.id.msgBox);

        database = FirebaseDatabase.getInstance();

        messages = new ArrayList<>();
        adapter = new MessagesAdapter(this, messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        String name = getIntent().getStringExtra("name");
        String receiveID = getIntent().getStringExtra("uID");
        final String sendID = FirebaseAuth.getInstance().getUid();
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

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgText = msgBox.getText().toString();
                if(msgText.equals("")){

                }
                else{
                    msgBox.setText("");
                    Date date = new Date();
                    final Message message = new Message(msgText, sendID, date.getTime());
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
            }
        });

    }
}