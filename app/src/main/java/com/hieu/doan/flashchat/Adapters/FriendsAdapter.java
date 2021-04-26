package com.hieu.doan.flashchat.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hieu.doan.flashchat.Activities.ChatActivity;
import com.hieu.doan.flashchat.Activities.FriendsActivity;
import com.hieu.doan.flashchat.Models.Friends;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    private Context context;
    List<User> friends;
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    public FriendsAdapter(Context context, List<User> friends){
        this.context = context;
        this.friends = friends;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_row, parent, false);
        return new FriendsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final User friend = friends.get(position);
        holder.textView.setText(friend.getName());
        holder.imageView.setImageResource(R.drawable.profile);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(context, ChatActivity.class);
                t.putExtra("name", friend.getName());
                t.putExtra("uID", friend.getId());
                t.putExtra("image", friend.getImage());
                context.startActivity(t);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popup = new PopupMenu(context,holder.textView);
                popup.inflate(R.menu.menu_delete);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.btnDelete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Xóa bạn bè");
                                builder.setMessage("bạn có muốn xóa người bạn này?");

                                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        database.getReference().child("users").child(auth.getUid()).
                                                child("friends").child(friends.get(position).getId())
                                                .removeValue();

                                        database.getReference().child("users").child(friend.getId()).
                                                child("friends").child(auth.getUid()).removeValue();

                                        friends.remove(position);
                                        notifyItemRemoved(position);

                                        Toast.makeText(context, "Đã xóa", Toast.LENGTH_SHORT).show();

                                    }
                                });

                                builder.setNegativeButton("hủy", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                builder.show();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

                popup.show();
                return true;
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
