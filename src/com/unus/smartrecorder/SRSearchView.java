//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : Untitled
//  @ File Name : SRSearchView.java
//  @ Date : 2013-05-30
//  @ Author : 
//
//
package com.unus.smartrecorder;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;

public class SRSearchView extends FrameLayout {
    private Context mContext;
    private SRVoiceControllerInterface mController;
    private ListView mListView;
    private SRTagListAdapter tagListAdapter;

    public SRSearchView(Context context) {
        super(context);
        initView(context);
    }
    
    
    public SRSearchView(Context context, SRVoiceControllerInterface controller) {
        super(context);
        mController = controller;
        initView(context);
    }
    
    public SRSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sr_searchview_layout, this, true);
          
        
        

    }
    
    public void clearTextFilter() {
        if (mListView != null) {
            mListView.clearTextFilter();
        }
    }
    
    public void setFilterText(String filterText) {
        if (mListView != null) {
            mListView.setFilterText(filterText);
        }        
    }
    
    public void deleteTag(SRTagDb tagDb) {
    	tagListAdapter.remove(tagDb);
    }
    
    public void setSearchViewMode() {
        SRDebugUtil.SRLog("call setSearchViewMode");
        
        SRDataSource datasource = new SRDataSource(mContext);
        datasource.open();
        
        ArrayList<SRTagDb> tags = datasource.getAllTag();
        
        datasource.close();
        SRDebugUtil.SRLog("tags = "+tags);
        
        mListView = (ListView)findViewById(R.id.SRSearchListView);
        
        tagListAdapter = new SRTagListAdapter(mContext, R.layout.sr_tag_list, tags);
        mListView.setAdapter(tagListAdapter);
        
        mListView.setTextFilterEnabled(true);
        
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

//            	view.setSelected(true);
//           	SRDebugUtil.SRLog("SRSearchView : onItemClick() pos = " + position);
////                
               mController.playBySearchList(tagListAdapter.getTagDb(position));
               
            }  
        });
        
        mListView.setOnItemLongClickListener(new OnItemLongClickListener(){
        	@Override
        	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
        			int position, long id) {
        		// TODO Auto-generated method stub
//        		SRDebugUtil.SRLog("call setOnItemLongClickListener");
//        		SRDebugUtil.SRLog("tagListAdapter.getTagDb(position) = " + tagListAdapter.getTagDb(position));
        		mController.showDeleteTagDialog(tagListAdapter.getTagDb(position));
        		return true;
        	}
        });
        
        

    }
    
    
}


