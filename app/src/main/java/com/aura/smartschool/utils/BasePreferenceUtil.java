package com.aura.smartschool.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

public class BasePreferenceUtil {
	private SharedPreferences _sharedPreferences;
	 
	protected BasePreferenceUtil(Context context) {
		super();
		_sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	protected void put(String key, String value) {
		SharedPreferences.Editor editor = _sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	protected String get(String key) {
		return _sharedPreferences.getString(key, "");
	}

	protected void put(String key, boolean value) {
		SharedPreferences.Editor editor = _sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	protected boolean get(String key, boolean defValue) {
		return _sharedPreferences.getBoolean(key, defValue);
	}

	protected void put(String key, int value) {
		SharedPreferences.Editor editor = _sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	protected int get(String key, int defValue) {
		return _sharedPreferences.getInt(key, defValue);
	}

	protected void put(String key, float value) {
		SharedPreferences.Editor editor = _sharedPreferences.edit();
		editor.putFloat(key, value);
		editor.commit();
	}
	protected float get(String key, float defValue) {
		return _sharedPreferences.getFloat(key, defValue);
	}

	protected void put(String key, Set<String> set) {
		SharedPreferences.Editor editor = _sharedPreferences.edit();
		editor.putStringSet(key, set);
		editor.commit();
	}
	protected Set<String> get(String key, Set<String> defValue) {
		return _sharedPreferences.getStringSet(key, defValue);
	}
}
