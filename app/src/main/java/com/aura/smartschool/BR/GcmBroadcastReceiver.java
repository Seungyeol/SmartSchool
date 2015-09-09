package com.aura.smartschool.BR;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;

import com.aura.smartschool.Constant;
import com.aura.smartschool.ConsultFragmentVisibleManager;
import com.aura.smartschool.MainActivity;
import com.aura.smartschool.R;
import com.aura.smartschool.database.ConsultType;
import com.aura.smartschool.utils.SchoolLog;

import org.json.JSONException;
import org.json.JSONObject;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    private Context mContext;

    public GcmBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SchoolLog.d("LDK", "GcmBroadcastReceiver onReceive");
        mContext = context;

        Bundle bundle = intent.getExtras();
        String command = "";
        String value = "";

        for (String key : bundle.keySet()) {
            Object objValue = bundle.get(key);
            if(key.equals("command")) {
                command = String.valueOf(objValue);
            }
            if(key.equals("value")) {
                value = String.valueOf(objValue);
            }
        }

        if(TextUtils.isEmpty(command) || TextUtils.isEmpty(value)) {
            return;
        }

        try {
            JSONObject json = new JSONObject(value);

            Intent activitIntent = new Intent(context, MainActivity.class);
            activitIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            //학교 알리미
            if("school".equals(command)) {
                int category = json.getInt("category");
                String categoryString = (category == Constant.CATEGORY_LETTER ? "가정통신문" : "공지사항");
                String title = json.getString("title");
                String content = json.getString("content");
                String noti_date = json.getString("noti_date");
                int school_id = json.getInt("school_id");
                String school_name = json.getString("school_name");

                int requestID = (int) System.currentTimeMillis();

                activitIntent.putExtra(Constant.NOTIFCATION_DESTINATION_FRAGMENT, Constant.NOTIFICATION_SCHOOL_ALIMI);
                activitIntent.putExtra(Constant.CATEGORY, category);
                activitIntent.putExtra(Constant.SCHOOL_ID, school_id);

                PendingIntent contentIntent = PendingIntent.getActivity(context, requestID, activitIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification noti = new NotificationCompat.Builder(context)
                        .setContentTitle(context.getResources().getString(R.string.app_name_korean))
                        .setContentText(String.format("(%s)%s - 학교정보가 업데이트 되었습니다.", categoryString, school_name))
                        .setTicker(String.format("(%s)%s - 학교정보가 업데이트 되었습니다.", categoryString, school_name))
                        .setSmallIcon(R.drawable.school)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .build();
                nm.notify(Constant.NOTIFICATION_SCHOOL_ALIMI, noti);

                return;
            }

            //학교 상담
            else if("consult".equals(command)) {
                int category = json.getInt("category");
                String content = json.getString("content");

                ConsultType type = ConsultType.findConsultTypeByConsultCode(category);

                if (!ConsultFragmentVisibleManager.getInstance().isVisible(category)) {
                    int requestID = (int) System.currentTimeMillis();

                    activitIntent.putExtra(Constant.NOTIFCATION_DESTINATION_FRAGMENT, Constant.NOTIFICATION_CONSULT);
                    activitIntent.putExtra(Constant.CATEGORY, category);

                    PendingIntent contentIntent = PendingIntent.getActivity(context, requestID, activitIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification noti = new NotificationCompat.Builder(context)
                            .setContentTitle(context.getResources().getString(R.string.app_name_korean))
                            .setContentText("(" + type.consultName + ") 상담 메시지가 도착하였습니다.")
                            .setTicker("(" + type.consultName + ") 상담 메시지가 도착하였습니다.")
                            .setSmallIcon(R.drawable.school)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true)
                            .setContentIntent(contentIntent)
                            .build();
                    nm.notify(Constant.NOTIFICATION_CONSULT, noti);
                } else {
                    ConsultFragmentVisibleManager.getInstance().notifyMessageReceived(content);
                }
            }

            //qna 답변시
            else if("qna".equals(command)) {
                String title = json.getString("title");

                int requestID = (int) System.currentTimeMillis();

                activitIntent.putExtra(Constant.NOTIFCATION_DESTINATION_FRAGMENT, Constant.NOTIFICATION_QNA);

                PendingIntent contentIntent = PendingIntent.getActivity(context, requestID, activitIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification noti = new NotificationCompat.Builder(context)
                        .setContentTitle(context.getResources().getString(R.string.app_name_korean))
                        .setContentText("Q&A 답변이 등록되었습니다.")
                        .setTicker("Q&A 답변이 등록되었습니다")
                        .setSmallIcon(R.drawable.school)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .build();
                nm.notify(Constant.NOTIFICATION_QNA, noti);

                return;
            }

            //공지사항
            else if("appNoti".equals(command)) {
                int requestID = (int) System.currentTimeMillis();

                activitIntent.putExtra(Constant.NOTIFCATION_DESTINATION_FRAGMENT, Constant.NOTIFICATION_APP_NOTICE);

                PendingIntent contentIntent = PendingIntent.getActivity(context, requestID, activitIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification noti = new NotificationCompat.Builder(context)
                        .setContentTitle(context.getResources().getString(R.string.app_name_korean))
                        .setContentText("새로운 공지사항이 등록되었습니다.")
                        .setTicker("새로운 공지사항이 등록되었습니다.")
                        .setSmallIcon(R.drawable.school)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .build();
                nm.notify(Constant.NOTIFICATION_APP_NOTICE, noti);

                return;
            }

        } catch (JSONException e) {

        }
    }
}
