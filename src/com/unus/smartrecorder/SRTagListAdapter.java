package com.unus.smartrecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
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
	
	private int tagNumber = 1;
	private static final int TITLE_TYPE = 1;
	private static final int TAG_TYPE = 2;
	int layout;
	
	public SRTagListAdapter(Context context, int alayout, ArrayList<SRTagDb> tags) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mTags = tags;
		this.mOriginalTags = tags;
		this.tagNumber = 1;
		layout = alayout;
	}
	
	
	public void add(SRTagDb tag){
		mTags.add(tag);
		notifyDataSetChanged();
	}
	
	public void remove(SRTagDb tag){
		//mTags.re
		for (int index = 0 ; index < mTags.size() ; index++){
			if(mTags.get(index).getTag_id() == tag.getTag_id()){
				mTags.remove(index);
				break;
			}
		}
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
		int layoutType = TAG_TYPE;
		
		int intTagTime = Integer.parseInt(tagTime);
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
		
		SRTagDb tag = mTags.get(position);

		// declaration
		TextView text = (TextView) convertView.findViewById(R.id.tagListTitle);
		RelativeLayout tagListMainLayout = (RelativeLayout) convertView
				.findViewById(R.id.tagListMainLayout);
		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.tagListImage);
		ImageView tagIconView = (ImageView) convertView
				.findViewById(R.id.tagIcon);
		Resources res = mContext.getResources();
		/** from an Activity */

		// init
		tagListMainLayout.setPadding(20, 0, 0, 0);
		imageView.setImageResource(android.R.color.transparent);
		tagIconView.setImageResource(android.R.color.transparent);

		if (tag.getIsTitleType()){
			tagIconView.setImageDrawable(res
					.getDrawable(R.drawable.voice_labels));
		}
		else if (tag.getType() == SRDbHelper.TEXT_TAG_TYPE) {

			tagIconView.setImageDrawable(res
					.getDrawable(R.drawable.text_labels));
		} else if (tag.getType() == SRDbHelper.PAGE_TAG_TYPE) {

			tagIconView.setImageDrawable(res
					.getDrawable(R.drawable.doc_labels));
		} else if (tag.getType() == SRDbHelper.PHOTO_TAG_TYPE) {

			File imgFile = new File(tag.getContent());

			if (imgFile.exists()) {

				try {
					// Drawable d =
					// Drawable.createFromPath(imgFile.getAbsolutePath());
					// imageView.setImageDrawable(d);

					SRDebugUtil.SRLog("imgFile.getAbsolutePath() = "
							+ imgFile.getAbsolutePath());
					// Bitmap myBitmap =
					// BitmapFactory.decodeFile("/mnt/sdcard/DCIM/100LGDSC/test.jpg");

					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 8;
					BitmapFactory.decodeStream(
							new FileInputStream(imgFile), null, options);

					final int REQUIRED_SIZE = 70;

					// Find the correct scale value. It should be the power
					// of 2.
					int scale = 1;
					while (options.outWidth / scale / 2 >= REQUIRED_SIZE
							&& options.outHeight / scale / 2 >= REQUIRED_SIZE)
						scale *= 2;

					// Decode with inSampleSize
					BitmapFactory.Options o2 = new BitmapFactory.Options();
					o2.inSampleSize = scale;
					Bitmap myBitmap = BitmapFactory.decodeStream(
							new FileInputStream(imgFile), null, o2);
					imageView.setImageBitmap(myBitmap);

				} catch (FileNotFoundException e) {

				}
			} else {

				imageView.setImageDrawable(res
						.getDrawable(R.drawable.no_pic));
			}
			tagIconView.setImageDrawable(res
					.getDrawable(R.drawable.pic_labels));
		}
		text.setText(tag.getTagListTitle());

		return convertView;

	}
	
	private String getImageFileName(String content) {
		String contents[] = content.split("/");
		return contents[contents.length-1];
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
                    SRDebugUtil.SRLog("constraint.toString() = " + constraint.toString());
                    //System.out.println("text.indexOf('23'); = " + text.indexOf("TA"));
                    if (tag.getContent().indexOf(constraint.toString()) != -1){
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
