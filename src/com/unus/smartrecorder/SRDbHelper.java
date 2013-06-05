package com.unus.smartrecorder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SRDbHelper extends SQLiteOpenHelper{
	
	private static final String DB_NAME = "test";
    private static final int DB_VERSION = 1;
	
    public SRDbHelper(Context context) {
		// TODO Auto-generated constructor stub
    	super(context, DB_NAME, null, DB_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
    	String sql = "create table voice("+
    	"voice_id INTEGER primary key autoincrement,"+
    	"created_time DATETIME default current_timestamp,"+
    	"voice_path TEXT not null,"+
    	"document_path TEXT)";
    	
    	db.execSQL(sql);
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
