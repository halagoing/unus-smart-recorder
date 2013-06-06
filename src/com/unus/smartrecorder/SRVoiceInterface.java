package com.unus.smartrecorder;

public interface SRVoiceInterface {
    public void initialize();

    public String makeDefaultTitle();
    
    public void setTitle(String title);

    public void setDocFilePath(String filePath);
}
