//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : Untitled
//  @ File Name : SRVoice.java
//  @ Date : 2013-05-30
//  @ Author : 
//
//
package com.unus.smartrecorder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;

public class SRVoice implements SRVoiceInterface, OnCompletionListener {
    public static final int RECORDER_MODE = 1;
    public static final int PLAYER_MODE = 2;
    public static final int SEARCH_MODE = 3;
    public static final int PLAYER_PLAY_STATE = 1;
    public static final int PLAYER_STOP_STATE = 2;      // 재생중에 사용자가 버튼을 눌러 종료 
    public static final int PLAYER_COMPLETE_STATE = 3;  // 재생이 끝까지 되서 종료 
    public static final int PLAYER_PAUSE_STATE = 3;
    
    private int mMode = RECORDER_MODE;
    private int mPrevMode = RECORDER_MODE;
    
    public SRShare mShare;
    
    private Context mContext;
    
    private String mTitle;
    private String mVoiceFilePath;
    private String mDocFilePath;
    
    boolean isRecorder = false;
    //private MediaRecorder mRecorder = null;
	private int currentFormat = 0;
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	
	private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP };
	private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
    
	private SRDataSource mDataSource;
	private SRVoiceDb mVoiceDb;
	private ArrayList<SRTagDb> mTagList = new ArrayList<SRTagDb>();

	private MediaPlayer mPlayer;
	
	private int mPlayerState;
	
	private long mRecordStartTime;
	private Handler mHandler = new Handler();
	private Timer mTimer;
	private class TimeTimerTask extends TimerTask {
        
        @Override
        public void run() {
            notifyTimeObservers(getCurrentRecordTime());
        }
    };
	
	@Override
    public void initialize(Context context) {
	    mContext = context;
	    
        //DB Open
	    mDataSource = new SRDataSource(context);
	    mDataSource.open();
	    
	    mPlayer = new MediaPlayer();
	    mPlayer.setOnCompletionListener(this);
    }
	
    @Override
    public void finalize() {
        // TODO: DB Close
        if (mDataSource != null) {
            mDataSource.close();
        }
        
        // Timer
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
    
    @Override
    public int getMode() {
        return mMode;
    }
    
    @Override
    public void setMode(int mode) {
        mPrevMode = mMode;
        mMode = mode;
        
        if (mMode == PLAYER_MODE) {
            notifyPlayerBtnStateObservers(false);
        } else if (mMode == RECORDER_MODE) {
            notifyRecorderBtnStateObservers(false);
        } else if (mMode == SEARCH_MODE) {
            
        }
    }
    
    @Override
    public int getPrevMode() {
        return mPrevMode;
    }

    @Override
    public long getCurrentRecordTime() {
        return System.currentTimeMillis() - mRecordStartTime;
    }
 
    public ArrayList<SRTagDb> getmTagList() {
		return mTagList;
	}

	public void setmTagList(ArrayList<SRTagDb> mTagList) {
		this.mTagList = mTagList;
	}

    public void recordStart() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            playStop();
        }
        
    	SRDebugUtil.SRLog("SRVoice.recordStart() : voice = " + mVoiceFilePath + " doc = " + mDocFilePath);
    	
    	Intent recorderIntent = new Intent("com.unus.smartrecorder.Recorder");
    	recorderIntent.putExtra(SRConfig.VOICE_PATH_KEY, mVoiceFilePath);
    	mContext.startService(recorderIntent);
    	mRecordStartTime = System.currentTimeMillis();
    	mTimer = new Timer();
    	mTimer.schedule(new TimeTimerTask(), 1000, 1000);
    	
    	// Add Voice ddd
    	mVoiceDb = mDataSource.createVoice(mVoiceFilePath, mDocFilePath);
    	addTag(SRDbHelper.TEXT_TAG_TYPE, mTitle, "0");
    	
//    	ServiceConnection mConnection = null;
//        Boolean woo = mContext.bindService(recorderIntent, mConnection, 0);
//        SRDebugUtil.SRLog("woo = "+woo);
    	
    	//mView.setTagList();
    	
    	// SRVoiceView Button State
    	notifyRecorderBtnStateObservers(true);
    }
    /*
     *  recorder error handling
     */
//    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
//        @Override
//        public void onError(MediaRecorder mr, int what, int extra) {     
//        	DebugUtil.SRLog("Error = " +what);
//        }
//    };
//    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
//        @Override
//        public void onInfo(MediaRecorder mr, int what, int extra) {
//        	DebugUtil.SRLog("Error = " +what);
//        }
//    };


    public void recordStop() {
        SRDebugUtil.SRLog("SRVoice.recordStop()");
        
    	mContext.stopService(new Intent("com.unus.smartrecorder.Recorder"));
//    	SRVoiceView.mBtnRecorder.setText("recorder");
//		isRecorder = false;
//		mRecorder.stop();
//		mRecorder.release();
//		mRecorder = null;
    	
    	if (mTimer != null) {
        	mTimer.cancel();
        	mTimer = null;
    	}
    	mVoiceDb = null;
    	
        // SRVoiceView Button State
        notifyRecorderBtnStateObservers(false);
    }

    @Override
    public int getPlayerState() {
        return mPlayerState;
    }
    
    /**
     * 재생 동작을 한다. (Search List에서 선택된 경우)
     */
    @Override
    public void play(long voiceId, int position) {
        if (mDataSource == null) {
            SRDebugUtil.SRLogError("SRVoice.play() : mDataSource is null");
            return;
        }
        
        SRVoiceDb voiceDb =  mDataSource.getVoiceByVoiceId(voiceId);
        if (voiceDb == null) {
            SRDebugUtil.SRLogError("SRVoice.play() : voiceId is not valid");
            return;
        }
        
        if (getPrevMode() == RECORDER_MODE) {
            recordStop();
        }
        
        mVoiceFilePath = voiceDb.getVoice_path();
        mTitle = makeVoicePathToTitle(mVoiceFilePath);
 
        SRDebugUtil.SRLog("SRVoice.play(): filePath = " + mVoiceFilePath + " pos = " + Integer.toString(position));
        
        try {
            mPlayer.reset();
            mPlayer.setDataSource(mVoiceFilePath);
            mPlayer.prepare();
            mPlayer.seekTo(position);
            mPlayer.start();
            mPlayerState = PLAYER_PLAY_STATE; 
            notifyPlayerBtnStateObservers(true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    /**
     * 재생중 상태에 따라서 재생, 일시정지 동작을 한다. (Play/Pause 버튼을 누른 경우)
     */
    @Override
    public void playToggle() {
        SRDebugUtil.SRLog("SRVoice.playToggle()");
        
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                // Pause
                mPlayer.pause();
                mPlayerState = PLAYER_PAUSE_STATE;
                notifyPlayerBtnStateObservers(false);
            } else {
                // Play
                if (mPlayerState == PLAYER_PAUSE_STATE) {
                    mPlayer.start();
                    notifyPlayerBtnStateObservers(true);
                }else if (mPlayerState == PLAYER_STOP_STATE
                        || mPlayerState == PLAYER_COMPLETE_STATE ) {
                    try {
                        if (mPlayerState == PLAYER_STOP_STATE) {
                            mPlayer.prepare();
                            mPlayer.seekTo(0);
                        }
                        mPlayer.start();
                        mPlayerState = PLAYER_PLAY_STATE; 
                        notifyPlayerBtnStateObservers(true);
                    } catch (IllegalStateException e) {
                        mPlayer.reset();
                        e.printStackTrace();
                    } catch (IOException e) {
                        mPlayer.reset();
                        e.printStackTrace();
                    }
                }
            }
        }       
    }
    
    /**
     * 재생 종료되었을 때 콜백, 버튼의 상태를 바꿔준다. 
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        SRDebugUtil.SRLog("SRVoice.onCompletion(): Playing completed");
        
        mPlayerState = PLAYER_COMPLETE_STATE;
        notifyPlayerBtnStateObservers(false);
    }    

    /**
     * SeekBar를 움직였을 때 해당 position으로 이동
     * 
     * @param position
     */
    public void seekTo(int position) {
        SRDebugUtil.SRLog("SRVoice.getDuration()");
        
        if (mPlayer != null) {
            mPlayer.seekTo(position);
        }
    }

    /**
     * 재생 일시 정지 
     */
    @Override
    public void playPause() {
        SRDebugUtil.SRLog("SRVoice.playPause()");
        
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            notifyPlayerBtnStateObservers(false);
        }
    }

    /**
     * 재생 정지 
     */
    @Override
    public void playStop() {
        SRDebugUtil.SRLog("SRVoice.playStop()");
        
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayerState = PLAYER_STOP_STATE;
            notifyPlayerBtnStateObservers(false);
        }
    }

    /**
     * 재생 파일의 전체 재생시간을 리턴한다 
     * 
     * @return
     */
    public int getDuration() {
        SRDebugUtil.SRLog("SRVoice.getDuration()");
        
        if (mPlayer != null) {
            return mPlayer.getDuration();
        }
        return 0;
    }

    public void share() {

    }
    
    /**
     * 기본 Title을 시간정보를 이용해 자동 생성해준다.
     */
    @Override
    public String makeDefaultTitle() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        
        return new String("Audio" + "-" + sdf.format(new Date()));
    }
    
    /**
     * Voice File 경로에서 Title에 해당하는 String을 리
     * @param voicePath
     * @return
     */
    public String makeVoicePathToTitle(String voicePath) {
        int i = voicePath.lastIndexOf("/");
        if (i == -1)
            return null;
        else 
            return voicePath.substring(i + 1);
    }

    @Override
    public void setTitle(String title) {
        SRDebugUtil.SRLog("setTitle(): " + title);
        mTitle = title;
        
        mVoiceFilePath = String.format("%s/%s/%s.mp4", 
                Environment.getExternalStorageDirectory().getPath(),
                SRConfig.AUDIO_RECORDER_FOLDER, mTitle);
        SRDebugUtil.SRLog("VoiceFilePath: " + mVoiceFilePath);
    }
    
    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void setDocFilePath(String filePath) {
        SRDebugUtil.SRLog("setDocFilePath(): " + filePath);
        
        mDocFilePath = filePath;
    }
    
    @Override
    public void addTag(int type, String data, String position) {
        if (mDataSource == null || mVoiceDb == null) {
            SRDebugUtil.SRLogError("addTag(): DB is null");
            return;
        }
        
        SRDebugUtil.SRLog("addTag(): " + Integer.toString(type) + " [" + data + "] " +position);
        SRTagDb tag = mDataSource.createTag(mVoiceDb.getVoice_id(), type, data, position);
        mTagList.add(tag);
        notifyTagsObservers(tag);
    }

    public interface SRVoiceObserver {
        public void updateTags(SRTagDb tag);
        public void updateTime(long time);
        public void updateRecorderBtnState(boolean isRecording);
        public void updatePlayerBtnState(boolean isPlaying);
    }
    
    ArrayList<SRVoiceObserver> mSRVoiceObserver = new ArrayList<SRVoiceObserver>();
    public void registerObserver(SRVoiceObserver observer) {
        mSRVoiceObserver.add(observer);
    }
    
    public void notifyTagsObservers(SRTagDb tag) {
        for (int i = 0; i < mSRVoiceObserver.size(); i++) {
            SRVoiceObserver observer = mSRVoiceObserver.get(i);
            observer.updateTags(tag);
        }
    }
    
    public void notifyTimeObservers(long time) {
        for (int i = 0; i < mSRVoiceObserver.size(); i++) {
            SRVoiceObserver observer = mSRVoiceObserver.get(i);
            observer.updateTime(time);
        }
    }
    
    public void notifyRecorderBtnStateObservers(boolean isRecording) {
        for (int i = 0; i < mSRVoiceObserver.size(); i++) {
            SRVoiceObserver observer = mSRVoiceObserver.get(i);
            observer.updateRecorderBtnState(isRecording);
        }
    }
    
    public void notifyPlayerBtnStateObservers(boolean isPlaying) {
        for (int i = 0; i < mSRVoiceObserver.size(); i++) {
            SRVoiceObserver observer = mSRVoiceObserver.get(i);
            observer.updatePlayerBtnState(isPlaying);
        }
    } 
    
    public void removeObserver(SRVoiceObserver o) {
        int i = mSRVoiceObserver.indexOf(o);
        if (i >= 0) {
            mSRVoiceObserver.remove(i);
        }
    }
}
