package com.aura.smartschool.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.aura.smartschool.MainActivity;
import com.aura.smartschool.R;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.utils.Util;

/**
 * Created by Administrator on 2015-06-28.
 */
public class StepCounterService extends Service implements SensorEventListener {

    private static final String TAG = StepCounterService.class.getSimpleName();

    private static final int RESTART_DELAY = 1000;
//    private static final int STEPCOUNTER_DELAYY = 5 * 60 * 1000000;  //5분
    private static final int STEPCOUNTER_DELAY = 1000000;  //1초

    private Handler mStepCounterHandler;

    private IBinder mBinder = new StepCounterBinder();

    @Override
    public void onSensorChanged(SensorEvent event) {

        Message message = Message.obtain();
        Bundle data = new Bundle();
        data.putInt("steps", (int) event.values[0]);
        Log.d("TEST", "StepCounter >> counter = " + (int) event.values[0]);
        message.setData(data);
        if(mStepCounterHandler != null) {
            mStepCounterHandler.sendMessage(message);
        }
    }

    /*
            if (event.values[0] > Integer.MAX_VALUE) {
            if (BuildConfig.DEBUG) Logger.log("probably not a real value: " + event.values[0]);
            return;
        } else {
            steps = (int) event.values[0];
            if (WAIT_FOR_VALID_STEPS && steps > 0) {
                WAIT_FOR_VALID_STEPS = false;
                Database db = Database.getInstance(this);
                if (db.getSteps(Util.getToday()) == Integer.MIN_VALUE) {
                    int pauseDifference = steps -
                            getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS)
                                    .getInt("pauseCount", steps);
                    db.insertNewDay(Util.getToday(), steps - pauseDifference);
                    if (pauseDifference > 0) {
                        // update pauseCount for the new day
                        getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS).edit()
                                .putInt("pauseCount", steps).commit();
                    }
                    reRegisterSensor();
                }
                db.saveCurrentSteps(steps);
                db.close();
                updateNotificationState();
                startService(new Intent(this, WidgetUpdateService.class));
            }
        }
     */

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public class StepCounterBinder extends Binder {
        public void setStepCounterHandler(Handler handler) {
            mStepCounterHandler = handler;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mStepCounterHandler = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (Util.isKitkatWithStepSensor(this)) {
            registerEventListener(STEPCOUNTER_DELAY, null);
        }
        return START_STICKY;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void registerEventListener(int maxdelay, Handler stepCounterHandler) {
        SensorManager sensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
        try {
            sensorManager.unregisterListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL, maxdelay);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC, System.currentTimeMillis() + RESTART_DELAY,
                        PendingIntent.getService(this, 3, new Intent(this, StepCounterService.class), 0));
    }
}
