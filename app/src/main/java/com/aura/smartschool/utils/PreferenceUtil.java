package com.aura.smartschool.utils;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;

public class PreferenceUtil extends BasePreferenceUtil {
	private static PreferenceUtil sInstance = null;

	public static synchronized PreferenceUtil getInstance(Context context) {
		if (sInstance == null)
			sInstance = new PreferenceUtil(context);
		return sInstance;
	}

	private PreferenceUtil(Context context) {
		super(context);
	}

	public void putStartState(boolean started) {
		put("started", started);
	}

	public boolean getStartState() {
		return get("started", false);
	}

	public void putRegID(String regId) {
		put("registration_id", regId);
	}

	public String getRegID() {
		return get("registration_id");
	}

/*	public void putAppVersion(int appVersion) {
		put(PROPERTY_APP_VERSION, appVersion);
	}

	public int appVersion() {
		return get(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	}*/
	
	public void putHomeId(String homeId) {
		put("home_id", homeId);
	}

	public String getHomeId() {
		return get("home_id");
	}
	
	//자기 이름 저장
	public void putName(String name) {
		put("name", name);
	}
	
	public String getName() {
		return get("name");
	}
	
	//부모인지 아닌지 저장
	public void putParent(boolean isParent) {
		put("isParent", isParent);
	}
	public boolean isParent() {
		return get("isParent", false);
	}

	//mobile id 저장
	public void putMemberId(int member_id) {
		put("member_id", member_id);
	}
	public int getMemberId() {
		return get("member_id", 0);
	}

	public void putAppVersion(int appVersion) {
		put("appVersion", appVersion);
	}
	public int appVersion() {
		return get("appVersion", Integer.MIN_VALUE);
	}

	//location time post time : second
	public void setLocationTime(int timestamp) {
		put("location_time", timestamp);
	}
	public int getLocationTime() {
		return get("location_time", 0);
	}

	public void setLat(double lat) {
		put("lat", (float)lat);
	}
	public double getLat() {
		return get("lat", 0f);
	}

	public void setLng(double lng) {
		put("lng", (float)lng);
	}
	public double getLng() {
		return get("lng", 0f);
	}

	public void setWeight(double weight) {
		put("weight", (float)weight);
	}
	public double getWeight() {
		return get("weight", 0f);
	}

	public void setHeight(double height) {
		put("height", (float)height);
	}
	public double getHeight() {
		return get("height", 0f);
	}

	public void setSOSEnabled(boolean enabled) {
		put("sos_enabled", enabled);
	}

	public boolean isSOSEnabled() {
		return get("sos_enabled", false);
	}

	public void setSosCoordX(int x) {
		put("sos_coord_x", x);
	}

	public int getSosCoordX() {
		return get("sos_coord_x", 0);
	}

	public void setSosCoordY(int y) {
		put("sos_coord_y", y);
	}

	public int getSosCoordY() {
		return get("sos_coord_y", 0);
	}

	public void putGuardianInfo(int idx, String name, String phoneNumber) {
		put("guardianName" + idx, name);
		put("guardianPhoneNumber" + idx, phoneNumber);
	}

	public String[] getGuardianInfo(int idx) {
		String[] result = new String[2];
		result[0] = get("guardianName" + idx);
		result[1] = get("guardianPhoneNumber" + idx);
		return result;
	}

	public void putSOSMessage(String msg) {
		put("SOSMessage", msg);
	}

	public String getSOSMessage() {
		String result = get("SOSMessage");
		if (StringUtils.isEmpty(result)) {
			return "[SOS] 긴급상황입니다. 도와주세요!!!";
		} else {
			return result;
		}
	}

	public void setGuideAddMemberShowed(boolean isShowed) {
		put("guide_add_member", isShowed);
	}

	public boolean isGuideAddMemberShowed() {
		return get("guide_add_member", false);
	}

	public void setInstallMemberEnable(boolean enable) {
		put("guide_install_member", enable);
	}

	public boolean getInstallMemberEnable() {
		return get("guide_install_member", true);
	}

	//학교 위도 경도 정보 저장
	public void putSchool_lat(String school_lat) {
		put("school_lat", school_lat);
	}
	public String getSchool_lat() {
		return get("school_lat");
	}

	public void putSchool_lng(String school_lng) {
		put("school_lng", school_lng);
	}
	public String getSchool_lng() {
		return get("school_lng");
	}
}
