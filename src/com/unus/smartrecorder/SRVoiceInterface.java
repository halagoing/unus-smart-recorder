package com.unus.smartrecorder;

import com.unus.smartrecorder.SRVoice.SRVoiceObserver;

import android.content.Context;

public interface SRVoiceInterface {
    public void initialize(Context context);
    
    public void finalize();

    public String makeDefaultTitle();
    
    public void setTitle(String title);
    
    public String getTitle();

    public void setDocFilePath(String filePath);

    public void recordStart();
    
    public void recordStop();
    
    public void addTag(int type, String data, String position);
    
    public void registerObserver(SRVoiceObserver observer);
    
    public void notifyTagsObservers(SRTagDb tag);
    
    public void notifyTimeObservers(long timer);   
    
    public void removeObserver(SRVoiceObserver o);

    public long getCurrentRecordTime();

    public int getMode();
    
    public int getPrevMode();
    
    public void setMode(int mode);

    public void play(long voiceId, int position);
}
