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

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.Window;
import android.widget.SearchView;

import com.android.debug.hv.ViewServer;

/**
 * This demonstrates the usage of SearchView in an ActionBar as a menu item. It
 * sets a SearchableInfo on the SearchView for suggestions and submitting
 * queries to.
 */
public class SearchViewActionBar extends Activity implements
        SearchView.OnQueryTextListener, SearchView.OnCloseListener  {
    public static final int STATE_RECORDING = 1;        // Recording
    public static final int STATE_PLAYING = 2;          // Playing
    public static final int STATE_SEARCHING = 3;        // Searching
    
    private SearchView mSearchView;
    private ActionBar mActionBar;
    private int mViewState; 
    private int mPrevViewState;
    
    private SRSearchView mSRSearchView;
    private SRVoiceView mSRVoiceView;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        //setContentView(R.layout.searchview_actionbar);

        mActionBar = getActionBar();
        mActionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_HOME);   // remove title icon
        
        //initial state
        mActionBar.setTitle(R.string.no_title);         // no title
        mPrevViewState = mViewState = STATE_RECORDING;  // recording
        
        // TEST
        mSRSearchView = new SRSearchView(getBaseContext());
        mSRVoiceView = new SRVoiceView(getBaseContext());
        
        setContentView(mSRVoiceView);

        // DEBUG : For Hierarchy Viewer
        ViewServer.get(this).addWindow(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchview_in_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        
        searchItem.setOnActionExpandListener(new OnActionExpandListener() {
            
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                DebugUtil.SRLog("onMenuItemActionExpand()");

                setViewState(STATE_SEARCHING);
                return true;
            }
            
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                DebugUtil.SRLog("onMenuItemActionCollapse()");

                setViewState(STATE_RECORDING);
                return true;
            }
        });
        
        mSearchView = (SearchView) searchItem.getActionView();
        setupSearchView(searchItem);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        //return super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
        case android.R.id.home:
            break;
        case R.id.action_search:
            break;
        default:
            break;
        }
        return true;
    }

    private void setupSearchView(MenuItem searchItem) {

        if (isAlwaysExpanded()) {
            mSearchView.setIconifiedByDefault(false);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }

//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        if (searchManager != null) {
//            List<SearchableInfo> searchables = searchManager
//                    .getSearchablesInGlobalSearch();
//
//            // Try to use the "applications" global search provider
//            SearchableInfo info = searchManager
//                    .getSearchableInfo(getComponentName());
//            for (SearchableInfo inf : searchables) {
//                if (inf.getSuggestAuthority() != null
//                        && inf.getSuggestAuthority().startsWith("applications")) {
//                    info = inf;
//                }
//            }
//            mSearchView.setSearchableInfo(info);
//        }

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
    }

    public boolean onQueryTextChange(String newText) {
        DebugUtil.SRLog("Query = " + newText);
        if (TextUtils.isEmpty(newText)) {
            mSRSearchView.clearTextFilter();
        } else {
            mSRSearchView.setFilterText(newText.toString());
        }
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        DebugUtil.SRLog("Query = " + query + " : submitted");
        return false;
    }

    public boolean onClose() {
        DebugUtil.SRLog("onClose()");
        return false;
    }

    protected boolean isAlwaysExpanded() {
        return false;
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

        // DEBUG : For Hierarchy Viewer
        ViewServer.get(this).removeWindow(this);
    }
    
    public int getViewState() {
        return mViewState;
    }
    
    public void setViewState(int state) {
        switch(state) {
        case STATE_PLAYING:
            setContentView(mSRVoiceView);
            break;
        case STATE_RECORDING:
            setContentView(mSRVoiceView);
            break;
        case STATE_SEARCHING:
            setContentView(mSRSearchView);
            break;
        default:
            return;
        }
        mPrevViewState = mViewState;    // Previous View State
        mViewState = state;  
    }
    
    public int getPrevViewState() {
        return mPrevViewState;
    }
    
}
