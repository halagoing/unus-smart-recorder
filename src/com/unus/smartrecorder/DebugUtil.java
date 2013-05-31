package com.unus.smartrecorder;

import android.util.Log;

public class DebugUtil {
    public static final boolean DEBUG = true;
    
    public static int SRLog(String log) {
        return (DEBUG ? Log.d("SmartRecorder", log) : 1);
    }
}
