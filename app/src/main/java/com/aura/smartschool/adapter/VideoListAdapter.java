package com.aura.smartschool.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.aura.smartschool.Interface.MemberListListener;
import com.aura.smartschool.R;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.vo.VideoVO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class VideoListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<VideoVO> mVideoList;
	private MemberListListener mMemberListListener;

	private int videoDiffDate;
	private int hourToNextDate;
	private int minuteToNextHour;

	public VideoListAdapter(Context context, ArrayList<VideoVO> videoList) {
		mContext = context;
		mVideoList = videoList;

		calculateVideoDiffDate();
	}

	private void calculateVideoDiffDate() {
		long videoDate = 0;
		try {
			videoDate = new SimpleDateFormat("yyyy-MM-dd").parse(PreferenceUtil.getInstance(mContext).getVideoDate()).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			videoDate = new Date().getTime();
		}
		Calendar today = Calendar.getInstance();

		videoDiffDate = (int) ((today.getTimeInMillis() - videoDate) / (24 * 60 * 60 * 1000));
		minuteToNextHour = 60 - today.get(Calendar.MINUTE);
		hourToNextDate = 24 - today.get(Calendar.HOUR_OF_DAY) - (minuteToNextHour != 60 ? 1 : 0);
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
			holder.lockLayout = convertView.findViewById(R.id.ll_lock);
			holder.tvLockDate = (TextView) convertView.findViewById(R.id.tv_lock_date);
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
				// 미디어파일 플레이 하기
				Uri uri = Uri.parse(mVideoList.get(position).video_url);
				Intent it = new Intent(Intent.ACTION_VIEW);
				it.setDataAndType(uri, "video/mp4");
				mContext.startActivity(it);
			}
		});

		if (position * 7 > videoDiffDate) {
			int remainDate = (position * 7) - videoDiffDate - 1;
			holder.lockLayout.setVisibility(View.VISIBLE);
			holder.lockLayout.getLayoutParams().width = holder.iv_video_img.getLayoutParams().width;
			holder.lockLayout.getLayoutParams().height = holder.iv_video_img.getLayoutParams().height;
			StringBuilder sb = new StringBuilder()
					.append(remainDate > 0 ? remainDate + "일 " : "")
					.append(hourToNextDate > 0 ? hourToNextDate+"시간 ":"")
					.append(minuteToNextHour != 60 ? minuteToNextHour : 0).append("분 후 OPEN");
			holder.tvLockDate.setText(sb.toString());
			holder.lockLayout.bringToFront();
			holder.iv_video_img.setOnClickListener(null);
		} else {
			holder.lockLayout.setVisibility(View.GONE);
		}

		return convertView;
	}

	class ViewHolder {
		ImageView iv_video_img;
		TextView tv_title;
		TextView tv_duration;

		View lockLayout;
		TextView tvLockDate;
	}
}
