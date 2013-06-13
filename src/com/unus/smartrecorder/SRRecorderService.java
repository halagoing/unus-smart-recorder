package com.unus.smartrecorder;

import java.io.File;
import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.ProgressBar;

public class SRRecorderService extends Service{
	
	private MediaRecorder mRecorder = null;
	private String NOTI_RECORDING = "Recording...";
	private String NOTI_RECORDING_STOP = "Recording is stopped...";
	private int currentFormat = 0;
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	private static final String AUDIO_RECORDER_FOLDER = SRConfig.AUDIO_RECORDER_FOLDER;
	private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP };
	private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
	public final static int NOTIFICATION_ID = 1357234;
	private NotificationManager mNotifiManager;
	private Boolean isRecording = false;

	private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;
	RecordAmplitude recordAmplitude;
	
	//{{ for communicate Activity
	private final IBinder mBinder = new SRRecorderBinder();
	
	public class SRRecorderBinder extends Binder {
	    SRRecorderService getService() {
	        return SRRecorderService.this;
	    }
	}
	
	// For test
//	private int mTestNumber;
//	public int getTestNumber() {
//	    return mTestNumber++;
//	}
	private ProgressBar mProgressBar;
	public void setProgressBar(ProgressBar progressBar) {
	    SRDebugUtil.SRLog("setProgressBar() : " + progressBar.toString());
	    mProgressBar = progressBar;
	}
	//{{ for communicate Activity
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		//return null;
	    return mBinder;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		SRDebugUtil.SRLog("SRRecorderService.onCreate()");
		super.onCreate();
		mRecorder = null;
		isRecording = false;
		mNotifiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	@Override
	public boolean bindService(Intent service, ServiceConnection conn, int flags) {
		// TODO Auto-generated method stub
		//return super.bindService(service, conn, flags);
		SRDebugUtil.SRLog("bindService");
		return false;
	}
	
    public void record(String voicePath) {        
        SRDebugUtil.SRLog("voicePath = " + voicePath);

        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }

        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(output_formats[currentFormat]);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(voicePath);
        mRecorder.setOnErrorListener(errorListener);
        mRecorder.setOnInfoListener(infoListener);

        try {
            mRecorder.prepare();
            mRecorder.start();
            isRecording = true;
            recordAmplitude = new RecordAmplitude();
    		recordAmplitude.execute();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        showRecordingNotification();
    }
    
    public void recordStop() {
		if(isRecording){
			isRecording = false;
    		recordAmplitude.cancel(true);
            mProgressBar.setProgress(0);
		}

    	if (mRecorder != null) {
    		mRecorder.stop();
            // recorder.reset();
            mRecorder.release();
            mRecorder = null;
            showStoppedNotification();
        }
    }
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		SRDebugUtil.SRLog("call SRRecorderService onStartCommand");
		String voicePath = intent.getStringExtra(SRConfig.VOICE_PATH_KEY);
		SRDebugUtil.SRLog("voicePath = "+ voicePath);
		
		record(voicePath);
		return super.onStartCommand(intent, flags, startId);
	}
	 
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		SRDebugUtil.SRLog("call SRRecorderService onDestroy");
		recordStop();
		super.onDestroy();
	}
	
	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {     
            //Toast.makeText(this, "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();;
//        	Log.d("TAG", "Error = " +what);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
        	//Toast.makeText(this, "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        	//Toast.makeText(this, "Test", 300).show();
//        	Log.d("TAG", "Error = " +what);
            //Toast.makeText(this, "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };
    
    private void showRecordingNotification() {
    	showNotification(NOTI_RECORDING);
    	//isRecording = true;
  
    }
    
    private void showStoppedNotification() {
    	showNotification(NOTI_RECORDING_STOP);
    	mNotifiManager.cancelAll();
		//isRecording = false;
    }
    
    private void showNotification(String notiMsg) {
    	Notification notification = new Notification(R.drawable.stat_sys_call_record,
    			notiMsg, System.currentTimeMillis());
    	Intent intent = new Intent("com.blank");
    	PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
    	notification.setLatestEventInfo(this, "Smart Recorder", notiMsg, pendingIntent);
    	mNotifiManager.notify(NOTIFICATION_ID, notification);
	}
    private class RecordAmplitude extends AsyncTask<Void, Double, Void>{
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//publishProgress(0.0);
		}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			while (isRecording) {
		        try {
		          Thread.sleep(100);
		        } catch (InterruptedException e) {
		          e.printStackTrace();
		        }
		        publishProgress(getAmplitudeEMA());
		        //publishProgress(recorder.getMaxAmplitude());
		      }			
			return null;
		}
		protected void onProgressUpdate(Double... progress) {
		      //amplitudeTextView.setText(progress[0].toString());
			  Integer iVal = Integer.parseInt(String.valueOf(Math.round(progress[0] * 50)));
			  mProgressBar.setProgress(iVal);
	    }
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			mProgressBar.setProgress(0);
			super.onPostExecute(result);
		}
		 public double getAmplitude() {
             if (mRecorder != null)
                     return  (mRecorder.getMaxAmplitude()/2700.0);
             else
                     return 0;

		 }
	     public double getAmplitudeEMA() {
	             double amp = getAmplitude();
	             mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
	             return mEMA;
	     }
	}    
}
