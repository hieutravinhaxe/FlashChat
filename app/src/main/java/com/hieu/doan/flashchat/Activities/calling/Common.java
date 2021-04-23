package com.hieu.doan.flashchat.Activities.calling;

import android.media.Ringtone;

import com.stringee.call.StringeeCall;

import java.util.HashMap;
import java.util.Map;

public class Common {
    public static Map<String, StringeeCall> callsMap = new HashMap<>();
    public static StringeeAudioManager audioManager;
    public static boolean isInCall = false;
    public static Ringtone ringtone;
    public static boolean isAppInBackground = false;
}
