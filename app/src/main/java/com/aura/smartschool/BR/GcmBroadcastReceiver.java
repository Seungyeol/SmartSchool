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

import com.aura.smartschool.R;

import org.json.JSONException;
import org.json.JSONObject;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    private Context mContext;

    public GcmBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
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

            //학교 알리미
            if("school".equals(command)) {
                int category = json.getInt("category");
                String title = json.getString("title");
                String content = json.getString("content");
                String noti_date = json.getString("noti_date");

                int requestID = (int) System.currentTimeMillis();
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
                nm.notify(8888, noti);

                return;
            }

            //학교 상담
            if("consult".equals(command)) {

            }

        } catch (JSONException e) {

        }
    }
}
