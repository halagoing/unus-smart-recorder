package com.unus.smartrecorder;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SRLogoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.sr_logo);
        
        Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                finish();
            }
            
        };
        handler.sendEmptyMessageDelayed(0, 3000);
    }

}
