package com.unus.smartrecorder;

import android.util.Log;

public class SRDebugUtil {
    public static final boolean DEBUG = true;
    
    public static int SRLog(String log) {
        return (DEBUG ? Log.d("SmartRecorder", log) : 1);
    }
    public static int SRLogError(String log) {
        return (DEBUG ? Log.e("SmartRecorder", log) : 1);
    }
}
