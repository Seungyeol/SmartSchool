package com.aura.smartschool.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.aura.smartschool.Constant;
import com.aura.smartschool.Interface.MemberListListener;
import com.aura.smartschool.LoginManager;
import com.aura.smartschool.R;
import com.aura.smartschool.utils.SchoolLog;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.MemberVO;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

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
		final ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.adapter_member_list, null);
			holder.iv_user_image = (ImageView) convertView.findViewById(R.id.iv_user_image);
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			holder.tvPayment = (TextView) convertView.findViewById(R.id.tvPayment);
			holder.tvRelation = (TextView) convertView.findViewById(R.id.tvRelation);
			holder.tvMdn = (TextView) convertView.findViewById(R.id.tvMdn);
			holder.btnModify = (Button) convertView.findViewById(R.id.btnModify);
			holder.btnRemove = (Button) convertView.findViewById(R.id.btnRemove);
			//holder.btnView = (Button) convertView.findViewById(R.id.btnView);
			holder.school_info = (LinearLayout) convertView.findViewById(R.id.school_info);
			holder.tvSchool = (TextView) convertView.findViewById(R.id.tv_school);
			//holder.tvSchoolHomepage = (TextView) convertView.findViewById(R.id.tv_school_homepage);
			holder.tvSchoolContact = (TextView) convertView.findViewById(R.id.tv_school_contact);
			holder.tv_current_location = (TextView) convertView.findViewById(R.id.tv_current_location);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvName.setText(mMemberList.get(position).name);
		holder.tvPayment.setText(mMemberList.get(position).isVIPUser() ? "통합서비스" : "기본서비스");
		holder.tvPayment.setBackgroundResource(mMemberList.get(position).isVIPUser() ? R.drawable.bg_yellow_fill : R.drawable.bg_gray_fill);
		holder.tvRelation.setText(mMemberList.get(position).relation);
		if(TextUtils.isEmpty(mMemberList.get(position).mdn)) {
			holder.tvMdn.setText("전화번호 없음");
		} else {
			holder.tvMdn.setText(mMemberList.get(position).mdn);
		}

		
		String userImage = mMemberList.get(position).photo;
		if(!TextUtils.isEmpty(userImage)){
			holder.iv_user_image.setImageBitmap(Util.StringToBitmap(userImage));
		}else{
			holder.iv_user_image.setImageBitmap(null);
		}

		if(mMemberList.get(position).is_parent > 0) {
			//통합 or 기본 서비스 감추기
			holder.tvPayment.setVisibility(View.GONE);
			//학교 정보
			holder.school_info.setVisibility(View.GONE);
			//위치정보
			holder.tv_current_location.setVisibility(View.GONE);
		} else {
			holder.tvPayment.setVisibility(View.VISIBLE);
			holder.school_info.setVisibility(View.VISIBLE);
			holder.tv_current_location.setVisibility(View.VISIBLE);
			holder.tvSchool.setText(mMemberList.get(position).mSchoolVO.school_name);
			holder.tvSchool.append("  ");
			holder.tvSchool.append(mMemberList.get(position).mSchoolVO.school_grade);
			holder.tvSchool.append(" - ");
			holder.tvSchool.append(mMemberList.get(position).mSchoolVO.school_class);
			//holder.tvSchoolHomepage.setText(mMemberList.get(position).mSchoolVO.homepage);
			holder.tvSchoolContact.setText(mMemberList.get(position).mSchoolVO.contact);
		}

		//convertView 재사용시 초기화
		holder.tv_current_location.setText("위치정보없음 or gps 꺼짐");

		if (LoginManager.getInstance().getLoginUser().is_parent == 1) {
			if (StringUtils.equals(LoginManager.getInstance().getLoginUser().mdn, mMemberList.get(position).mdn)) {
				holder.btnRemove.setVisibility(View.GONE);
			} else {
				holder.btnRemove.setVisibility(View.VISIBLE);
			}
		} else {
			holder.btnRemove.setVisibility(View.GONE);
		}

		holder.btnModify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMemberListListener.onUpdateClicked(position);
			}
		});

		holder.btnRemove.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
				alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						mMemberListListener.onRemoveClicked(mMemberList.get(position));
					}
				});
				alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				alert.setMessage("삭제 하시겠습니까?");
				alert.show();
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

		//자녀의 경우 위치 조회
		if(mMemberList.get(position).is_parent == 0) {
			AQuery aq = new AQuery(convertView);
			try {
				String url = Constant.HOST + Constant.API_GET_LASTLOCATION;

				JSONObject json = new JSONObject();
				json.put("member_id", mMemberList.get(position).member_id);

				SchoolLog.d("LDK", "url:" + url);
				SchoolLog.d("LDK", "input parameter:" + json.toString(1));

				aq.post(url, json, JSONObject.class, new AjaxCallback<JSONObject>() {
					@Override
					public void callback(String url, JSONObject object, AjaxStatus status) {
						try {
							if (status.getCode() != 200) {
								return;
							}
							SchoolLog.d("LDK", "result:" + object.toString(1));

							if ("0".equals(object.getString("result"))) {
								JSONObject data = object.getJSONObject("data");
								double lat = 0;
								double lng = 0;
								if (data != null) {
									lat = Double.parseDouble(data.getString("lat"));
									lng = Double.parseDouble(data.getString("lng"));
									mMemberList.get(position).lat = lat;
									mMemberList.get(position).lng = lng;
								}
								long term = Util.getLastedMinuteToCurrent(data.getString("created_date"));

								holder.tv_current_location.setText(Util.getAddress(mContext, lat, lng));
								holder.tv_current_location.append("(" + Util.convertLongToDate(term) + " 전)");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return convertView;
	}

	class ViewHolder {
		ImageView iv_user_image;
		TextView tvName;
		TextView tvPayment;
		TextView tvRelation;
		TextView tvMdn;
		LinearLayout school_info;
		TextView tvSchool;
		//TextView tvSchoolHomepage;
		TextView tvSchoolContact;
		Button btnModify;
		Button btnRemove;
		//Button btnView;
		TextView tv_current_location;
	}
}
