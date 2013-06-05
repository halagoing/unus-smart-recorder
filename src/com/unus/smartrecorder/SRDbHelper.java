package com.unus.smartrecorder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SRDbHelper extends SQLiteOpenHelper{
	
	private static final String DB_NAME = "SMART_RECORDER";
    private static final int DB_VERSION = 1;
	
    // voice table
    private static final String TABLE_VOICE = "voice";
    private static final String VOICE_COLUMN_VOICE_ID = "voice_id";
    private static final String VOICE_COLUMN_CREATED_DATETIME = "created_datetime";
    private static final String VOICE_COLUMN_VOICE_PATH = "voice_path";
    private static final String VOICE_COLUMN_DOCUMENT_PATH = "document_path";
    
    private static final String VOICE_CREATE = "create table"
    		+ TABLE_VOICE + "(" + VOICE_COLUMN_VOICE_ID
    		+ " INTEGER primary key autoincrement, " + VOICE_COLUMN_CREATED_DATETIME
    		+ " DATETIME default current_timestamp, " + VOICE_COLUMN_VOICE_PATH
    		+ " TEXT not null, " + VOICE_COLUMN_DOCUMENT_PATH
    		+ " TEXT);";
    
    // tag table
    private static final String TABLE_TAG = "tag";
    private static final String TAG_COLUMN_TAG_ID = "tag_id";
    private static final String TAG_COLUMN_CREATED_DATETIME = "created_datetime";
    private static final String TAG_COLUMN_VOICE_ID = "voice_id";
    private static final String TAG_COLUMN_TYPE = "type";
    private static final String TAG_COLUMN_CONTENT = "content";
    private static final String TAG_COLUMN_TAG_TIME = "tag_time";
    
    private static final int TEXT_TAG_TYPE = 1;
    private static final int PHOTO_TAG_TYPE = 2;
    private static final int PAGE_TAG_TYPE = 3;
    
    private static final String TAG_CREATE = "create table"
    		+ TABLE_TAG + "(" + TAG_COLUMN_TAG_ID
    		+ " INTEGER primary key autoincrement, " + TAG_COLUMN_CREATED_DATETIME
    		+ " DATETIME default current_timestamp, " + TAG_COLUMN_VOICE_ID
    		+ " INTEGER not null, " + TAG_COLUMN_TYPE
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
		db.execSQL("DROP TABLE IF EXISTS" + TABLE_VOICE);
		db.execSQL("DROP TABLE IF EXISTS" + TABLE_TAG);
		onCreate(db);
	}
}
