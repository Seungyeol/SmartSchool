package com.aura.smartschool.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.aura.smartschool.Interface.MemberListListener;
import com.aura.smartschool.R;
import com.aura.smartschool.vo.VideoVO;

import java.util.ArrayList;

public class VideoListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<VideoVO> mVideoList;
	private MemberListListener mMemberListListener;

	public VideoListAdapter(Context context, ArrayList<VideoVO> videoList) {
		mContext = context;
		mVideoList = videoList;
	}
	
	public void setData(ArrayList<VideoVO> videoList) {
		mVideoList = videoList;
	}

	@Override
	public int getCount() {
		return mVideoList.size();
	}

	@Override
	public VideoVO getItem(int position) {
		return mVideoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.video_list_item, null);
			holder.iv_video_img = (ImageView) convertView.findViewById(R.id.iv_video_img);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_title.setText(mVideoList.get(position).title);
		holder.tv_duration.setText(mVideoList.get(position).duration);

		AQuery aq = new AQuery(convertView);
		aq.id(holder.iv_video_img).image(mVideoList.get(position).image_url);

		holder.iv_video_img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView iv_video_img;
		TextView tv_title;
		TextView tv_duration;
	}
}
