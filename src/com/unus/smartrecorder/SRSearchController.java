package com.unus.smartrecorder;

import android.app.Activity;
import android.content.Context;

public class SRSearchController implements SRSearchControllerInterface{
	
	private SRSearchInterface mModel;
    private Activity mActivity;
	private Context mContext;
	private SRSearchView mView;
	
	public SRSearchController(Activity activity) {
		// TODO Auto-generated constructor stub
		super();
        mActivity = activity;
        mContext = mActivity;
        mView = new SRSearchView(mContext, this);

	}
	
	public SRSearchView getView() {
        return mView;
    }
}
