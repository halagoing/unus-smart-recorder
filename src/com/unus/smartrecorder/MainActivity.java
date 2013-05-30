package com.unus.smartrecorder;

import com.android.debug.hv.ViewServer;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.view.Menu;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
    ActionBar mActionBar; // Title Bar

    SRVoiceView mSRVoiceView; // SRVoiceView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActionBar = getActionBar();
        mSRVoiceView = new SRVoiceView(getBaseContext());

        setContentView(mSRVoiceView);

        // DEBUG : For Hierarchy Viewer
        ViewServer.get(this).addWindow(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        // DEBUG : For Hierarchy Viewer
        ViewServer.get(this).setFocusedWindow(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        // DEBUG : For Hierarchy Viewer
        ViewServer.get(this).removeWindow(this);
    }

}
