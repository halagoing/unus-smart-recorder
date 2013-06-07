package com.unus.smartrecorder;

import java.util.ArrayList;
import java.util.List;





import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/*
 * Context maincon;
	LayoutInflater Inflater;
	ArrayList<MyItem> arSrc;
	int layout;
	
	maincon = context;
		Inflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		arSrc = aarSrc;
		layout = alayout;
 */
public class SRTagListAdapter extends BaseAdapter{
	Context mContext;
	LayoutInflater Inflater;
	ArrayList<SRTagDb> tags;
	int layout;
	
	public SRTagListAdapter(Context context, int alayout, ArrayList<SRTagDb> tags) {
		// TODO Auto-generated constructor stub
		mContext = context;
		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.tags = tags;
		layout = alayout;
	}
	
	public void add(SRTagDb tag){
		tags.add(tag);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tags.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return tags.get(position).getContent();
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int pos = position;
		if (convertView == null) {
			convertView = Inflater.inflate(layout, parent, false);
		}
		
		String tagTitle = "";
		SRTagDb tag = tags.get(position);
		
		if(tag.getType() == SRDbHelper.TEXT_TAG_TYPE){
			tagTitle = "Tag#"+position+" "+tag.getContent();
		}
		else if(tag.getType() == SRDbHelper.PAGE_TAG_TYPE){
			tagTitle = "Tag#"+position+" Page is "+tag.getContent();
		}
		else if(tag.getType() == SRDbHelper.PHOTO_TAG_TYPE){
			ImageView image = (ImageView)convertView.findViewById(R.id.tagListImage);
			Resources res = mContext.getResources(); /** from an Activity */
			image.setImageDrawable(res.getDrawable(R.drawable.test));
			tagTitle = "Tag#"+position+" Image path "+tag.getContent();
		}
		TextView text = (TextView)convertView.findViewById(R.id.tagListTitle);
		text.setText(tagTitle);
		return convertView;

	}
	
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
		
	}
	
	public SRTagDb getTagDb(int position) {
	    return tags.get(position);
	}
}
