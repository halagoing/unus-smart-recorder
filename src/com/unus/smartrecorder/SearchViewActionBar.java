/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.unus.smartrecorder;

import java.lang.reflect.Field;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.Window;

import com.android.debug.hv.ViewServer;

/**
 * This demonstrates the usage of SearchView in an ActionBar as a menu item. It
 * sets a SearchableInfo on the SearchView for suggestions and submitting
 * queries to.
 */
public class SearchViewActionBar extends Activity {

    private ActionBar mActionBar;
   
    private SRVoice mSRVoice;
    private SRVoiceController mSRVoiceController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        // setContentView(R.layout.searchview_actionbar);

        mActionBar = getActionBar();
        mActionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_HOME); // remove
                                                                      // title
                                                                      // icon
        getOverflowMenu();
        
        // initial state
        mActionBar.setTitle(R.string.no_title); // no title
        
        mSRVoice = new SRVoice();
        mSRVoiceController = new SRVoiceController(mSRVoice, this);
        mSRVoiceController.setViewMode(SRVoice.RECORDER_MODE);
        
        // Logo
        startActivity(new Intent(this, SRLogoActivity.class));
        
        // DEBUG : For Hierarchy Viewer
        ViewServer.get(this).addWindow(this);
    }
    
    private void getOverflowMenu() {

        try {
           ViewConfiguration config = ViewConfiguration.get(this);
           Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
           if(menuKeyField != null) {
               menuKeyField.setAccessible(true);
               menuKeyField.setBoolean(config, false);
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        if (mSRVoiceController != null)
            mSRVoiceController.createOptionMenu(menu);
        
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mSRVoiceController != null)
            mSRVoiceController.optionsItemSelected(item);
        
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // DEBUG : For Hierarchy Viewer
        ViewServer.get(this).setFocusedWindow(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSRVoiceController != null)
            mSRVoiceController.finalize();
        
        // DEBUG : For Hierarchy Viewer
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        if (mSRVoiceController != null) {
            return mSRVoiceController.createDialog(id);
        }
        
        return null;
    }

    @Override
    @Deprecated
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        if (mSRVoiceController != null) {
            mSRVoiceController.prepareDialog(id, dialog, args);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mSRVoiceController != null) {
            mSRVoiceController.activityResult(requestCode, resultCode, data);
        }
    }
}
