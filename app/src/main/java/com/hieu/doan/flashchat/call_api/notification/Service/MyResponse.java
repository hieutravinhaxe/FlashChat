package com.hieu.doan.flashchat.call_api.notification.Service;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyResponse {
    public int success;

    public static void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        Log.d("chatchit", message);
        Sender sender = new Sender(data, usertoken);
        APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

}