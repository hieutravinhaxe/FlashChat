package com.hieu.doan.flashchat.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieu.doan.flashchat.Activities.ChatActivity;
import com.hieu.doan.flashchat.Activities.DeleteMessage;
import com.hieu.doan.flashchat.Activities.GroupsActivity;
import com.hieu.doan.flashchat.Activities.ListMemberActivity;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final User user = users.get(position);
        String senderId = FirebaseAuth.getInstance().getUid();
        final String sendRoom = senderId + user.getId();

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(sendRoom)
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

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("X??c nh???n");
                builder.setMessage("B???n c?? mu???n x??a tin nh???n kh??ng?");
                builder.setCancelable(false);
                builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Toast.makeText(MainActivity.this, "Kh??ng tho??t ???????c", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        DeleteMessage deleteMessage = new DeleteMessage(sendRoom);
                        deleteMessage.exeDelete();
                        users.remove(user);
                        notifyItemRemoved(position);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
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
