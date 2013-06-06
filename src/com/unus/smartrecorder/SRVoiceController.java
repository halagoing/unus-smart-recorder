package com.unus.smartrecorder;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SRVoiceController implements SRVoiceControllerInterface {
    public static final int DIALOG_INPUT_BASIC_INFO = 1; // Input Basic Info Dialog
    public static final int DIALOG_INPUT_TEXT_TAG = 2; // Input Text Tag Dialog
    
    public static final int FILE_EXPLORER_RESULT = 1;   // document file browsing
    
    private SRVoiceInterface mModel;
    private Activity mActivity;
    private Context mContext;
    private SRVoiceView mView;
    
    private EditText mTitleView;    // Basic Info Dialog
    private TextView mDocPathView;  // Basic Info Dialog
    private EditText mTextTagView;  // Text Tag Dialog
    
    private long mTagTime;
    
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
    
    public SRVoiceController(SRVoiceInterface model, Activity activity) {
        super();
        mModel = model;
        mActivity = activity;
        mContext = mActivity;
        mView = new SRVoiceView(mContext, this);
        
        mModel.initialize(mContext);
        mModel.registerObserver(mView);
    }
    
    public SRVoiceView getView() {
        return mView;
    }

    @Override
    public void record() {
        // show Input Basic Info Dialog
        mActivity.showDialog(DIALOG_INPUT_BASIC_INFO);
        
    }

    @Override
    public void recordStop() {
        mModel.recordStop();
        Toast.makeText(mContext, R.string.stop, Toast.LENGTH_SHORT).show();
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
                                    
                                    // Set Title, DocFilePath
                                    mModel.setTitle(mTitleView.getText().toString());
                                    mActivity.getActionBar().setTitle(mModel.getTitle());
                                    
                                    mModel.setDocFilePath(mDocPathView.getText().toString());
                                    mView.setDocPath(mDocPathView.getText().toString());
                                    // Record Start
                                    mModel.recordStart();
                                    Toast.makeText(mContext, R.string.record, Toast.LENGTH_SHORT).show();
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
                                    mModel.addTag(SRDbHelper.TEXT_TAG_TYPE, mTextTagView.getText().toString(), Long.toString(mTagTime));
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
        default:
            break;    
        }
    }
}