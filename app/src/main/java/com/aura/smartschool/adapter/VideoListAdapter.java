package com.aura.smartschool.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.aura.smartschool.R;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.VideoVO;

import java.util.ArrayList;

public class VideoListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<VideoVO> mVideoList;
	private MemberVO mMember;
	private int mType;
	private ArrayList<String> mSupportedTitle;

	public VideoListAdapter(Context context, MemberVO member, ArrayList<VideoVO> videoList, int type	) {
		mContext = context;
		mVideoList = videoList;
		mMember = member;
		mType = type;
		mSupportedTitle = getSupprtedVideoTitle();
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

		if (isSupportVideo(mVideoList.get(position).title)) {
			holder.lockLayout.setVisibility(View.GONE);
		} else {
			holder.lockLayout.setVisibility(View.VISIBLE);
			holder.lockLayout.getLayoutParams().width = holder.iv_video_img.getLayoutParams().width;
			holder.lockLayout.getLayoutParams().height = holder.iv_video_img.getLayoutParams().height;
			holder.lockLayout.bringToFront();
			holder.lockLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Util.showAlertDialog(v.getContext(), v.getContext().getResources().getString(R.string.popup_alert_nodata));
				}
			});
			holder.iv_video_img.setOnClickListener(null);
		}

		return convertView;
	}

	private boolean isSupportVideo(String title) {
		if (mMember.isVIPUser()) {
			return true;
		} else {
			for (String supportedTitle : mSupportedTitle) {
				if (title.contains(supportedTitle)) {
					return true;
				}
			}
			return false;
		}
	}

	//1: pt 화면, 2:키, 3: 체중, 4:bmi
	private ArrayList<String> getSupprtedVideoTitle() {
		ArrayList<String> titles = new ArrayList<>();
		if (mType == 1) {
			titles.add("플러러 킥");
			titles.add("서킷트레이닝 3단계");
			titles.add("누워서 엇갈려 손 발 닿기");
		} else if (mType == 2) {
			titles.add("손으로 원 그리기");
		} else if (mType == 3) {
			titles.add("양발모아 뛰기");
		} else if (mType == 4) {
			titles.add("밴드로 발끝 당기고 상체 숙이기");
		}
		return titles;
	}

	class ViewHolder {
		ImageView iv_video_img;
		TextView tv_title;
		TextView tv_duration;

		View lockLayout;
	}
}
