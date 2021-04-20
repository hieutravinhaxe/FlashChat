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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieu.doan.flashchat.Activities.ChatActivity;
import com.hieu.doan.flashchat.Activities.GroupChatActivity;
import com.hieu.doan.flashchat.Models.Group;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {
    private Context context;
    List<Group> groups;

    public GroupsAdapter(Context context, List<Group> groups) {
        this.context = context;
        this.groups = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.simple_conversation_1,parent, false);
        return new GroupsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Group group = groups.get(position);
        String senderId = FirebaseAuth.getInstance().getUid();

        holder.username.setText(group.getName());

        FirebaseDatabase.getInstance().getReference()
                .child("public")
                .child(group.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            try{
                                String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                                long lastMsgTime = snapshot.child("lastMsgTime").getValue(Long.class);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");

                                holder.textMsg.setText(lastMsg);
                                holder.lastModify.setText(dateFormat.format(new Date(lastMsgTime)));
                            }
                            catch (NullPointerException e){
                                throw e;
                            }
                        }
                        else{
                            holder.textMsg.setText("");
                            holder.lastModify.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        if(group.getImageUri().equals("undefined")){
            holder.imageView.setImageResource(R.drawable.teamwork);
        }
        else{
            Glide.with(context).load(group.getImageUri()).into(holder.imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, group.getId(), Toast.LENGTH_SHORT).show();
                Intent tt = new Intent(context, GroupChatActivity.class);
                tt.putExtra("groupId",group.getId());
                tt.putExtra("groupName", group.getName());
                tt.putExtra("imageUri", group.getImageUri());
                context.startActivity(tt);
            }
        });

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView username, textMsg, lastModify;
        private ImageView imageView;

        public ViewHolder (View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.converName);
            imageView = itemView.findViewById(R.id.imageConver);
            textMsg = itemView.findViewById(R.id.textMsg);
            lastModify = itemView.findViewById(R.id.lastModify);
        }
    }
}
