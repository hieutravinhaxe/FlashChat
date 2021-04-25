package com.hieu.doan.flashchat.call_api.notification.Service;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Authorization:key=AAAA9IocR_A:APA91bET8-w_Xtqw-b0coE9JkZTQ9CCktaW4pttnzMhwKoEHT3IzzCrU9fM_Ikb6daoBvFRgcu87TEuMQJ5UWFEQunUk3QV1NI5PyXVINT4MyUsMavAgbu04xFzFGOC0RFm3soNzGH8g",
                    "Content-Type:application/json"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body Sender body);
}

