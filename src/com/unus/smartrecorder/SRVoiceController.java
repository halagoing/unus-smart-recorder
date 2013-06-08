package com.unus.smartrecorder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.NetworkInfo.DetailedState;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

public class SRVoiceController implements SRVoiceControllerInterface {
    public static final int DIALOG_INPUT_BASIC_INFO = 1; // Input Basic Info Dialog
    public static final int DIALOG_INPUT_TEXT_TAG = 2; // Input Text Tag Dialog
    
    public static final int FILE_EXPLORER_RESULT = 1;   // document file browsing
    public static final int TAKE_PICTURE_RESULT = 2;   // camera
    
    private SRVoiceInterface mModel;
    private Activity mActivity;
    private Context mContext;
    private SRVoiceView mSRVoiceView;
    private SRSearchView mSRSearchView;
    private MenuItem mActionBarSearchItem;
    private MenuItem mActionBarAddItem;
    private MenuItem mActionBarShareItem;
    private SearchView mSearchView;
    
    private EditText mTitleView;    // Basic Info Dialog
    private TextView mDocPathView;  // Basic Info Dialog
    private EditText mTextTagView;  // Text Tag Dialog
    
    private int mTagTime;
    
	private static final String DOC_PATTERN = "([^*]+(\\.(?i)(pdf))$)";
	private static final Boolean Boolean = null;
	private Pattern pattern;
	private Matcher matcher;
    
	
	
    // for show Keyboard 
    private EditText mActiveEditText;
    private Runnable mShowImeRunnable = new Runnable() {
        public void run() {
            InputMethodManager imm = (InputMethodManager)
                    mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.showSoftInput(mActiveEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    };    
    
    public void checkFileExists(){
    	
    			
    			
    	SRDataSource datasource = new SRDataSource(mContext);
        datasource.open();
        // {{TESTCODE
        
        //mContext.bindService(service, conn, flags)
        
        
        List<SRVoiceDb> voiceList = datasource.getAllVoice();

        for (int index =0 ; index < voiceList.size();  index++){
        	SRVoiceDb voiceDb = voiceList.get(index);
        	SRDebugUtil.SRLog("voiceDb getVoice_path" + voiceDb.getVoice_path());
        	File voiceFile = new  File(voiceDb.getVoice_path());
        	if(!voiceFile.exists()){
        		datasource.deleteTagsByVoiceId(voiceDb.getVoice_id());
        		datasource.deleteVoice(voiceDb);
        		SRDebugUtil.SRLog("no voiceFile!!!!");
        		continue;
        	}
        	SRDebugUtil.SRLog("voiceDb.getDocument_path() = " +voiceDb.getDocument_path());
        	if(!voiceDb.getDocument_path().equals("")){
//        		SRDebugUtil.SRLog("doc check!!!!");
        		File docFile = new  File(voiceDb.getDocument_path());
            	if(!docFile.exists()){
            		datasource.deleteDocTagByVoiceId(voiceDb.getVoice_id());
            		SRDebugUtil.SRLog("no docFile!!!!");
            		
            	}
        	}
        	
        	SRDebugUtil.SRLog("con??");
        }
        
        ArrayList<SRTagDb> tagList = datasource.getAllTag();
        
        for (int index =0 ; index < tagList.size();  index++){
        	SRTagDb tagDb = tagList.get(index);
        	if(tagDb.getType()==SRDbHelper.PHOTO_TAG_TYPE){
        		File picFile = new  File(tagDb.getContent());
        		if(!picFile.exists()){
            		datasource.deleteTag(tagDb);
            	}
        	}
        }
        
        
        
        datasource.close();
    }
    
    public SRVoiceController(SRVoiceInterface model, Activity activity) {
        super();
        mModel = model;
        mActivity = activity;
        mContext = mActivity;
        mSRVoiceView = new SRVoiceView(mContext, this);
        mSRSearchView = new SRSearchView(mContext, this);
        
        mModel.initialize(mContext);
        mModel.registerObserver(mSRVoiceView);
    }
    
    public void finalize() {
        if (mModel != null) {
            mModel.removeObserver(mSRVoiceView);
            mModel.finalize();
        }
        if (mSRVoiceView != null) {
            //TODO: if need
        }
        mActionBarSearchItem = null;
        mActionBarAddItem = null;
        mActionBarShareItem = null;
    }
    
    /**
     * Set Recorder, Player, Search View
     * 
     * @param mode
     */
    @Override
    public void setViewMode(int mode) {
        mModel.setMode(mode);
        
        if (SRVoice.RECORDER_MODE == mode) {
            mSRVoiceView.setVoiceViewMode(SRVoice.RECORDER_MODE);
            mActivity.setContentView(mSRVoiceView);
            
            //ActionBar : only Search
            if (mActionBarAddItem != null)
                mActionBarAddItem.setVisible(false);
            if (mActionBarSearchItem != null)
                mActionBarSearchItem.setVisible(true);
            if (mActionBarShareItem != null)
                mActionBarShareItem.setVisible(false);
            
        } else if (SRVoice.PLAYER_MODE == mode) {
            mSRVoiceView.setVoiceViewMode(SRVoice.PLAYER_MODE);
            mActivity.setContentView(mSRVoiceView);
            
            //ActionBar : All
            if (mActionBarAddItem != null)
                mActionBarAddItem.setVisible(true);
            if (mActionBarSearchItem != null)
                mActionBarSearchItem.setVisible(true);
            if (mActionBarShareItem != null)
                mActionBarShareItem.setVisible(true);
            
        } else if (SRVoice.SEARCH_MODE == mode) {
        	mSRSearchView.setSearchViewMode();
            mActivity.setContentView(mSRSearchView);
            
            //ActionBar : only Search
            if (mActionBarAddItem != null)
                mActionBarAddItem.setVisible(false);
            if (mActionBarSearchItem != null)
                mActionBarSearchItem.setVisible(true);
            if (mActionBarShareItem != null)
                mActionBarShareItem.setVisible(false);
        }
    }
    
    public SRVoiceView getVoiceView() {
        return mSRVoiceView;
    }
    
    public SRVoiceView getSearchView() {
        return mSRVoiceView;
    }

    @Override
    public void record() {
        // show Input Basic Info Dialog
        mActivity.showDialog(DIALOG_INPUT_BASIC_INFO);
        
    }

    @Override
    public void recordStop() {
        mModel.recordStop();
        //Toast.makeText(mContext, R.string.stop, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void tagText() {
        // show Input Basic Info Dialog
        mActivity.showDialog(DIALOG_INPUT_TEXT_TAG);
        mTagTime = mModel.getCurrentRecordTime();
    }
    
    @Override
    public void tagPhoto() {
        // move to Camera
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        mActivity.startActivityForResult(intent, TAKE_PICTURE_RESULT);
        mTagTime = mModel.getCurrentRecordTime();
    }
    
    @Override
    public Dialog createDialog(int id) {
        LayoutInflater factory = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch(id) {
        case DIALOG_INPUT_BASIC_INFO:
            final View inputBasicInfoView = factory.inflate(
                    R.layout.sr_input_basic_info_dialog, null);
            mTitleView = (EditText)inputBasicInfoView.findViewById(R.id.titleText);
            mDocPathView = (TextView)inputBasicInfoView.findViewById(R.id.docPathText);
            mDocPathView.setSelected(true);
            final ImageButton explorerBtn = (ImageButton)inputBasicInfoView.findViewById(R.id.explorerBtn);
            explorerBtn.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    showFileExplorer();
                }
            });
            
            return new AlertDialog.Builder(mContext)
                    .setTitle(R.string.input_basic_info)
                    .setView(inputBasicInfoView)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                	
                                	String voiceTitle = mTitleView.getText().toString();
                            		String docFilePath = mDocPathView.getText().toString();
                            		String resultMsg = "Start Recording";
                            		if(isNull(voiceTitle)){
                            			resultMsg = "제목을 입력하세요!!";
                            		}
                            		else if(!validateDocFilePath(docFilePath)){
                            			resultMsg = "pdf 파일을 선택하세요!!";
                            		}
                            		else{
                            			 mModel.setTitle(voiceTitle);
                                         mActivity.getActionBar().setTitle(mModel.getTitle());
                                         mModel.setDocFilePath(docFilePath);
                                         mSRVoiceView.setDocPath(docFilePath);
                                         mSRVoiceView.initRecorderTagListView();
                                         mModel.recordStart();
                            		}
                            		Toast.makeText(mContext, resultMsg, Toast.LENGTH_SHORT).show();
                    
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    mModel.setTitle(null);
                                    mModel.setDocFilePath(null);
                                }
                            }).create();
            
        case DIALOG_INPUT_TEXT_TAG:
            final View inputTextTagView = factory.inflate(
                    R.layout.sr_input_text_tag_dialog, null);
            mTextTagView = (EditText)inputTextTagView.findViewById(R.id.textTagText);
            return new AlertDialog.Builder(mContext)
                    .setTitle(R.string.input_text_tag)
                    .setView(inputTextTagView)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                	
                                    // add Text Tag
                                    mModel.addTag(SRDbHelper.TEXT_TAG_TYPE, mTextTagView.getText().toString(), Integer.toString(mTagTime));
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {

                                    // do nothing
                                }
                            }).create();
        default:
            return null;
        }
    }
    
    private Boolean	isNull(String voiceTitle) {
    	Boolean result = false;
    	if (voiceTitle==null || voiceTitle.length() ==0){
    		result = true;
		}
    	return result;
	}
    
    private Boolean validateDocFilePath(String docFilePath) {
    	if (isNull(docFilePath)) return true;
    	SRDebugUtil.SRLog("docFilePath = " + docFilePath);
    	pattern = Pattern.compile(DOC_PATTERN);
		matcher = pattern.matcher(docFilePath);
		return matcher.matches();
	}
    
    @Override
    public void prepareDialog(int id, Dialog dialog, Bundle args) {
        switch(id) {
        case DIALOG_INPUT_BASIC_INFO:
            mTitleView.setText(mModel.makeDefaultTitle());
            mTitleView.selectAll();
            mTitleView.requestFocus();
            mActiveEditText = mTitleView;
            mTitleView.post(mShowImeRunnable);

            mDocPathView.setText("");
            break;
            
        case DIALOG_INPUT_TEXT_TAG:
            mTextTagView.setText("");
            mTextTagView.requestFocus();
            mActiveEditText = mTextTagView;
            mTextTagView.post(mShowImeRunnable);
            break;

        default:
            break;
        }
    }
    
    /**
     * For File browsing
     * launch File explorer (ex. Astro app)
     */
    public void showFileExplorer() {
        final PackageManager packageManager = mContext.getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
        //intent.setType("file/*");
        intent.setType("application/pdf");
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                                        PackageManager.GET_ACTIVITIES);

        if (list.size() > 0) {
            mActivity.startActivityForResult(intent, FILE_EXPLORER_RESULT);
        } else {
            SRDebugUtil.SRLogError("File Explorer Activity Not Found");
            Toast.makeText(mContext, R.string.file_explorer_not_found, Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * get real path by Uri
     * for camera photo tag
     * 
     * @param contentUri
     * @return
     */
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = mActivity.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    
    public void activityResult(int requestCode, int resultCode, Intent data)  {
        switch(requestCode) {
        case FILE_EXPLORER_RESULT:
            if (resultCode == Activity.RESULT_OK) {
                String filePath = data.getData().getPath();
                
                SRDebugUtil.SRLog("onActivityResult() DocumentPath:" + filePath);
                
                mDocPathView.setText(filePath);
            } else {

            }
            break;
        case TAKE_PICTURE_RESULT:
            if (resultCode == Activity.RESULT_OK) {
                String filePath = getRealPathFromURI(data.getData());
                
                SRDebugUtil.SRLog("onActivityResult() PhotoPath:" + filePath);
                
                mModel.addTag(SRDbHelper.PHOTO_TAG_TYPE, filePath, Integer.toString(mTagTime));
            } else {
                mTagTime = 0;
            }
            break;
        default:
            break;    
        }
    }
   
    /**
     * Search View에서 Tag를 선택했을때 재생 
     */
    @Override
    public void playBySearchList(SRTagDb tagDb) {
        // ActionBar 이전 상태로 이동 
        if (mActionBarSearchItem != null)
            mActionBarSearchItem.collapseActionView();
            
        // 녹음중일 경우 녹음 정지 
        if (mModel.getPrevMode() == SRVoice.RECORDER_MODE
                && mModel.isRecordering()) {
            mModel.recordStop();
        }
        
        // Player 모드 전환 
        setViewMode(SRVoice.PLAYER_MODE);
        
        // voice id와 tag time으로 재생 
        long voiceId = tagDb.getVoice_id();
        String tagTime = tagDb.getTag_time();        
        SRVoiceDb voiceDb =  mModel.getDataSource().getVoiceByVoiceId(voiceId);        
        ArrayList<SRTagDb> tagsDb = mModel.getDataSource().getTagByVoiceId(voiceId);
        
        if (voiceDb == null) {
            SRDebugUtil.SRLogError("playBySearchList() : voiceId is not valid");
            return;
        }
        String voicePath = voiceDb.getVoice_path();
        String docPath = voiceDb.getDocument_path();       
        
        // Tag List update
        mModel.setTagList(tagsDb);
        
        // Document Display
        if (docPath != null && docPath.length() > 0) {
            SRDebugUtil.SRLog("playBySearchList(): Doc = " + docPath);
            mSRVoiceView.setDocPath(docPath);
            //mSRVoiceView.setDocPage(10);
            
            if (tagDb.getType() == SRDbHelper.PAGE_TAG_TYPE) {
                mSRVoiceView.setDocPage(Integer.parseInt(tagDb.getContent()));
            } else {
                ArrayList<SRTagDb> tags = mModel.getDataSource().getDocTagByVoiceId(voiceId);
                if (tags.size() > 0) {
                    int time = Integer.parseInt(tagDb.getTag_time());
                    int i;
                    for (i = tags.size() - 1; i >= 0; i--) {
                        SRTagDb pageTag = tags.get(i);
                        int pageTagTime = Integer.parseInt(pageTag.getTag_time());
                        if (pageTagTime < time) {
                            mSRVoiceView.setDocPage(Integer.parseInt(tagDb.getContent()));
                            break;
                        }
                    }
                }
            }
        }
        
        // Play
        mModel.play(voicePath, Integer.parseInt(tagTime));
        
        // Action Bar Title
        mActivity.getActionBar().setTitle(mModel.makeVoicePathToTitle(voicePath));
    }
    
    /**
     * Play toggle button
     */
    @Override
    public void playByPlayToggleBtn() {
        mModel.playToggle();
    }
    
    /**
     * Play Stop button
     */
    @Override
    public void playStop() {
        mModel.playStop();
    }
    
    private void setupSearchView(MenuItem searchItem) {

        searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
            
            @Override
            public boolean onQueryTextSubmit(String query) {
                SRDebugUtil.SRLog("Query = " + query + " : submitted");
                return false;
            }
            
            @Override
            public boolean onQueryTextChange(String newText) {
                SRDebugUtil.SRLog("Query = " + newText);
                if (TextUtils.isEmpty(newText)) {
                    mSRSearchView.clearTextFilter();
                } else {
                    mSRSearchView.setFilterText(newText.toString());
                }                
                return false;
            }
        });
        mSearchView.setOnCloseListener(new OnCloseListener() {
            
            @Override
            public boolean onClose() {
                SRDebugUtil.SRLog("onClose()");
                return false;
            }
        });
    }
    
    /**
     * ActionBar menu create (Search, Share etc)
     * 
     * @param menu
     * @return
     */
    public boolean createOptionMenu(Menu menu) {
        MenuInflater inflater = mActivity.getMenuInflater();
        inflater.inflate(R.menu.searchview_in_menu, menu);
        mActionBarSearchItem = menu.findItem(R.id.action_search);
        mActionBarAddItem = menu.findItem(R.id.action_add);
        mActionBarShareItem = menu.findItem(R.id.action_share);
        
        //Default : Recorder
        mActionBarAddItem.setVisible(false);
        mActionBarShareItem.setVisible(false);

        mActionBarSearchItem.setOnActionExpandListener(new OnActionExpandListener() {
    
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                SRDebugUtil.SRLog("onMenuItemActionExpand()");
    
                //setViewState(STATE_SEARCHING);
                setViewMode(SRVoice.SEARCH_MODE);
                return true;
            }
    
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                SRDebugUtil.SRLog("onMenuItemActionCollapse()");
    
                setViewMode(mModel.getPrevMode());                
                return true;
            }
        });
    
        mSearchView = (SearchView) mActionBarSearchItem.getActionView();
        setupSearchView(mActionBarSearchItem);
        return true;
    }

    /**
     * ActionBar Menu (Add, Share)
     * 
     * @param item
     */
    public void optionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.action_add:
            SRDebugUtil.SRLog("ActionBar: add");
            if (mModel.getMode() == SRVoice.PLAYER_MODE) {
                // Stop Player
                playStop();
                
                // Record
                setViewMode(SRVoice.RECORDER_MODE);
                record();
            } else {
                SRDebugUtil.SRLogError("ERROR: Not in PLAYER_MODE");
            }
            break;
        case R.id.action_share:
            SRDebugUtil.SRLog("ActionBar: share");
            break;
        }
    }
    
    /*
     * User Pressed back button
     * 
     */
    
    public void backPressed() {
		//SRDebugUtil.SRLog("gppd!");
		if(mModel.isRecordering()) mModel.recordStop();
		else mActivity.finish();
	}
    
	@Override
	public void jumpToggleBtn(Boolean rewind) {
		// TODO Auto-generated method stub
		//mModel.playJump(rewind);

		int curTime = mModel.getCurrentPlayTime();
		if (rewind) {
		    mModel.seekTo(curTime - SRVoice.JUMP_TIME);
		} else {
		    mModel.seekTo(curTime + SRVoice.JUMP_TIME);
		}
	}
	
	@Override
	public void playBySeekTime(int seekTime) {
		// TODO Auto-generated method stub
		mModel.seekTo(seekTime);
	}

    @Override
    public void docPageChanged(int page) {
        SRDebugUtil.SRLog("docPageChanged(): " + page);
        
        if (mModel.getMode() == SRVoice.RECORDER_MODE) {
            if (mModel.isRecordering()
                    && mModel.isAutoTag()) {
                mModel.addTag(SRDbHelper.PAGE_TAG_TYPE, Integer.toString(page), Integer.toString(mModel.getCurrentRecordTime()));                
            }
        } else if (mModel.getMode() == SRVoice.PLAYER_MODE) {
            // Do nothing
        }
    }

    @Override
    public void changeAutoTag(boolean isChecked) {
        mModel.setAutoTag(isChecked);
    }
}
