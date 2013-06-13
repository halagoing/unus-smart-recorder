/*
 *             SRDataSource datasource = new SRDataSource(mContext);
            datasource.open();
//          
            datasource.createVoice("testVoicePath", "testDocPath");
////            
//            datasource.createTag(2, 1, "good2", "00:01");
//            
//            List<SRVoiceModel> voices = datasource.getAllVoice();
//            
            List<SRTagModel> tags = datasource.getTagByVoiceId((long) 2);
//            SRDebugUtil.SRLog("voices = "+voices);
//            SRDebugUtil.SRLog("tags = "+tags);
 */

package com.unus.smartrecorder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SRDbHelper extends SQLiteOpenHelper{
	
	private static final String DB_NAME = "SMART_RECORDER";
    private static final int DB_VERSION = 13;
    static final int VOICE_COMPLETION = 1;
    // voice table
    static final String TABLE_VOICE = "voice";
    static final String VOICE_COLUMN_VOICE_ID = "voice_id";
    static final String VOICE_COLUMN_CREATED_DATETIME = "created_datetime";
    static final String VOICE_COLUMN_VOICE_PATH = "voice_path";
    static final String VOICE_COLUMN_DOCUMENT_PATH = "document_path";
    static final String VOICE_COLUMN_STATE = "state";
    
    private static final String VOICE_CREATE = "create table "
    		+ TABLE_VOICE + "(" + VOICE_COLUMN_VOICE_ID
    		+ " INTEGER primary key autoincrement, " + VOICE_COLUMN_CREATED_DATETIME
    		+ " DATETIME default current_timestamp, " + VOICE_COLUMN_VOICE_PATH
    		+ " TEXT not null, " + VOICE_COLUMN_DOCUMENT_PATH
    		+ " TEXT, " + VOICE_COLUMN_STATE 
    		+ " INTEGER default 0);";
    
    // tag table
    static final String TABLE_TAG = "tag";
    static final String TAG_COLUMN_TAG_ID = "tag_id";
    static final String TAG_COLUMN_CREATED_DATETIME = "created_datetime";
    static final String TAG_COLUMN_VOICE_ID = "voice_id";
    static final String TAG_COLUMN_NUMBERING = "numbering";
    static final String TAG_COLUMN_TYPE = "type";
    static final String TAG_COLUMN_CONTENT = "content";
    static final String TAG_COLUMN_TAG_TIME = "tag_time";
    
    public static final int TEXT_TAG_TYPE = 1;
    public static final int PHOTO_TAG_TYPE = 2;
    public static final int PAGE_TAG_TYPE = 3;
    
    private static final String TAG_CREATE = "create table "
    		+ TABLE_TAG + "(" + TAG_COLUMN_TAG_ID
    		+ " INTEGER primary key autoincrement, " + TAG_COLUMN_CREATED_DATETIME
    		+ " DATETIME default current_timestamp, " + TAG_COLUMN_VOICE_ID
    		+ " INTEGER not null, " + TAG_COLUMN_NUMBERING
    		+ " TEXT not null, " + TAG_COLUMN_TYPE
    		+ " INTEGER default "+ TEXT_TAG_TYPE +", " + TAG_COLUMN_CONTENT
    		+ " TEXT not null, " + TAG_COLUMN_TAG_TIME
    		+ " TEXT not null);";
    

    
    public SRDbHelper(Context context) {
		// TODO Auto-generated constructor stub
    	super(context, DB_NAME, null, DB_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// create Table
    	db.execSQL(VOICE_CREATE);
    	db.execSQL(TAG_CREATE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOICE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
		onCreate(db);
	}
}
