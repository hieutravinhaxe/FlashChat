package com.hieu.doan.flashchat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.hieu.doan.flashchat.Activities.AddMemberActivity;
import com.hieu.doan.flashchat.Activities.ChatActivity;
import com.hieu.doan.flashchat.Models.Friends;
import com.hieu.doan.flashchat.R;

import java.util.ArrayList;
import java.util.List;

public class ListFriendsAddToGroup extends RecyclerView.Adapter<ListFriendsAddToGroup.ViewHolder> {
    private Context context;
    ArrayList<Friends> friends;
    private String idGroup;

    public ListFriendsAddToGroup(Context context, ArrayList<Friends> friends, String idGroup){
        this.context = context;
        this.friends = friends;
        this.idGroup = idGroup;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_row, parent, false);
        return new ListFriendsAddToGroup.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Friends friend = friends.get(position);
        holder.textView.setText(friend.getName());
        if(friend.getImage().equals("undefined")){
            holder.imageView.setImageResource(R.drawable.profile);
        }
        else{
            Glide.with(context).load(friend.getImage()).into(holder.imageView);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friends.remove(friend);
                FirebaseDatabase.getInstance().getReference()
                        .child("groups")
                        .child(idGroup)
                        .child("members")
                        .child(friend.getId())
                        .setValue(friend)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Đã thêm thành viên mới vào nhóm", Toast.LENGTH_SHORT).show();
                            }
                        });

                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
