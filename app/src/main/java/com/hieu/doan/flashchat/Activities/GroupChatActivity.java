package com.hieu.doan.flashchat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {

    private ArrayList<Message> messages;
    private MessagesAdapter adapter;
    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private ProgressDialog dialog;
    private EditText msgBox;
    private ImageView sendBtn, imageGroup, sendImage;
    private TextView groupName;
    String sendID, groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        msgBox = findViewById(R.id.msgBox);
        sendBtn = findViewById(R.id.sendBtn);
        groupName = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.recyclerView);
        sendImage = findViewById(R.id.imageSend);
        sendID = FirebaseAuth.getInstance().getUid();
        //groupID sẽ được truyền qua intent

        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang tải hình lên");
        dialog.setCancelable(false);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        messages = new ArrayList<>();

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
                    Message message = new Message(msgText, sendID, date.getTime());
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
                    recyclerView.scrollToPosition(adapter.getItemCount() -1);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1234){
            if(data != null){
                if(data.getData() != null){
                    Uri selectImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    final StorageReference reference = storage.getReference().child("public").child(calendar.getTimeInMillis()+ "");
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
                                        message.setMsg("photo");

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
                                    }
                                });
                                recyclerView.scrollToPosition(adapter.getItemCount()-1);
                            }
                        }
                    });
                }
            }
        }
    }
}