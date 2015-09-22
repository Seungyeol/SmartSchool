package com.aura.smartschool.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.widget.Toast;

import com.aura.smartschool.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class Util {

	private static float WALKING_COEFFICIENT = 0.9F;

	//MainApplication에서 주입받는다.
	//public static Context sContext;

	public static boolean isTablet(Context context) {
	    boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
	    boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
	    return (xlarge || large);
	}
	
	public static String getMdn(Context context) {
		TelephonyManager tMgr =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		//2015-06-29 전화번호가 +82로 시작하는 경우 보정
		String number = tMgr.getLine1Number();
		if(number.startsWith("+82")) {
			number = number.replace("+82", "0");
		}
	    return number;
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

	public static void showConnectAuraDialog(final Context context, String strErrorMsg){
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "15441284"));
				context.startActivity(intent);
			}
		});
		alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.setMessage(strErrorMsg + "\n고객센터에 연결하시겠습니까?");
		alert.show();
	}

	public static void showAlertDialog(Context context, String strErrorMsg){
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.setMessage(strErrorMsg);
		alert.show();
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

	public static String convertLongToDate(long term) {
		int hour = (int)term/60;
		int minute = (int)term%60;
		if(hour > 0) {
			return String.format("%d시간 %d분", hour, minute);
		} else {
			return String.format("%d분", minute);
		}
	}

	public static Date getDateTimeFromString(String date) {
		Date parsedDate = null;
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			parsedDate = dateFormat.parse(date);
		}catch(ParseException e){//this generic but you can control another types of exception

		}
		return parsedDate;
	}

	public static Date getDateFromString(String date) {
		Date parsedDate = null;
		try{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			parsedDate = dateFormat.parse(date);
		}catch(ParseException e){//this generic but you can control another types of exception

		}
		return parsedDate;
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
			if(address.startsWith("한국") || address.startsWith("남한")) {
				address = address.substring(3);
			}

			return address;
		} else {
			return "";
		}
	}

	public static boolean isKitkatWithStepSensor(Context context) {
		int currentApiVersion = android.os.Build.VERSION.SDK_INT;
		PackageManager packageManager = context.getPackageManager();
		return currentApiVersion >= android.os.Build.VERSION_CODES.KITKAT
				&& packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
	}

	public static long getTodayTimeInMillis() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}

	public static long getFirstDayTimeOfMonthInMillis(int year, int month) {
		Calendar c = Calendar.getInstance();
		c.set(year, month, 1);
		return c.getTimeInMillis();
	}

	public static long getYesterdayTimeInMillis() {
		long dayTimeInMillis = 24 * 60 * 60 * 1000;
		return getTodayTimeInMillis() - dayTimeInMillis;
	}

	public static boolean isDifferentDay(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);

		if(c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR) != 0) {
			return true;
		}
		if(c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH) != 0) {
			return true;
		}
		if(c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH) != 0) {
			return true;
		}
		return false;
	}

	public static String convertSecondToMinute(String strSecond) {
		int totalSecond = Integer.parseInt(strSecond);
		int minute = totalSecond/60;
		int second = totalSecond%60;

		return String.format("%d분 %d초", minute, second);
	}

	public static File getTempFile() {
		boolean isSdCardMounted = false;
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			isSdCardMounted = true;
		}

		if (isSdCardMounted) {
			File f = new File(Environment.getExternalStorageDirectory(), // 외장메모리 경로
					MainActivity.TEMP_PHOTO_FILE);
			try {
				f.createNewFile();      // 외장메모리에 temp.jpg 파일 생성
			} catch (IOException e) {
			}

			return f;
		} else
			return null;
	}

//
//	public static int getCalories(Context context, int second) {
//		double weight = PreferenceUtil.getInstance(context).getWeight();
//		if (weight == 0f) {
//			weight = 50f;
//		}
//		return (int)(WALKING_COEFFICIENT*weight/15/60*second);
//	}

	public static int getCalories(Context context, int distance) {
		double weight = PreferenceUtil.getInstance(context).getWeight();
		if (weight == 0f) {
			weight = 70f;
		}

		double calPerMile = 0.57 * weight / 0.453592;
		return (int) (distance * calPerMile * 0.0006213);
	}

	/*
    return meter
     */
	public static int getDistance(Context context, int steps) {
        /*
        Men - you can multiply your height in cm by 0.415
        Ladies - multiply your height in cm by 0.413
         */
		double height = PreferenceUtil.getInstance(context).getHeight();
		if (height == 0f) {
			height = 170f;
		}
		return (int)(height*0.415)*steps/100;
	}
}
