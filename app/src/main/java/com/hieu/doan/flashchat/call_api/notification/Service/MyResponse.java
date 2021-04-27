package com.hieu.doan.flashchat.call_api.notification.Service;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyResponse {
    public static void sendNotifications(String usertoken, String title, String message) {
        if (FirebaseAuth.getInstance().getUid()!=null){
            Data data = new Data(title, message);
            Log.d("chatchit", message);
            Sender sender = new Sender(data, usertoken);
            APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
            apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
                @Override
                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                    if (response.code() == 200) {
                    }
                }

                @Override
                public void onFailure(Call<MyResponse> call, Throwable t) {

                }
            });
        }
    }
}
