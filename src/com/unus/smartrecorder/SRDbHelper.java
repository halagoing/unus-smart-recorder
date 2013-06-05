package com.unus.smartrecorder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SRDbHelper extends SQLiteOpenHelper{
	
	private static final String DB_NAME = Config.DB_NAME;
    private static final int DB_VERSION = Config.DB_VERSION;
	
    public SRDbHelper(Context context) {
		// TODO Auto-generated constructor stub
    	super(context, DB_NAME, null, DB_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// create Table
    	db.execSQL(getCreateVoiceTableQuery());
    	db.execSQL(getCreateTagTableQuery());
	}
	
	private String getCreateVoiceTableQuery() {
		String query = "create table "+Config.DB_VOICE_TABLE_NAME+"("+
    	"voice_id INTEGER primary key autoincrement,"+
    	"created_time DATETIME default current_timestamp,"+
    	"voice_path TEXT not null,"+
    	"document_path TEXT)";
		return query;
	}
	
	private String getCreateTagTableQuery() {
		String query = "create table "+Config.DB_TAG_TABLE_NAME+"("+
    	"tag_id INTEGER primary key autoincrement,"+
    	"created_time DATETIME default current_timestamp,"+
    	"content TEXT not null,"+
    	"voice_id INTEGER not null,"+
    	"tag_time TIME not null)";
		return query;
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
}
