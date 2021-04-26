package com.hieu.doan.flashchat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieu.doan.flashchat.Models.Friends;
import com.hieu.doan.flashchat.Models.Message;
import com.hieu.doan.flashchat.R;

import java.util.ArrayList;

public class GroupMessagesAdapter extends RecyclerView.Adapter {

    final int SEND = 1;
    final int RECEIVE = 2;

    Context context;
    ArrayList<Message> messages;
    String GroupId;


    public GroupMessagesAdapter(Context context, ArrayList<Message> messagesList, String groupId) {
        this.context = context;
        this.messages = messagesList;
        this.GroupId = groupId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.item_send, parent, false);
            return new SendViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_receive_group, parent, false);
            return new ReceiveViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message m = messages.get(position);
        if(m.getSenderID().equals(FirebaseAuth.getInstance().getUid())){
            return  SEND;
        }
        else{
            return RECEIVE;
        }
        //return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Message message = messages.get(position);
        if(holder.getClass() == SendViewHolder.class){
            SendViewHolder sendViewHolder = (SendViewHolder) holder;
            if(message.getMsg().equals("photofefededeofkt")){
                sendViewHolder.attachFileSend.setVisibility(View.GONE);
                sendViewHolder.sendTV.setVisibility(View.GONE);
                sendViewHolder.image.setVisibility(View.VISIBLE);
                Glide.with(context).load(message.getImageUri()).into(sendViewHolder.image);
            }
            else if(message.getMsg().equals("file123456hvcseblhvjblasfv")) {
                sendViewHolder.sendTV.setVisibility(View.GONE);
                sendViewHolder.image.setVisibility(View.GONE);
                sendViewHolder.attachFileSend.setVisibility(View.VISIBLE);
                sendViewHolder.attachFileSend.setText(message.getFileName());
            }
            else{
                sendViewHolder.sendTV.setVisibility(View.VISIBLE);
                sendViewHolder.image.setVisibility(View.GONE);
                sendViewHolder.attachFileSend.setVisibility(View.GONE);
                sendViewHolder.sendTV.setText(message.getMsg());
            }
        }
        else{
            ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
            if(message.getMsg().equals("photofefededeofkt")){
                receiveViewHolder.receiveTV.setVisibility(View.GONE);
                receiveViewHolder.attachFileReceive.setVisibility(View.GONE);
                receiveViewHolder.senderName.setVisibility(View.VISIBLE);
                receiveViewHolder.image.setVisibility(View.VISIBLE);
                receiveViewHolder.senderName.setText(message.getSenderName());
                Glide.with(context).load(message.getImageUri()).into(receiveViewHolder.image);
            }else if(message.getMsg().equals("file123456hvcseblhvjblasfv")){
                receiveViewHolder.receiveTV.setVisibility(View.GONE);
                receiveViewHolder.attachFileReceive.setVisibility(View.VISIBLE);
                receiveViewHolder.senderName.setVisibility(View.VISIBLE);
                receiveViewHolder.image.setVisibility(View.GONE);
                receiveViewHolder.senderName.setText(message.getSenderName());
                receiveViewHolder.attachFileReceive.setText(message.getFileName());
            }
            else{
                receiveViewHolder.receiveTV.setVisibility(View.VISIBLE);
                receiveViewHolder.attachFileReceive.setVisibility(View.GONE);
                receiveViewHolder.senderName.setVisibility(View.VISIBLE);
                receiveViewHolder.image.setVisibility(View.GONE);
                receiveViewHolder.senderName.setText(message.getSenderName());
                receiveViewHolder.receiveTV.setText(message.getMsg());
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SendViewHolder extends RecyclerView.ViewHolder{
        private TextView sendTV, attachFileSend;
        private ImageView image;

        public SendViewHolder (View itemView){
            super(itemView);

            attachFileSend = itemView.findViewById(R.id.attachFile);
            sendTV = itemView.findViewById(R.id.SendTextView);
            image = itemView.findViewById(R.id.imageSend);
        }
    }

    public class ReceiveViewHolder extends RecyclerView.ViewHolder{
        private TextView receiveTV, senderName, attachFileReceive;
        private ImageView image;

        public ReceiveViewHolder (View itemView){
            super(itemView);

            attachFileReceive = itemView.findViewById(R.id.fileReceiveGroup);
            senderName = itemView.findViewById(R.id.senderName);
            receiveTV = itemView.findViewById(R.id.ReceiveTextView);
            image = itemView.findViewById(R.id.imageRecieve);
        }
    }
}
