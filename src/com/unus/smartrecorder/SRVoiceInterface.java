package com.unus.smartrecorder;

import android.content.Context;

public interface SRVoiceInterface {
    public void initialize();

    public String makeDefaultTitle();
    
    public void setTitle(String title);

    public void setDocFilePath(String filePath);

    public void recordStart(Context context);
    
    public void recordStop(Context context);
}
