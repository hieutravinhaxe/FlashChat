package com.hieu.doan.flashchat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hieu.doan.flashchat.Activities.ChatActivity;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;

import java.util.List;

public class ListConverAdapter extends RecyclerView.Adapter<ListConverAdapter.ViewHolder> {
    private Context context;
    List<User> users;

    public ListConverAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.simple_conversation_1,parent, false);
        return new ListConverAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.username.setText(user.getName());
        if(user.getImage().equals("undefined")){
            holder.imageView.setImageResource(R.drawable.profile);
        }
        else{
            Glide.with(context).load(user.getImage()).into(holder.imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(context, ChatActivity.class);
                t.putExtra("name", user.getName());
                t.putExtra("uID", user.getId());
                t.putExtra("image", user.getImage());
                context.startActivity(t);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView username;
        private ImageView imageView;

        public ViewHolder (View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.converName);
            imageView = itemView.findViewById(R.id.imageConver);
        }
    }
}
