package com.unus.smartrecorder;

public class SRVoiceDb {
	private long voice_id;
	private String created_datetime;
	private String voice_path;
	private String document_path;
	
	public long getVoice_id() {
		return voice_id;
	}
	public void setVoice_id(long voice_id) {
		this.voice_id = voice_id;
	}
	public String getCreated_datetime() {
		return created_datetime;
	}
	public void setCreated_datetime(String created_datetime) {
		this.created_datetime = created_datetime;
	}
	public String getVoice_path() {
		return voice_path;
	}
	public void setVoice_path(String voice_path) {
		this.voice_path = voice_path;
	}
	public String getDocument_path() {
		return document_path;
	}
	public void setDocument_path(String document_path) {
		this.document_path = document_path;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return voice_path;
	}
	
}
