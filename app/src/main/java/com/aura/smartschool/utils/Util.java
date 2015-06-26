package com.aura.smartschool.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class Util {
	//MainApplication에서 주입받는다.
	//public static Context sContext;

	public static boolean isTablet(Context context) {
	    boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
	    boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
	    return (xlarge || large);
	}
	
	public static String getMdn(Context context) {
		TelephonyManager tMgr =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	    return tMgr.getLine1Number();
	}
	
    private static Toast m_toast = null;
	public static void showToast(Context context, String text) {
		if (m_toast == null) {
			m_toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		}
		m_toast.setText(text);
		m_toast.setDuration(Toast.LENGTH_SHORT);

		m_toast.show();
	}
	
	public static Bitmap StringToBitmap(String encodedString) {
		try {
			byte[] decodeByte = Base64.decode(encodedString, Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(decodeByte, 0, decodeByte.length);
			return bitmap;
		} catch (Exception e) {
			e.getMessage();
			return null;
		}
	}
	
    public static String BitmapToString(Bitmap bm){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		
		bm = Bitmap.createScaledBitmap(bm, 200, 200, true);
		bm.compress(Bitmap.CompressFormat.JPEG, 80 , baos);    
		byte[] b = baos.toByteArray(); 
		
		return Base64.encodeToString(b, Base64.DEFAULT);
	}

	/**
	 * 현재시간부터 입력된 시간까지의 경과된 시간을 분단위로 리턴받는다.
	 * @param date yyyy-MM-dd hh:mm:ss.SSS 형태의 스트링
	 * @return long
	 */
	public static long getLastedMinuteToCurrent(String date) {
		long term = 0;
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date parsedDate = dateFormat.parse(date);
			term = System.currentTimeMillis() - parsedDate.getTime();
			term = term/(1000*60);
		}catch(ParseException e){//this generic but you can control another types of exception

		}
		return term;
	}

	public static String getAddress(Context context, double lat, double lng) {
		List<Address> addresses = null;
		try {
			Geocoder geocoder = new Geocoder(context, Locale.getDefault());
			addresses = geocoder.getFromLocation(lat, lng, 1);
		} catch (IOException ioException) {

		} catch (IllegalArgumentException illegalArgumentException) {

		} catch (NullPointerException e) {

		}

		if(addresses != null && addresses.size() > 0) {
			String address = addresses.get(0).getAddressLine(0);
			if(address.startsWith("한국")) {
				address = address.substring(3);
			}

			return address;
		} else {
			return "";
		}
	}
}
