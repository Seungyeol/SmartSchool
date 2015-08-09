package com.aura.smartschool.BR;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;
import android.util.Log;

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

            }

            //학교 상담
            if("consult".equals(command)) {

            }

        } catch (JSONException e) {

        }
    }
}
