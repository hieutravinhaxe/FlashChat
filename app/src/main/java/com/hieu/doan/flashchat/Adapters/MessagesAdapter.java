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
import com.google.firebase.auth.FirebaseAuth;
import com.hieu.doan.flashchat.Activities.ChatActivity;
import com.hieu.doan.flashchat.Models.Message;
import com.hieu.doan.flashchat.R;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {

    final int SEND = 1;
    final int RECEIVE = 2;

    Context context;
    ArrayList<Message> messages;


    public MessagesAdapter(Context context, ArrayList<Message> messagesList) {
        this.context = context;
        this.messages = messagesList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.item_send, parent, false);
            return new SendViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_receive, parent, false);
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
        Message message = messages.get(position);

        if(holder.getClass() == SendViewHolder.class){
            SendViewHolder sendViewHolder = (SendViewHolder) holder;
            if(message.getMsg().equals("photofefededeofkt")){
                sendViewHolder.sendTV.setVisibility(View.GONE);
                sendViewHolder.image.setVisibility(View.VISIBLE);
                Glide.with(context).load(message.getImageUri()).into(sendViewHolder.image);
            }
            else {
                sendViewHolder.sendTV.setVisibility(View.VISIBLE);
                sendViewHolder.image.setVisibility(View.GONE);
                sendViewHolder.sendTV.setText(message.getMsg());
            }
        }
        else{
            ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
            if(message.getMsg().equals("photofefededeofkt")){
                receiveViewHolder.receiveTV.setVisibility(View.GONE);
                receiveViewHolder.image.setVisibility(View.VISIBLE);
                Glide.with(context).load(message.getImageUri()).into(receiveViewHolder.image);
            }else{
                receiveViewHolder.receiveTV.setVisibility(View.VISIBLE);
                receiveViewHolder.image.setVisibility(View.GONE);
                receiveViewHolder.receiveTV.setText(message.getMsg());
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SendViewHolder extends RecyclerView.ViewHolder{
        private TextView sendTV;
        private ImageView image;

        public SendViewHolder (View itemView){
            super(itemView);

            sendTV = itemView.findViewById(R.id.SendTextView);
            image = itemView.findViewById(R.id.imageSend);
        }
    }

    public class ReceiveViewHolder extends RecyclerView.ViewHolder{
        private TextView receiveTV;
        private ImageView image;

        public ReceiveViewHolder (View itemView){
            super(itemView);

            receiveTV = itemView.findViewById(R.id.ReceiveTextView);
            image = itemView.findViewById(R.id.imageRecieve);
        }
    }
}
