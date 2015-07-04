package com.aura.smartschool.utils;

import android.content.Context;

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

}
