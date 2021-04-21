package com.hieu.doan.flashchat.Activities;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteMessage {
    private String id;

    public DeleteMessage(String id) {
        this.id = id;
    }
    boolean t = false;
    public boolean exeDelete(){
        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(this.id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        t = true;
                    }
                });
        return t;
    }
}
