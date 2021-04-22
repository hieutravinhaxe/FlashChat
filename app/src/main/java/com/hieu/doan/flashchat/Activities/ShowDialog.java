package com.hieu.doan.flashchat.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.hieu.doan.flashchat.R;

public class ShowDialog {
    Activity activity;
    Dialog dialog;

    public ShowDialog() {
    }

    public ShowDialog(Activity activity) {
        this.activity = activity;
    }

    public void show(String text) {
        dialog = new Dialog(activity);
        dialog.getWindow().getAttributes().windowAnimations = R.style.up_down;
        dialog.setContentView(R.layout.dialog_confirm_email);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        TextView textInfo = dialog.findViewById(R.id.tvDes);
        textInfo.setText(text);
        Button ok = dialog.findViewById(R.id.btnClose);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
