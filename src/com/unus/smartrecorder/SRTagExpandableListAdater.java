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
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class SRTagExpandableListAdater extends BaseExpandableListAdapter implements Filterable{
	
	Context mContext;
	ArrayList<SRVoiceDb> mRecorders;
	ArrayList<SRVoiceDb> mOriginalRecorders;
	LayoutInflater mInflater;
	
	
	private static final int TITLE_TYPE = 1;
	private static final int TAG_TYPE = 2;
	
	public SRTagExpandableListAdater(Context context, ArrayList<SRVoiceDb> recorders) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mRecorders = recorders;
		mOriginalRecorders = recorders;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return mRecorders.get(groupPosition).getmTagList().get(childPosition);
	}
	
	public void removeChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		//return mRecorders.get(groupPosition).getmTagList().get(childPosition);
		mRecorders.get(groupPosition).getmTagList().remove(childPosition);
		notifyDataSetChanged();
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return mRecorders.get(groupPosition).getmTagList().size();
	}

	

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return mRecorders.get(groupPosition);
	}
	
	public void removeGroup(int groupPosition) {
		mRecorders.remove(groupPosition);
		notifyDataSetChanged();
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return mRecorders.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//return null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sr_expandable_voice_list, parent,false);
        }
		
		TextView recorder_title = (TextView) convertView.findViewById(R.id.recorder_title);
		recorder_title.setText(getVoiceFileName(getGroup(groupPosition).toString()));
		
//		convertView.setOnLongClickListener(new OnLongClickListener() {
//			
//			@Override
//			public boolean onLongClick(View arg0) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		});
		
        return convertView;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sr_tag_list, viewGroup,false);
        }
		
		// data
		SRTagDb tag = mRecorders.get(groupPosition).getmTagList().get(childPosition);
		
		// declaration
		TextView text = (TextView)convertView.findViewById(R.id.tagListTitle);
		RelativeLayout tagListMainLayout = (RelativeLayout)convertView.findViewById(R.id.tagListMainLayout);
		ImageView imageView = (ImageView)convertView.findViewById(R.id.tagListImage);
		ImageView tagIconView = (ImageView)convertView.findViewById(R.id.tagIcon);
		Resources res = mContext.getResources(); /** from an Activity */
		
		// init
		tagListMainLayout.setPadding(20, 0, 0, 0);
		imageView.setImageResource(android.R.color.transparent);
		tagIconView.setImageResource(android.R.color.transparent);
		
		// set
		switch (getLayoutType(tag.getTag_time())) {
		case TITLE_TYPE:
			tagIconView.setImageDrawable(res
					.getDrawable(R.drawable.voice_labels));
			text.setText(tag.getTagListTitle());
		case TAG_TYPE:

			// tagListMainLayout.setBackgroundResource(R.id.t)
			// tagIconView.setImageDrawable(res.getDrawable(R.drawable.test));

			if (tag.getType() == SRDbHelper.TEXT_TAG_TYPE) {

				tagIconView.setImageDrawable(res
						.getDrawable(R.drawable.text_labels));
			} else if (tag.getType() == SRDbHelper.PAGE_TAG_TYPE) {

				tagIconView.setImageDrawable(res
						.getDrawable(R.drawable.doc_labels));
				// tagTitle = "Page is "+tag.getContent();
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
			break;
		default:
			break;
		}

		return convertView;
	}
	
	private String getVoiceFileName(String content) {
		String contents[] = content.split("/");
		return contents[contents.length-1];
	}

	private int getLayoutType(String tagTime) {
		int layoutType = TAG_TYPE;
		
		int intTagTime = Integer.parseInt(tagTime);
		if(intTagTime==0){
			layoutType = TITLE_TYPE;

		}
		return layoutType;
	}
	private  ArrayList<SRVoiceDb> getAllRecorders(){
		SRDataSource datasource = new SRDataSource(mContext);
        datasource.open();
    
        ArrayList<SRVoiceDb> recorders = datasource.getAllRecorder();

        datasource.close();
        return recorders;
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
            	SRDebugUtil.SRLog("publishResults(CharSequence constraint, FilterResults results)");
            	mRecorders = (ArrayList<SRVoiceDb>) results.values;
            	notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                SRDebugUtil.SRLog("FilterResults performFiltering");
                ArrayList<SRVoiceDb> filteredArrList = new ArrayList<SRVoiceDb>();
                if(constraint == null || constraint.length() == 0){
                    ArrayList<SRVoiceDb> recorders = getAllRecorders();
                	results.count = recorders.size();
                	results.values = recorders;
				} else {
					constraint = constraint.toString();
					SRDebugUtil.SRLog("FilterResults performFiltering");
					ArrayList<SRVoiceDb> recorders = getAllRecorders();
					for (int indexRecorder = 0; indexRecorder < recorders.size(); indexRecorder++) {
						SRVoiceDb tempVoice = recorders.get(indexRecorder);
						//System.arraycopy(mOriginalRecorders.get(indexRecorder), 0, tempVoice, 0, 1);

						ArrayList<SRTagDb> tags = tempVoice.getmTagList();
						SRDebugUtil.SRLog("indexTag tags = " + tags);
						
						ArrayList<SRTagDb> tempTags = new ArrayList<SRTagDb>();

						for (int indexTag = 0 ; indexTag < tags.size() ; indexTag++){
							if (tags.get(indexTag).getTagListTitle().contains(constraint)){
								tempTags.add(tags.get(indexTag));
							};
						}
						if(tempTags.size() != 0) {
							tempVoice.setmTagList(tempTags);
							filteredArrList.add(tempVoice);
						}
					}

					results.count = filteredArrList.size();
					results.values = filteredArrList;
				}

                
                return results;
            }
        };

        return filter;
	}
	
	
}
