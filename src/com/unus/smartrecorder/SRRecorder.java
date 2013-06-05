package com.unus.smartrecorder;

import java.io.File;

import android.content.Context;
import android.os.Environment;





public class SRRecorder {
	
	private Context mContext;
	
	private File mrecorderDir = null;
	
	public SRRecorder(Context context) {
        mContext = context;
        File sampleDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + SRConfig.AUDIO_RECORDER_FOLDER);
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        mrecorderDir = sampleDir;

        //syncStateWithService();
    }
	
	public void startRecording(){
		SRDebugUtil.SRLog("SRRecorder startRecording -> sampleDir = " + mrecorderDir);
		
            
    }
}
