package com.hieu.doan.flashchat.call_api.calling;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieu.doan.flashchat.Activities.ChatActivity;
import com.hieu.doan.flashchat.Activities.ManagerActivity;
import com.hieu.doan.flashchat.Models.Message;
import com.hieu.doan.flashchat.Models.User;
import com.hieu.doan.flashchat.R;
import com.stringee.call.StringeeCall;
import com.stringee.common.StringeeConstant;
import com.stringee.listener.StatusListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IncomingCallActivity extends AppCompatActivity implements View.OnClickListener {
    private FrameLayout mLocalViewContainer;
    private FrameLayout mRemoteViewContainer;
    private TextView tvFrom;
    private TextView tvState;
    private ImageButton btnAnswer;
    private ImageButton btnEnd;
    private ImageButton btnMute;
    private ImageButton btnSpeaker;
    private ImageButton btnVideo;
    private ImageButton btnSwitch;
    private View vControl;
    static String sendID, receiveID, name, image;

    private StringeeCall mStringeeCall;
    private boolean isMute = false;
    private boolean isSpeaker = false;
    private boolean isVideo = false;

    private StringeeCall.MediaState mMediaState;
    private StringeeCall.SignalingState mSignalingState;

    public static final int REQUEST_PERMISSION_CALL = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        Common.isInCall = true;

        String callId = getIntent().getStringExtra("call_id");
        mStringeeCall = Common.callsMap.get(callId);

        mLocalViewContainer = (FrameLayout) findViewById(R.id.v_local);
        mRemoteViewContainer = (FrameLayout) findViewById(R.id.v_remote);

        tvFrom = (TextView) findViewById(R.id.tv_from);
        receiveID = mStringeeCall.getFrom();

        sendID = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(receiveID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user  = snapshot.getValue(User.class);
                name = user.getName();
                image = user.getImage();
                tvFrom.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tvState = (TextView) findViewById(R.id.tv_state);

        btnAnswer = (ImageButton) findViewById(R.id.btn_answer);
        btnAnswer.setOnClickListener(this);

        btnEnd = (ImageButton) findViewById(R.id.btn_end);
        btnEnd.setOnClickListener(this);

        btnMute = (ImageButton) findViewById(R.id.btn_mute);
        btnMute.setOnClickListener(this);
        btnSpeaker = (ImageButton) findViewById(R.id.btn_speaker);
        btnSpeaker.setOnClickListener(this);
        btnVideo = (ImageButton) findViewById(R.id.btn_video);
        btnVideo.setOnClickListener(this);
        btnSwitch = (ImageButton) findViewById(R.id.btn_switch);
        btnSwitch.setOnClickListener(this);

        isSpeaker = mStringeeCall.isVideoCall();
        btnSpeaker.setBackgroundResource(isSpeaker ? R.drawable.btn_speaker_on : R.drawable.btn_speaker_off);

        vControl = findViewById(R.id.v_control);
        isVideo = mStringeeCall.isVideoCall();
        btnVideo.setImageResource(isVideo ? R.drawable.btn_video : R.drawable.btn_video_off);

        btnVideo.setVisibility(isVideo ? View.VISIBLE : View.GONE);
        btnSwitch.setVisibility(isVideo ? View.VISIBLE : View.GONE);

        //create audio manager to control audio device
        if (Common.audioManager == null) {
            Common.audioManager = StringeeAudioManager.create(IncomingCallActivity.this);
            Common.audioManager.start(new StringeeAudioManager.AudioManagerEvents() {
                @Override
                public void onAudioDeviceChanged(StringeeAudioManager.AudioDevice selectedAudioDevice, Set<StringeeAudioManager.AudioDevice> availableAudioDevices) {
                    Log.d("StringeeAudioManager", "onAudioManagerDevicesChanged: " + availableAudioDevices + ", "
                            + "selected: " + selectedAudioDevice);
                }
            });
        }

        //play device ringtone
        if (Common.ringtone == null) {
            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            Common.ringtone = RingtoneManager.getRingtone(IncomingCallActivity.this, ringtoneUri);
            Common.ringtone.play();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> lstPermissions = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                lstPermissions.add(Manifest.permission.RECORD_AUDIO);
            }

            if (mStringeeCall.isVideoCall()) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    lstPermissions.add(Manifest.permission.CAMERA);
                }
            }

            if (lstPermissions.size() > 0) {
                String[] permissions = new String[lstPermissions.size()];
                for (int i = 0; i < lstPermissions.size(); i++) {
                    permissions[i] = lstPermissions.get(i);
                }
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CALL);
                return;
            }
        }

        initAnswer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        boolean isGranted = false;
        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                } else {
                    isGranted = true;
                }
            }
        }
        if (requestCode == REQUEST_PERMISSION_CALL) {
            if (!isGranted) {
                endCall(false, true);
            } else {
                initAnswer();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("name", name);
        intent.putExtra("receiveID", receiveID);
        intent.putExtra("sendID", sendID);
        intent.putExtra("image", image);
        startActivity(intent);
    }

    private void initAnswer() {
        mStringeeCall.setCallListener(new StringeeCall.StringeeCallListener() {
            @Override
            public void onSignalingStateChange(StringeeCall stringeeCall, final StringeeCall.SignalingState signalingState, String s, int i, String s1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Stringee", "signalingState: " + signalingState);
                        mSignalingState = signalingState;
                        switch (signalingState) {
                            case ANSWERED:
                                tvState.setText(R.string.connected);
                                if (mMediaState == StringeeCall.MediaState.CONNECTED) {
                                    tvState.setText(R.string.connected);
                                }
                                break;
                            case ENDED:
                                tvState.setText(R.string.end_call);
                                endCall(true, false);
                                break;
                        }
                    }
                });
            }

            @Override
            public void onError(StringeeCall stringeeCall, int i, String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.reportMessage(IncomingCallActivity.this, s);
                    }
                });
            }

            @Override
            public void onHandledOnAnotherDevice(StringeeCall stringeeCall, final StringeeCall.SignalingState signalingState, String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (signalingState == StringeeCall.SignalingState.ANSWERED || signalingState == StringeeCall.SignalingState.BUSY) {
                            Utils.reportMessage(IncomingCallActivity.this, "This call is handled on another device.");
                            endCall(false, false);
                        }
                    }
                });
            }

            @Override
            public void onMediaStateChange(StringeeCall stringeeCall, final StringeeCall.MediaState mediaState) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Stringee", "mediaState: " + mediaState);
                        mMediaState = mediaState;
                        if (mediaState == StringeeCall.MediaState.CONNECTED) {
                            if (mSignalingState == StringeeCall.SignalingState.ANSWERED) {
//                                tvState.setText("Started");
                                tvState.setVisibility(View.GONE);
                                tvFrom.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            }

            @Override
            public void onLocalStream(final StringeeCall stringeeCall) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (stringeeCall.isVideoCall()) {
                            mLocalViewContainer.removeAllViews();
                            mLocalViewContainer.addView(stringeeCall.getLocalView());
                            stringeeCall.renderLocalView(true);
                        }
                    }
                });
            }

            @Override
            public void onRemoteStream(final StringeeCall stringeeCall) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (stringeeCall.isVideoCall()) {
                            mRemoteViewContainer.removeAllViews();
                            mRemoteViewContainer.addView(stringeeCall.getRemoteView());
                            stringeeCall.renderRemoteView(false);
                        }
                    }
                });
            }

            @Override
            public void onCallInfo(StringeeCall stringeeCall, final JSONObject jsonObject) {

            }
        });
        mStringeeCall.ringing(new StatusListener() {
            @Override
            public void onSuccess() {
                Log.d("Stringee", "send ringing success");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_mute:
                isMute = !isMute;
                btnMute.setBackgroundResource(isMute ? R.drawable.btn_mute : R.drawable.btn_mic);
                if (mStringeeCall != null) {
                    mStringeeCall.mute(isMute);
                }
                break;
            case R.id.btn_speaker:
                isSpeaker = !isSpeaker;
                btnSpeaker.setBackgroundResource(isSpeaker ? R.drawable.btn_speaker_on : R.drawable.btn_speaker_off);
                if (Common.audioManager != null) {
                    Common.audioManager.setSpeakerphoneOn(isSpeaker);
                }
                break;
            case R.id.btn_answer:
                if (mStringeeCall != null) {
                    Common.audioManager.setSpeakerphoneOn(isVideo);
                    if (Common.ringtone != null && Common.ringtone.isPlaying()) {
                        Common.ringtone.stop();
                        Common.ringtone = null;
                    }
                    vControl.setVisibility(View.VISIBLE);
                    btnAnswer.setVisibility(View.GONE);
                    mStringeeCall.answer();
                }
                break;
            case R.id.btn_end:
                endCall(true, false);
                break;
            case R.id.btn_video:
                isVideo = !isVideo;
                btnVideo.setImageResource(isVideo ? R.drawable.btn_video : R.drawable.btn_video_off);
                if (mStringeeCall != null) {
                    mStringeeCall.enableVideo(isVideo);
                    mStringeeCall.setQuality(StringeeConstant.QUALITY_FULLHD);
                }
                break;
            case R.id.btn_switch:
                if (mStringeeCall != null) {
                    mStringeeCall.switchCamera(new StatusListener() {
                        @Override
                        public void onSuccess() {

                        }
                    });
                }
                break;
        }
    }

    private void endCall(boolean isHangup, boolean isReject) {
        if (Common.audioManager != null) {
            Common.audioManager.stop();
            Common.audioManager = null;
        }

        if (Common.ringtone != null && Common.ringtone.isPlaying()) {
            Common.ringtone.stop();
            Common.ringtone = null;
        }

        if (isHangup) {
            if (mStringeeCall != null) {
                mStringeeCall.hangup();
            }
        }

        if (isReject) {
            if (mStringeeCall != null) {
                mStringeeCall.reject();
            }
        }

        Utils.postDelay(new Runnable() {
            @Override
            public void run() {
                Common.isInCall = false;
                finish();
            }
        }, 1000);
    }
}
