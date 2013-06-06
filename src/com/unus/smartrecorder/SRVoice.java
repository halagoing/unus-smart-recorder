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
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

public class SRVoice implements SRVoiceInterface {
    private SRTag mTag;
    private SRDoc mDoc;
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
	
	@Override
    public void initialize(Context context) {
	    mContext = context;
	    
        //DB Open
	    mDataSource = new SRDataSource(context);
    }
	
    @Override
    public void finalize() {
        // TODO: DB Close
        if (mDataSource != null) {
            mDataSource.close();
        }
    }

    /**
     * Playing
     * 
     * 음성녹음 파일과 Tag DB 읽는다
     * 
     * Tag DB는 음성녹음 파일 이름을 Key로 가진다
     * 
     * @param filePath
     **/
    public void open(String filePath) {

    }

    public SRDoc getDoc() {
        return mDoc;
    }

    public void setDoc(SRDoc doc) {
        mDoc = doc;
    }

    public void recordStart() {
    	SRDebugUtil.SRLog("recordStart -> isRecorder = " + isRecorder);
    	Intent recorderIntent = new Intent("com.unus.smartrecorder.Recorder");
    	recorderIntent.putExtra(SRConfig.VOICE_PATH_KEY, mVoiceFilePath);
    	mContext.startService(recorderIntent);

    	// Add Voice
    	mVoiceDb = mDataSource.createVoice(mVoiceFilePath, mDocFilePath);
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
    	mContext.stopService(new Intent("com.unus.smartrecorder.Recorder"));
//    	SRVoiceView.mBtnRecorder.setText("recorder");
//		isRecorder = false;
//		mRecorder.stop();
//		mRecorder.release();
//		mRecorder = null;
    	mVoiceDb = null;
    }

    public void save() {

    }

    public void play() {
    	SRDebugUtil.SRLog("play -> play = ");
    	MediaPlayer player;
    	player = new MediaPlayer();
//    	player = MediaPlayer.create(this, R.raw.man);
    	

		String filepath = Environment.getExternalStorageDirectory().getPath();
		
    	File file = new File(filepath, "BondRecorder");
	    if (!file.exists()) {
	        file.mkdirs();
	    }
	    //String filename = file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat];
	    String filename = file.getAbsolutePath() + "/" + "test" + file_exts[currentFormat];
	    SRDebugUtil.SRLog("filename = " +filename);
    	
	    try {
			player.setDataSource(filename);
			player.prepare();
			//player.seekTo(1);
			player.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

    public void seekTo(int position) {

    }

    public void playPause() {

    }

    public void playStop() {

    }

    public void getCurrentPosition() {

    }

    public void getDuration() {

    }

    public void share() {

    }
    
    @Override
    public String makeDefaultTitle() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        
        return new String("Audio" + "-" + sdf.format(new Date()));
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
    }

    /*
    public void registerObserver(BeatObserver o) {
        beatObservers.add(o);
    }

    public void notifyBeatObservers() {
        for (int i = 0; i < beatObservers.size(); i++) {
            BeatObserver observer = (BeatObserver) beatObservers.get(i);
            observer.updateBeat();
        }
    }

    public void removeObserver(BeatObserver o) {
        int i = beatObservers.indexOf(o);
        if (i >= 0) {
            beatObservers.remove(i);
        }
    }
    */
}
