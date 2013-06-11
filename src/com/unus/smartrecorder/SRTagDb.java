package com.unus.smartrecorder;

import android.view.View;

public class SRTagDb {
	private long tag_id;
	private String created_datetime;
	private long voice_id;
	private int type;
	private String numbering;
	private String content;
	private String tag_time;
	private Boolean isTitleType;
	
	
	public Boolean getIsTitleType() {
		return isTitleType;
	}
	public void setIsTitleType(Boolean isTitleType) {
		this.isTitleType = isTitleType;
	}
	public String getNumbering() {
		return numbering;
	}
	public void setNumbering(String numbering) {
		this.numbering = numbering;
	}
	public long getTag_id() {
		return tag_id;
	}
	public void setTag_id(long tag_id) {
		this.tag_id = tag_id;
	}
	public String getCreated_datetime() {
		return created_datetime;
	}
	public void setCreated_datetime(String created_datetime) {
		this.created_datetime = created_datetime;
	}
	public long getVoice_id() {
		return voice_id;
	}
	public void setVoice_id(long voice_id) {
		this.voice_id = voice_id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTag_time() {
		return tag_time;
	}
	public void setTag_time(String tag_time) {
		this.tag_time = tag_time;
	}
	
	public String getTagListTitle(){
		String tagTitle = "";
		if(getIsTitleType()){
			tagTitle = "00:00 | " + getContent();
		}
		else{
			if (getType() == SRDbHelper.TEXT_TAG_TYPE) {
				tagTitle = " " + getTime() + " | " + getContent();
			} else if (getType() == SRDbHelper.PAGE_TAG_TYPE) {
				tagTitle = " " + getTime() + " | Page# " + getContent();
			} else if (getType() == SRDbHelper.PHOTO_TAG_TYPE) {
				tagTitle = " " + getTime() + " | " + getImageFileName(getContent());
			}
		}
		return tagTitle;
	}
	
	private String getTime() {
		int t = Integer.parseInt(getTag_time());
        int sec = t / 1000;
        int h, m, s, tmp;

        if (sec < 3600) {
            h = 0;
            m = sec / 60;
            s = sec % 60;
        } else {
            h = sec / 3600;
            tmp = sec % 3600;
            m = tmp / 60;
            s = tmp % 60;
        }
        return String.format("%d:%02d:%02d", h, m, s);

    }
	

	private String getImageFileName(String content) {
		String contents[] = content.split("/");
		return contents[contents.length-1];
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return content;
	}
	
}
