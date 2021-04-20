package com.hieu.doan.flashchat.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.hieu.doan.flashchat.R;

public class AddFriendDialog extends AppCompatDialogFragment {
    EditText emailEditText;
    AddfriendDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_friend_dialog, null);

        builder.setView(view)
                .setTitle("Add a friend")
                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String email = emailEditText.getText().toString();
                        listener.applyText(email);
                    }
                });

        emailEditText = view.findViewById(R.id.emailEditText);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (AddfriendDialogListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    public interface AddfriendDialogListener {
        void applyText(String email);
    }


}
