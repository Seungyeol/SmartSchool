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
import android.util.Log;

import com.aura.smartschool.Constant;
import com.aura.smartschool.ConsultFragmentVisibleManager;
import com.aura.smartschool.MainActivity;
import com.aura.smartschool.R;
import com.aura.smartschool.database.ConsultType;

import org.json.JSONException;
import org.json.JSONObject;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    private Context mContext;

    public GcmBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("LDK", "GcmBroadcastReceiver onReceive");
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
                String title = json.getString("title");
                String content = json.getString("content");
                String noti_date = json.getString("noti_date");

                int requestID = (int) System.currentTimeMillis();

                activitIntent.putExtra(Constant.NOTIFCATION_DESTINATION_FRAGMENT, Constant.NOTIFICATION_SCHOOL_ALIMI);

                PendingIntent contentIntent = PendingIntent.getActivity(context, requestID, new Intent(),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification noti = new NotificationCompat.Builder(context)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setTicker(title)
                        .setSmallIcon(R.drawable.school)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .build();
                nm.notify(Constant.NOTIFICATION_SCHOOL_ALIMI, noti);

                return;
            }

            //학교 상담
            if("consult".equals(command)) {
                int category = json.getInt("category");
                String content = json.getString("content");

                ConsultType type = ConsultType.findConsultTypeByConsultCode(category);

                if (!ConsultFragmentVisibleManager.getInstance().isVisible(category)) {
                    int requestID = (int) System.currentTimeMillis();

                    activitIntent.putExtra(Constant.NOTIFCATION_DESTINATION_FRAGMENT, Constant.NOTIFICATION_CONSULT);
                    activitIntent.putExtra(Constant.CONSULT_CATEGORY, category);

                    PendingIntent contentIntent = PendingIntent.getActivity(context, requestID, activitIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification noti = new NotificationCompat.Builder(context)
                            .setContentTitle(type.consultName)
                            .setContentText("선생님 : " + content)
                            .setTicker(type.consultName)
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
            if("qna".equals(command)) {
                String title = json.getString("title");

                int requestID = (int) System.currentTimeMillis();

                activitIntent.putExtra(Constant.NOTIFCATION_DESTINATION_FRAGMENT, Constant.NOTIFICATION_QNA);

                PendingIntent contentIntent = PendingIntent.getActivity(context, requestID, activitIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification noti = new NotificationCompat.Builder(context)
                        .setContentTitle("QnQ 게시판 답변 도착")
                        .setContentText(title + "에 대한 답변이 도착하였습니다.")
                        .setTicker("QnQ 게시판 답변 도착")
                        .setSmallIcon(R.drawable.school)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .build();
                nm.notify(Constant.NOTIFICATION_SCHOOL_ALIMI, noti);

                return;
            }

        } catch (JSONException e) {

        }
    }
}
