package com.hieu.doan.flashchat.Adapters;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hieu.doan.flashchat.Activities.ChatActivity;
import com.hieu.doan.flashchat.Models.Message;
import com.hieu.doan.flashchat.R;

import java.io.File;
import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MessagesAdapter extends RecyclerView.Adapter {

    final int SEND = 1;
    final int RECEIVE = 2;

    Context context;
    FirebaseStorage storage = FirebaseStorage.getInstance();
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
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final Message message = messages.get(position);

        if(holder.getClass() == SendViewHolder.class){
            SendViewHolder sendViewHolder = (SendViewHolder) holder;
            if(message.getMsg().equals("photofefededeofkt")){
                sendViewHolder.sendTV.setVisibility(View.GONE);
                sendViewHolder.fileSend.setVisibility(View.GONE);
                sendViewHolder.image.setVisibility(View.VISIBLE);
                Glide.with(context).load(message.getImageUri()).into(sendViewHolder.image);
            }
            else if(message.getMsg().equals("file123456hvcseblhvjblasfv")){
                sendViewHolder.sendTV.setVisibility(View.GONE);
                sendViewHolder.image.setVisibility(View.GONE);
                sendViewHolder.fileSend.setVisibility(View.VISIBLE);
                sendViewHolder.fileSend.setText(message.getFileName());
            }
            else {
                sendViewHolder.sendTV.setVisibility(View.VISIBLE);
                sendViewHolder.image.setVisibility(View.GONE);
                sendViewHolder.fileSend.setVisibility(View.GONE);
                sendViewHolder.sendTV.setText(message.getMsg());
            }
        }
        else{
            ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
            if(message.getMsg().equals("photofefededeofkt")){
                receiveViewHolder.receiveTV.setVisibility(View.GONE);
                receiveViewHolder.fileReceive.setVisibility(View.GONE);
                receiveViewHolder.image.setVisibility(View.VISIBLE);
                Glide.with(context).load(message.getImageUri()).into(receiveViewHolder.image);
            }
            else if(message.getMsg().equals("file123456hvcseblhvjblasfv")){
                receiveViewHolder.receiveTV.setVisibility(View.GONE);
                receiveViewHolder.fileReceive.setVisibility(View.VISIBLE);
                receiveViewHolder.image.setVisibility(View.GONE);
                receiveViewHolder.fileReceive.setText(message.getFileName());
            }
            else{
                receiveViewHolder.receiveTV.setVisibility(View.VISIBLE);
                receiveViewHolder.image.setVisibility(View.GONE);
                receiveViewHolder.fileReceive.setVisibility(View.GONE);
                receiveViewHolder.receiveTV.setText(message.getMsg());
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.getItemViewType() == SEND){
                    SendViewHolder sendViewHolder = (SendViewHolder) holder;
                    if(sendViewHolder.sendTV.getVisibility() == View.VISIBLE){
                        //Log.i("CHECK","text");
                    }
                    else if(sendViewHolder.image.getVisibility() == View.VISIBLE){
                        //Log.i("CHECK","IMAGE");
                    }
                    else {
                        //Log.i("CHECK","File");
                    }
                }else{
                    ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
                    if(receiveViewHolder.receiveTV.getVisibility() == View.VISIBLE){
                        //Log.i("CHECK","text");
                    }
                    else if(receiveViewHolder.image.getVisibility() == View.VISIBLE){
                        //Log.i("CHECK",message.getImageUri());
                        //Log.i("NAME ",fName);
                        //Log.i("EX",fEx);
                        downloadFile(holder.itemView.getContext(),"image",".png",DIRECTORY_DOWNLOADS,message.getImageUri());
                    }
                    else {
                        //Log.i("CHECK",message.getFileUri());
                        String fName = message.getFileName().substring(0,message.getFileName().lastIndexOf("."));
                        String fEx =message.getFileName().substring(message.getFileName().lastIndexOf("."));
                        //Log.i("NAME ",fName);
                        //Log.i("EX",fEx);
                        downloadFile(holder.itemView.getContext(),fName,fEx,DIRECTORY_DOWNLOADS,message.getFileUri());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SendViewHolder extends RecyclerView.ViewHolder{
        private TextView sendTV;
        private ImageView image;
        private TextView fileSend;

        public SendViewHolder (View itemView){
            super(itemView);

            sendTV = itemView.findViewById(R.id.SendTextView);
            image = itemView.findViewById(R.id.imageSend);
            fileSend = itemView.findViewById(R.id.attachFile);
        }
    }

    public class ReceiveViewHolder extends RecyclerView.ViewHolder{
        private TextView receiveTV;
        private ImageView image;
        private TextView fileReceive;

        public ReceiveViewHolder (View itemView){
            super(itemView);

            receiveTV = itemView.findViewById(R.id.ReceiveTextView);
            image = itemView.findViewById(R.id.imageRecieve);
            fileReceive = itemView.findViewById(R.id.attachFileRecieve);
        }
    }
    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {

        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadmanager.enqueue(request);
    }
}
