package com.unus.smartrecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;





import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
public class SRTagListAdapter extends BaseAdapter implements Filterable{
	Context mContext;
	LayoutInflater mInflater;
	ArrayList<SRTagDb> mTags;
	ArrayList<SRTagDb> mOriginalTags;
	
	
	private static final int TITLE_TYPE = 1;
	private static final int TAG_TYPE = 2;
	int layout;
	
	public SRTagListAdapter(Context context, int alayout, ArrayList<SRTagDb> tags) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mTags = tags;
		this.mOriginalTags = tags;
		layout = alayout;
	}
	
	public void add(SRTagDb tag){
		mTags.add(tag);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mTags.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mTags.get(position).getContent();
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	private int getLayoutType(String tagTime) {
		int intTagTime = Integer.parseInt(tagTime);
		int layoutType = TAG_TYPE;
		if(intTagTime==0){
			layoutType = TITLE_TYPE;
		}
		return layoutType;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int pos = position;
		if (convertView == null) {
			convertView = mInflater.inflate(layout, parent, false);
		}
		
		String tagTitle = "";
		SRTagDb tag = mTags.get(position);
		
		TextView text = (TextView)convertView.findViewById(R.id.tagListTitle);
		LinearLayout dividingLine = (LinearLayout)convertView.findViewById(R.id.dividingLine);
		dividingLine.setBackgroundColor(Color.WHITE);
		switch (getLayoutType(tag.getTag_time())) {
			case TITLE_TYPE:
				
				//RelativeLayout rl = (RelativeLayout)fin
				//RelativeLayout tagListMainLayout = (RelativeLayout)convertView.findViewById(R.id.tagListMainLayout);
				
				dividingLine.setBackgroundColor(Color.BLACK);
				tagTitle = "Voice_"+tag.getVoice_id()+" "+tag.getContent();
				text.setText(tagTitle);
				break;
			case TAG_TYPE:
				if(tag.getType() == SRDbHelper.TEXT_TAG_TYPE){
					tagTitle = "Tag#"+position+" "+tag.getContent();
				}
				else if(tag.getType() == SRDbHelper.PAGE_TAG_TYPE){
					tagTitle = "Tag#"+position+" Page is "+tag.getContent();
				}
				else if(tag.getType() == SRDbHelper.PHOTO_TAG_TYPE){
					
					File imgFile = new  File(tag.getContent());
					ImageView imageView = (ImageView)convertView.findViewById(R.id.tagListImage);
					
					if(imgFile.exists()){
						
						try{
							//Drawable d = Drawable.createFromPath(imgFile.getAbsolutePath());
							//imageView.setImageDrawable(d);
							
							SRDebugUtil.SRLog("imgFile.getAbsolutePath() = " +imgFile.getAbsolutePath());
//							Bitmap myBitmap = BitmapFactory.decodeFile("/mnt/sdcard/DCIM/100LGDSC/test.jpg");
							
							BitmapFactory.Options options=new BitmapFactory.Options();
							options.inSampleSize = 8;
							BitmapFactory.decodeStream(new FileInputStream(imgFile),null,options);
							
							
							final int REQUIRED_SIZE=70;

					        //Find the correct scale value. It should be the power of 2.
					        int scale=1;
					        while(options.outWidth/scale/2>=REQUIRED_SIZE && options.outHeight/scale/2>=REQUIRED_SIZE)
					            scale*=2;

					        //Decode with inSampleSize
					        BitmapFactory.Options o2 = new BitmapFactory.Options();
					        o2.inSampleSize=scale;
					        Bitmap myBitmap = BitmapFactory.decodeStream(new FileInputStream(imgFile), null, o2);
					        imageView.setImageBitmap(myBitmap);
						}
						catch (FileNotFoundException e){
							
						}
						
						
						
//						Bitmap myBitmap = BitmapFactory.decodeFile("/mnt/sdcard/DCIM/100LGDSC/CAM00016.jpg");
//						imageView.setImageBitmap(myBitmap);
					}
					else{
						Resources res = mContext.getResources(); /** from an Activity */
						imageView.setImageDrawable(res.getDrawable(R.drawable.test));
					}
					
					
					
					
//					
//					
					
					
					tagTitle = "Tag#"+position+" Image path "+tag.getContent();
				}
				text.setText(tagTitle);
				break;
			default:
				break;
		}
		
		
		
		return convertView;

	}
	
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
		
	}
	

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		
		Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
//                arrayList = (List<String>) results.values;
//                notifyDataSetChanged();
            	SRDebugUtil.SRLog("publishResults(CharSequence constraint, FilterResults results)");
            	mTags = (ArrayList<SRTagDb>) results.values;
            	notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                SRDebugUtil.SRLog("FilterResults performFiltering");
                ArrayList<SRTagDb> filteredArrList = new ArrayList<SRTagDb>();
                if(constraint == null || constraint.length() == 0){
                	results.count = mOriginalTags.size();
                	results.values = mOriginalTags;
              	}else{
            	constraint = constraint.toString();
            	
            	for (int index = 0; index < mTags.size(); index++) {
                    SRTagDb tag = mTags.get(index);
                    if (tag.getContent().toLowerCase().startsWith(constraint.toString()))   {
                    	filteredArrList.add(tag);
                    }
                }
            	
            	//mTags.remove(1);
            	results.count = filteredArrList.size();
            	results.values = filteredArrList;
            }


                return results;
            }
        };

        return filter;
	}

	public SRTagDb getTagDb(int position) {
	    return mTags.get(position);

	}
}
