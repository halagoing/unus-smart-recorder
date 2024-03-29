package com.unus.smartrecorder;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


/*
 * SRDataSource
 */
public class SRDataSource {
	private SQLiteDatabase database;
	private SRDbHelper dbHelper;
	private int mNubering = 0; 
	private ArrayList<SRVoiceDb> tempVoiceList;
	
	// voice table all columns
	private String[] allVoiceColumns = {SRDbHelper.VOICE_COLUMN_VOICE_ID,SRDbHelper.VOICE_COLUMN_CREATED_DATETIME,
			SRDbHelper.VOICE_COLUMN_VOICE_PATH,SRDbHelper.VOICE_COLUMN_DOCUMENT_PATH,SRDbHelper.VOICE_COLUMN_STATE};
	
	// tag table all columns
	private String[] allTagColumns = {SRDbHelper.TAG_COLUMN_TAG_ID,SRDbHelper.TAG_COLUMN_CREATED_DATETIME,
			SRDbHelper.TAG_COLUMN_VOICE_ID,SRDbHelper.TAG_COLUMN_NUMBERING,SRDbHelper.TAG_COLUMN_TYPE,SRDbHelper.TAG_COLUMN_CONTENT,SRDbHelper.TAG_COLUMN_TAG_TIME};
	
	public SRDataSource (Context context) {
		// init dbHelper
		dbHelper = new SRDbHelper(context);
	}
	
	// when start using database
	public void open() throws SQLException{
		// get database from dbhelper
		database = dbHelper.getWritableDatabase();
	}
	
	// when end using database
	public void close() {
		dbHelper.close();
	}
	
	
	public ArrayList<SRVoiceDb> getAllRecorder(){
		ArrayList<SRVoiceDb> resultAllRecorder = new ArrayList<SRVoiceDb>();
		
	    Cursor cursor = database.query(SRDbHelper.TABLE_VOICE,
	    		allVoiceColumns, SRDbHelper.VOICE_COLUMN_STATE + " = " + SRDbHelper.VOICE_COMPLETION , null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	SRVoiceDb voice = cursorToVoice(cursor);
	    	ArrayList<SRTagDb> tags = getTagByVoiceId(voice.getVoice_id());
	    	voice.setmTagList(tags);
	    	resultAllRecorder.add(voice);
	    	cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return resultAllRecorder;
	}
	
	public SRVoiceDb createVoice(String voiceFilePath, String docFilePath){
    	ContentValues values = new ContentValues();
    	values.put(SRDbHelper.VOICE_COLUMN_VOICE_PATH, voiceFilePath);
    	values.put(SRDbHelper.VOICE_COLUMN_DOCUMENT_PATH, docFilePath);
    	long insertId = database.insert(SRDbHelper.TABLE_VOICE, null, values);
//    	SRDebugUtil.SRLog("insertId = "+ insertId);
    	Cursor cursor = database.query(SRDbHelper.TABLE_VOICE,
    			allVoiceColumns, SRDbHelper.VOICE_COLUMN_VOICE_ID + " = " + insertId, null,
    	        null, null, null);
    	cursor.moveToFirst();
    	SRVoiceDb resultVoice = cursorToVoice(cursor);
    	return resultVoice;
	}
	
	private SRVoiceDb cursorToVoice(Cursor cursor) {
		SRVoiceDb voice = new SRVoiceDb();
		voice.setVoice_id(cursor.getLong(0));
		voice.setCreated_datetime(cursor.getString(1));
		voice.setVoice_path(cursor.getString(2));
		voice.setDocument_path(cursor.getString(3));
		voice.setState(cursor.getInt(4));
		return voice;
	}
	
	public void deleteVoice(SRVoiceDb voice) {
		long voice_id = voice.getVoice_id();
		database.delete(SRDbHelper.TABLE_VOICE, SRDbHelper.VOICE_COLUMN_VOICE_ID
		    + " = " + voice_id, null);
    }
	
	public void updateVoiceState(SRVoiceDb voice){
		long voice_id = voice.getVoice_id();
		ContentValues args = new ContentValues();
	    args.put(SRDbHelper.VOICE_COLUMN_STATE, SRDbHelper.VOICE_COMPLETION);
	    database.update(SRDbHelper.TABLE_VOICE, args, SRDbHelper.VOICE_COLUMN_VOICE_ID + " = " + voice_id, null);
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
	
	public SRVoiceDb getVoiceByVoiceId(long voice_id){
		Cursor cursor = database.query(SRDbHelper.TABLE_VOICE,
    			allVoiceColumns, SRDbHelper.VOICE_COLUMN_VOICE_ID + " = " + voice_id, null,
    	        null, null, null);
    	cursor.moveToFirst();
    	cursor.moveToFirst();
    	SRVoiceDb newVoice = cursorToVoice(cursor);
    	return newVoice;
	}
	
	public SRTagDb createTag(long voice_id, String numbering,int type, String content, String tag_time){
    	ContentValues values = new ContentValues();
    	values.put(SRDbHelper.TAG_COLUMN_VOICE_ID, voice_id);
    	values.put(SRDbHelper.TAG_COLUMN_NUMBERING, numbering);
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
		tag.setNumbering(cursor.getString(3));
		tag.setType(cursor.getInt(4));
		tag.setContent(cursor.getString(5));
		tag.setTag_time(cursor.getString(6));
		tag.setIsTitleType(isFirstTag(tag.getTag_time()));

		return tag;
	}
	
	private Boolean isFirstTag(String tagTime) {
		int intTagTime = Integer.parseInt(tagTime);
		Boolean isFirstTag = false;
		if (intTagTime == 0) {
			isFirstTag = true;
		}
		return isFirstTag;
	}
	
	
//	private String getNumbering(){
//		return String.format("%03d",mNubering);
//	}
	
	public void deleteTag(SRTagDb tag) {
		long tag_id = tag.getTag_id();
		database.delete(SRDbHelper.TABLE_TAG, SRDbHelper.TAG_COLUMN_TAG_ID
		    + " = " + tag_id, null);
    }
	public void deleteTagsByVoiceId(long voice_id){
		
		database.delete(SRDbHelper.TABLE_TAG, SRDbHelper.TAG_COLUMN_VOICE_ID
		    + " = " + voice_id, null);
	}
	public void deleteDocTagByVoiceId(long voice_id) {
		database.delete(SRDbHelper.TABLE_TAG, SRDbHelper.TAG_COLUMN_VOICE_ID
			    + " = " + voice_id+ " and "+SRDbHelper.TAG_COLUMN_TYPE+ " = " + SRDbHelper.PAGE_TAG_TYPE, null);
	}
	
	public ArrayList<SRTagDb> getAllTag() {
		ArrayList<SRTagDb> tags = new ArrayList<SRTagDb>();

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
	
	public ArrayList<SRTagDb> getTagByVoiceId(long voice_id) {
		ArrayList<SRTagDb> tags = new ArrayList<SRTagDb>();

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
	
	public ArrayList<SRTagDb> getDocTagByVoiceId(long voice_id) {
		ArrayList<SRTagDb> tags = new ArrayList<SRTagDb>();

	    Cursor cursor = database.query(SRDbHelper.TABLE_TAG,
	    		allTagColumns, SRDbHelper.TAG_COLUMN_VOICE_ID+" = "+voice_id + " and "+SRDbHelper.TAG_COLUMN_TYPE+ " = "+SRDbHelper.PAGE_TAG_TYPE , null, null, null, null);

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
