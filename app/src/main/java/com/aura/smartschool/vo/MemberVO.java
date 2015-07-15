package com.aura.smartschool.vo;

import java.io.Serializable;

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
}
