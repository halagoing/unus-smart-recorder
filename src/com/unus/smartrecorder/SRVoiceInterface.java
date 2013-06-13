package com.unus.smartrecorder;

import java.util.ArrayList;

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
    
    public void notifyTimeObservers(int timer);   
    
    public void removeObserver(SRVoiceObserver o);

    public int getCurrentRecordTime();
    
    public int getCurrentPlayTime();
    
    public int getDurationTime();

    public int getMode();
    
    public int getPrevMode();
    
    public void setMode(int mode);
    
    public void play(String voicePath, int seekTime);

    public void playStop();

    public void playResume(boolean isStopped);

    public void playPause();

    public void playToggle();

    public int getPlayerState();
    
    public boolean isRecordering();
    
    public void seekTo(int seekTime);

    public boolean isAutoTag();

    public void setAutoTag(boolean isAutoTag);

    public SRDataSource getDataSource();

    public String makeVoicePathToTitle(String voicePath);

    public ArrayList<SRTagDb> getTagList();

    public void setTagList(ArrayList<SRTagDb> tagList);

    public long getVoiceId();

    public void setVoiceId(long voiceId);

    public void setPageTagList(ArrayList<SRTagDb> docTagByVoiceId);

    public ArrayList<SRTagDb> getPageTagList();

    public boolean isPlaying();
    
    public void setmTempTagForDelete(SRTagDb tagDb);
    
    public void setmTempVoiceForDelete(SRVoiceDb voiceDb);
    
    public SRTagDb getmTempTagForDelete();
    
    public SRVoiceDb getmTempVoiceForDelete();

    public void setSRRecorderService(SRRecorderService service);

    public SRRecorderService getSRRecorderService();

    public void setSRRecorderServiceBound(boolean isBound);

    public boolean isSRRecorderServiceBound();

}
