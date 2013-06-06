package com.unus.smartrecorder;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SRDataSource {
	private SQLiteDatabase database;
	private SRDbHelper dbHelper;
	
	private String[] allVoiceColumns = {SRDbHelper.VOICE_COLUMN_VOICE_ID,SRDbHelper.VOICE_COLUMN_CREATED_DATETIME,
			SRDbHelper.VOICE_COLUMN_VOICE_PATH,SRDbHelper.VOICE_COLUMN_DOCUMENT_PATH};
	
	private String[] allTagColumns = {SRDbHelper.TAG_COLUMN_TAG_ID,SRDbHelper.TAG_COLUMN_CREATED_DATETIME,
			SRDbHelper.TAG_COLUMN_VOICE_ID,SRDbHelper.TAG_COLUMN_TYPE,SRDbHelper.TAG_COLUMN_CONTENT,SRDbHelper.TAG_COLUMN_TAG_TIME};
	
	public SRDataSource (Context context) {
		dbHelper = new SRDbHelper(context);
	}
	
	public void open() throws SQLException{
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public SRVoiceDb createVoice(String voiceFilePath, String docFilePath){
    	ContentValues values = new ContentValues();
    	values.put(SRDbHelper.VOICE_COLUMN_VOICE_PATH, voiceFilePath);
    	values.put(SRDbHelper.VOICE_COLUMN_DOCUMENT_PATH, docFilePath);
    	long insertId = database.insert(SRDbHelper.TABLE_VOICE, null, values);
    	SRDebugUtil.SRLog("insertId = "+ insertId);
    	Cursor cursor = database.query(SRDbHelper.TABLE_VOICE,
    			allVoiceColumns, SRDbHelper.VOICE_COLUMN_VOICE_ID + " = " + insertId, null,
    	        null, null, null);
    	cursor.moveToFirst();
    	SRVoiceDb newVoice = cursorToVoice(cursor);
    	return newVoice;
//    	SRVoiceModel voice = new SRVoiceModel();
//    	return voice;
	}
	
	private SRVoiceDb cursorToVoice(Cursor cursor) {
		SRVoiceDb voice = new SRVoiceDb();
		voice.setVoice_id(cursor.getLong(0));
		voice.setCreated_datetime(cursor.getString(1));
		voice.setVoice_path(cursor.getString(2));
		voice.setDocument_path(cursor.getString(3));
		return voice;
	}
	
	public void deleteVoice(SRVoiceDb voice) {
		long voice_id = voice.getVoice_id();
		database.delete(SRDbHelper.TABLE_VOICE, SRDbHelper.VOICE_COLUMN_VOICE_ID
		    + " = " + voice_id, null);
    }
	
	public List<SRVoiceDb> getAllVoice() {
	    List<SRVoiceDb> voices = new ArrayList<SRVoiceDb>();

	    Cursor cursor = database.query(SRDbHelper.TABLE_VOICE,
	    		allVoiceColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	SRVoiceDb voice = cursorToVoice(cursor);
	    	voices.add(voice);
	    	cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return voices;
	}
	
	public SRTagDb createTag(int voice_id, int type, String content, String tag_time){
    	ContentValues values = new ContentValues();
    	values.put(SRDbHelper.TAG_COLUMN_VOICE_ID, voice_id);
    	values.put(SRDbHelper.TAG_COLUMN_TYPE, type);
    	values.put(SRDbHelper.TAG_COLUMN_CONTENT, content);
    	values.put(SRDbHelper.TAG_COLUMN_TAG_TIME, tag_time);
    	long insertId = database.insert(SRDbHelper.TABLE_TAG, null, values);
    	Cursor cursor = database.query(SRDbHelper.TABLE_TAG,
    			allTagColumns, SRDbHelper.TAG_COLUMN_TAG_ID + " = " + insertId, null,
    	        null, null, null);
    	cursor.moveToFirst();
    	SRTagDb newTag = cursorToTag(cursor);
    	return newTag;
	}
	
	private SRTagDb cursorToTag(Cursor cursor) {
		SRTagDb tag = new SRTagDb();
		tag.setTag_id(cursor.getLong(0));
		tag.setCreated_datetime(cursor.getString(1));
		tag.setVoice_id(cursor.getLong(2));
		tag.setType(cursor.getInt(3));
		tag.setContent(cursor.getString(4));
		tag.setTag_time(cursor.getString(5));
		return tag;
	}
	
	public void deleteTag(SRTagDb tag) {
		long tag_id = tag.getTag_id();
		database.delete(SRDbHelper.TABLE_TAG, SRDbHelper.TAG_COLUMN_TAG_ID
		    + " = " + tag_id, null);
    }
	
	public List<SRTagDb> getAllTag() {
	    List<SRTagDb> tags = new ArrayList<SRTagDb>();

	    Cursor cursor = database.query(SRDbHelper.TABLE_TAG,
	    		allTagColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	SRTagDb tag = cursorToTag(cursor);
	    	tags.add(tag);
	    	cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return tags;
	}
	
	public List<SRTagDb> getTagByVoiceId(long voice_id) {
		List<SRTagDb> tags = new ArrayList<SRTagDb>();

	    Cursor cursor = database.query(SRDbHelper.TABLE_TAG,
	    		allTagColumns, SRDbHelper.TAG_COLUMN_VOICE_ID+" = "+voice_id, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	SRTagDb tag = cursorToTag(cursor);
	    	tags.add(tag);
	    	cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return tags;
	}

}
