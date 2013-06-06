package com.unus.smartrecorder;

import android.content.Context;

public interface SRVoiceInterface {
    public void initialize(Context context);
    
    public void finalize();

    public String makeDefaultTitle();
    
    public void setTitle(String title);

    public void setDocFilePath(String filePath);

    public void recordStart();
    
    public void recordStop();
    
    public void addTag(int type, String data, String position);
}
