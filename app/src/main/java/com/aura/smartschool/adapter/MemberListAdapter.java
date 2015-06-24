package com.aura.smartschool.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aura.smartschool.Interface.MemberListListener;
import com.aura.smartschool.R;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.MemberVO;

import java.util.ArrayList;

public class MemberListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<MemberVO> mMemberList;
	private MemberListListener mMemberListListener;
	
	public MemberListAdapter(Context context, ArrayList<MemberVO> memberList, MemberListListener memberListListener) {
		mContext = context;
		mMemberList = memberList;
		mMemberListListener = memberListListener;
	}
	
	public void setData(ArrayList<MemberVO> memberList) {
		mMemberList = memberList;
	}

	@Override
	public int getCount() {
		return mMemberList.size();
	}

	@Override
	public MemberVO getItem(int position) {
		return mMemberList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.adapter_member_list, null);
			holder.iv_user_image = (ImageView) convertView.findViewById(R.id.iv_user_image);
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			holder.tvRelation = (TextView) convertView.findViewById(R.id.tvRelation);
			holder.tvMdn = (TextView) convertView.findViewById(R.id.tvMdn);
			holder.btnModify = (Button) convertView.findViewById(R.id.btnModify);
			//holder.btnView = (Button) convertView.findViewById(R.id.btnView);
			holder.school_info = (LinearLayout) convertView.findViewById(R.id.school_info);
			holder.tvSchool = (TextView) convertView.findViewById(R.id.tv_school);
			//holder.tvSchoolHomepage = (TextView) convertView.findViewById(R.id.tv_school_homepage);
			holder.tvSchoolContact = (TextView) convertView.findViewById(R.id.tv_school_contact);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tvName.setText(mMemberList.get(position).name);
		holder.tvRelation.setText(mMemberList.get(position).relation);
		holder.tvMdn.setText(mMemberList.get(position).mdn);
		
		String userImage = mMemberList.get(position).photo;
		if(!TextUtils.isEmpty(userImage)){
			holder.iv_user_image.setImageBitmap(Util.StringToBitmap(userImage));
		}else{
			holder.iv_user_image.setImageBitmap(null);
		}

		if(mMemberList.get(position).is_parent > 0) {
			holder.school_info.setVisibility(View.GONE);
		} else {
			holder.school_info.setVisibility(View.VISIBLE);
			holder.tvSchool.setText(mMemberList.get(position).mSchoolVO.school_name);
			holder.tvSchool.append("  ");
			holder.tvSchool.append(mMemberList.get(position).mSchoolVO.school_grade);
			holder.tvSchool.append(" - ");
			holder.tvSchool.append(mMemberList.get(position).mSchoolVO.school_class);
			//holder.tvSchoolHomepage.setText(mMemberList.get(position).mSchoolVO.homepage);
			holder.tvSchoolContact.setText(mMemberList.get(position).mSchoolVO.contact);
		}
		
		holder.btnModify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMemberListListener.onUpdateClicked(position);
			}
		});

		convertView.findViewById(R.id.ll_member_info).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMemberListListener.onSelected(position);
			}
		});
		
		/*holder.btnView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMemberListListener.onSelected(position);
			}
		});*/
		
		return convertView;
	}

	class ViewHolder {
		ImageView iv_user_image;
		TextView tvName;
		TextView tvRelation;
		TextView tvMdn;
		LinearLayout school_info;
		TextView tvSchool;
		//TextView tvSchoolHomepage;
		TextView tvSchoolContact;
		Button btnModify;
		//Button btnView;
	}
}
