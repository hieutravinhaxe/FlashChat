package com.hieu.doan.flashchat.Activities;

import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hieu.doan.flashchat.Adapters.ListConverAdapter;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;
import com.hieu.doan.flashchat.call_api.calling.Common;
import com.hieu.doan.flashchat.call_api.calling.IncomingCallActivity;
import com.hieu.doan.flashchat.call_api.calling.Utils;
import com.stringee.StringeeClient;
import com.stringee.call.StringeeCall;
import com.stringee.call.StringeeCall2;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StatusListener;
import com.stringee.listener.StringeeConnectionListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LifecycleObserver {
    public static StringeeClient client;
    FirebaseDatabase database;
    RecyclerView recyclerView;
    FirebaseAuth auth;
    ArrayList<User> listConver;
    ListConverAdapter adapter;
    BottomNavigationView bottomNavigationView;
    ProgressDialog dialog;

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        Log.d("AppLifecycle", "App in background");
        Common.isAppInBackground = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        Log.d("AppLifecycle", "App in foreground");
        Common.isAppInBackground = false;
    }

    //For calling
    String token = "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTS0p4T2hYSzVTYXZ2OFp1eDJ4dFB2SWtDblZCb0FpNEUzLTE2MTkyMDI5NTciLCJpc3MiOiJTS0p4T2hYSzVTYXZ2OFp1eDJ4dFB2SWtDblZCb0FpNEUzIiwiZXhwIjoxNjIxNzk0OTU3LCJ1c2VySWQiOiJzdWJpMiIsImljY19hcGkiOnRydWV9.T8uNx_73362tUusQ-qcwkNvzPRe9gjt5mPHIZnC20co";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final String PREF_NAME = "com.hieu.doan.flashchat";
    private final String IS_TOKEN_REGISTERED = "is_token_registered";
    private final String TOKEN = "token";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        setupNotification();
        recyclerView = findViewById(R.id.recyclerView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menuChat);

        database = FirebaseDatabase.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Your messages are loading...");
        dialog.setCancelable(false);


        listConver = new ArrayList<User>();

        adapter = new ListConverAdapter(this, listConver);
        recyclerView.setAdapter(adapter);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuChat:
                        //Toast.makeText(getApplicationContext(), "chat", Toast.LENGTH_SHORT).show();
                        /*startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        finish();*/
                        return true;
                    case R.id.menuFriends:
                        //Toast.makeText(getApplicationContext(), "call", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), FriendsActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.menuGroup:
                        startActivity(new Intent(getApplicationContext(), GroupsActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.menuManager:
                        //Toast.makeText(getApplicationContext(), "manager", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), ManagerActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });

        dialog.show();
        database.getReference().child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listConver.clear();
                final String uID = auth.getUid();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final String idChat = dataSnapshot.getKey();
                    database.getReference().child("users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                User u = snapshot1.getValue(User.class);
                                if (idChat.equals(uID + u.getId())) {
                                    listConver.add(u);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void initAndConnectStringee() {
        client = new StringeeClient(this);
        client.setConnectionListener(new StringeeConnectionListener() {
            @Override
            public void onConnectionConnected(final StringeeClient stringeeClient, boolean isReconnecting) {
                boolean isTokenRegistered = sharedPreferences.getBoolean(IS_TOKEN_REGISTERED, false);
                if (!isTokenRegistered) {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.d("Stringee", "getInstanceId failed", task.getException());
                                return;
                            }
                            //register push notification
                            final String token = task.getResult().getToken();
                            client.registerPushToken(token, new StatusListener() {
                                @Override
                                public void onSuccess() {
                                    Log.d("Stringee", "Register push token successfully.");
                                    editor.putBoolean(IS_TOKEN_REGISTERED, true);
                                    editor.putString(TOKEN, token);
                                    editor.commit();
                                }

                                @Override
                                public void onError(StringeeError error) {
                                    Log.d("Stringee", "Register push token unsuccessfully: " + error.getMessage());
                                }
                            });
                        }
                    });

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Utils.reportMessage(MainActivity.this, "PLASH CHAT is connected.");
                    }
                });
            }

            @Override
            public void onConnectionDisconnected(StringeeClient stringeeClient, boolean isReconnecting) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Utils.reportMessage(MainActivity.this, "Bị mất kết nối");
                    }
                });
            }

            @Override
            public void onIncomingCall(final StringeeCall stringeeCall) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Common.isInCall) {
                            stringeeCall.hangup();
                        } else {
                            Common.callsMap.put(stringeeCall.getCallId(), stringeeCall);
                            Intent intent = new Intent(MainActivity.this, IncomingCallActivity.class);
                            intent.putExtra("call_id", stringeeCall.getCallId());
                            startActivity(intent);
                        }
                    }
                });
            }

            @Override
            public void onIncomingCall2(StringeeCall2 stringeeCall2) {

            }

            @Override
            public void onConnectionError(StringeeClient stringeeClient, final StringeeError stringeeError) {
                Log.d("Stringee", "StringeeClient fails to connect: " + stringeeError.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.reportMessage(MainActivity.this, "Lỗi kết nối: " + stringeeError.getMessage());
                    }
                });
            }

            @Override
            public void onRequestNewToken(StringeeClient stringeeClient) {
                // Get new token here and connect to Stringe server
            }

            @Override
            public void onCustomMessage(String s, JSONObject jsonObject) {

            }

            @Override
            public void onTopicMessage(String s, JSONObject jsonObject) {

            }
        });
        client.connect(token);
    }

    private void setupNotification() {
        requiredPermissions();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        NotificationManager nm = (NotificationManager) getSystemService
                (NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancel(44448888);
        }

        initAndConnectStringee();
    }

    private void requiredPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        }, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Vui lòng cấp quyền để thực hiện Video Call!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
