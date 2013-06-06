package com.unus.smartrecorder;

public class SRTagDb {
	private long tag_id;
	private String created_datetime;
	private long voice_id;
	private int type;
	private String content;
	private String tag_time;
	
	
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
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return content;
	}
	
}
