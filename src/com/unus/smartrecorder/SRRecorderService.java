package com.unus.smartrecorder;

import java.io.File;
import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class SRRecorderService extends Service{
	
	private MediaRecorder recorder = null;
	private int currentFormat = 0;
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	private static final String AUDIO_RECORDER_FOLDER = "BondRecorder";
	private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP };
	private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		DebugUtil.SRLog("call SRRecorderService onStartCommand");
		
		DebugUtil.SRLog("RecorderService onStartCommand");

		recorder = new MediaRecorder();
		
		String filepath = Environment.getExternalStorageDirectory().getPath();
		
		DebugUtil.SRLog("filepath = " +filepath);
		
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);
	    if (!file.exists()) {
	        file.mkdirs();
	    }
	    //String filename = file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat];
	    String filename = file.getAbsolutePath() + "/" + "test" + file_exts[currentFormat];
	    DebugUtil.SRLog("filename = " +filename);
	    
	    
	    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    recorder.setOutputFormat(output_formats[currentFormat]);
	    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	    recorder.setOutputFile(filename);
	    recorder.setOnErrorListener(errorListener);
	    recorder.setOnInfoListener(infoListener);
	    
	    try {
	        recorder.prepare();
	        recorder.start();
	    } catch (IllegalStateException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		
		return super.onStartCommand(intent, flags, startId);
	}
	 
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		DebugUtil.SRLog("call SRRecorderService onDestroy");
		recorder.stop();
        //recorder.reset();
        recorder.release();
        recorder = null;
		super.onDestroy();
	}
	
	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {     
            //Toast.makeText(this, "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();;
        	Log.d("TAG", "Error = " +what);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
        	//Toast.makeText(this, "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        	//Toast.makeText(this, "Test", 300).show();
        	Log.d("TAG", "Error = " +what);
            //Toast.makeText(this, "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };
}
