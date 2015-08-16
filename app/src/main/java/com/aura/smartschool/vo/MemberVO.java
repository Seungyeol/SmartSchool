package com.aura.smartschool.vo;

import android.util.Log;

import com.aura.smartschool.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Calendar;

public class MemberVO implements Serializable {
	public int member_id;
	public String home_id;
	public String mdn;
	public String name;
	public String relation;
	public int is_parent = 1; //0:학생, 1:부모
	public String photo;
	public String sex;
	public String birth_date;
	//위치정보
	public double lat;
	public double lng;
	//유료회원일 경우 지급 날짜
	public String pay_date;

	//자녀 정보
	public SchoolVO mSchoolVO;

	//측정정보
	public MeasureSummaryVO mMeasureSummaryVO;
	
	private static final long serialVersionUID = 6631779405103025795L;
	
	public MemberVO () {
		mSchoolVO = new SchoolVO();
	}
	
	public MemberVO(String home_id, String mdn) {
		this.home_id = home_id;
		this.mdn = mdn;
	}

	public boolean isVIPUser() {
		if (StringUtils.isEmpty(pay_date) || "null".equals(pay_date)) {
			return false;
		}
		Calendar payDate = Calendar.getInstance();
		payDate.setTime(Util.getDateFromString(pay_date));
		int monthDay = payDate.getActualMaximum(Calendar.DAY_OF_MONTH);
		payDate.add(Calendar.DATE, monthDay - 1);
		return payDate.after(Calendar.getInstance());
	}
}
