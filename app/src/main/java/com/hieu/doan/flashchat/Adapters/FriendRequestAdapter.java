package com.hieu.doan.flashchat.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieu.doan.flashchat.Activities.FriendRequestActivity;
import com.hieu.doan.flashchat.Activities.FriendsActivity;
import com.hieu.doan.flashchat.Models.Friends;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;

import java.util.HashMap;
import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {
    private Context context;
    List<User> requests;
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    public FriendRequestAdapter(Context context, List<User> requests){
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_row, parent, false);
        return new FriendRequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final User friend = requests.get(position);
        holder.textView.setText(friend.getName());
        if(friend.getImage().equals("undefined")){
            holder.imageView.setImageResource(R.drawable.profile);
        }
        else{
            Glide.with(context).load(friend.getImage()).into(holder.imageView);
        }

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Tr??? l???i l???i m???i k???t b???n");
                builder.setMessage("b???n c?? ch???p nh???n l???i m???i k???t b???n?");
                builder.setPositiveButton("?????ng ??", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        database.getReference().child("users").child(friend.getId()).child("friends").child(auth.getUid())
                                .child("status").setValue(1);

                        database.getReference().child("users").child(auth.getUid())
                                .child("friends").child(friend.getId()).child("status")
                                .setValue(1);

                        requests.remove(friend);
                        notifyItemRemoved(position);


                    }
                });

                builder.setNegativeButton("X??a", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        database.getReference().child("users")
                                .child(auth.getUid()).child("friends")
                                .child(requests.get(position).getId()).removeValue();

                        requests.remove(friend);
                        notifyItemRemoved(position);


                        Toast.makeText(context, "???? x??a y??u c???u k???t b???n", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNeutralButton("H???y",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return requests.size();
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
