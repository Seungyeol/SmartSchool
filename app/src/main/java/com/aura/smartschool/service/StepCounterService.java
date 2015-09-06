package com.aura.smartschool.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.aura.smartschool.Constant;
import com.aura.smartschool.LoginManager;
import com.aura.smartschool.MainActivity;
import com.aura.smartschool.R;
import com.aura.smartschool.database.DBStepCounter;
import com.aura.smartschool.fragment.walkingfragments.WalkingCountFragment;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.utils.StepSharePrefrenceUtil;
import com.aura.smartschool.utils.Util;

import org.jsoup.helper.StringUtil;

/**
 * Created by Administrator on 2015-06-28.
 */
public class StepCounterService extends Service implements SensorEventListener {

    private static final int RESTART_DELAY = 1000;
    private static final int SCREEN_OFF_RECEIVER_DELAY = 5000;

    private static final int STEPCOUNTER_DELAY_SEC = 1000000;  //1초
    private static final int STEPCOUNTER_DELAY_2S = 2 * 1000000;  //2초

    private volatile int currentTotalSteps;
    private volatile int lastSteps;
    private volatile boolean isWalking;
    private volatile long startWalkingTime;

    public enum SENSOR_DELAY_TYPE {
        WALKING_FRAGMENT_SHOW,
        WALKING_FRAGMENT_DISAPPEAR
    }

    private Handler mStepCounterHandler;
    private PowerManager.WakeLock mWakeLock;

    private IBinder mBinder = new StepCounterBinder();

    public class StepCounterBinder extends Binder {
        public void setSensorDelay(SENSOR_DELAY_TYPE type, Handler stepCounterHandler) {
            mStepCounterHandler = stepCounterHandler;
            switch (type) {
                case WALKING_FRAGMENT_SHOW:
                    registerEventListener(STEPCOUNTER_DELAY_SEC);
                    break;
                case WALKING_FRAGMENT_DISAPPEAR:
                    registerEventListener(STEPCOUNTER_DELAY_2S);
                    break;
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int mergeSteps = StepSharePrefrenceUtil.getMergeStepCount(this);
        int diffSteps = StepSharePrefrenceUtil.getDiffStepCount(this);
        int steps = (int) event.values[0];

        int totalSteps = steps + mergeSteps - diffSteps;

        DBStepCounter db = DBStepCounter.getInstance(this);
        int dbSteps = db.getSteps(Util.getTodayTimeInMillis());
        if(dbSteps == -1) {
            db.insertNewDate(Util.getTodayTimeInMillis());
            if (db.getSteps(Util.getYesterdayTimeInMillis()) > 0) {   // 최초실행일땐 전날 steps 값 존재하지 않음
                StepSharePrefrenceUtil.saveMergeStepCount(this, 0);
            }
            lastSteps = 0;
            totalSteps = 0;
            StepSharePrefrenceUtil.saveDiffStepCount(this, steps);
            StepSharePrefrenceUtil.saveTodayAchieved(this, false);
        }

        checkWalkingTime(totalSteps);

        StepSharePrefrenceUtil.saveCurrentStepCount(this, totalSteps);
        int totalActiveTime = db.getWalkingTime(Util.getTodayTimeInMillis());
        if (startWalkingTime > 0) {
            totalActiveTime += (int) ((System.currentTimeMillis() - startWalkingTime) / 1000);
        }
        int calories = Util.getCalories(getApplicationContext(), totalActiveTime);
        int distance = Util.getDistance(getApplicationContext(), totalSteps);

        doCheckAchievedTarget(totalSteps, calories, distance);

        Message message = Message.obtain();
        Bundle data = new Bundle();
        data.putInt("steps", totalSteps);
        data.putInt("calories", calories);
        data.putInt("distance", distance);
        data.putInt("activeTime", totalActiveTime);
        message.setData(data);
        if(mStepCounterHandler != null) {
            mStepCounterHandler.sendMessage(message);
        }
        db.close();
        makeForgroundService();
    }


    private void checkWalkingTime(int steps) {
        currentTotalSteps = steps;
        if(steps != 0 && lastSteps < steps && !isWalking) {
            isWalking = true;
            startWalkingTime = System.currentTimeMillis();
            WalkingTimeCheckHandler.post(WalkingTimeCheckTask);
        }
    }

    private Handler WalkingTimeCheckHandler  = new Handler();

    private Runnable WalkingTimeCheckTask = new Runnable() {
        @Override
        public void run() {
            if (lastSteps < currentTotalSteps && isWalking) {
                lastSteps = currentTotalSteps;
                WalkingTimeCheckHandler.postDelayed(this, 3000);
            } else {
                isWalking = false;
                if (System.currentTimeMillis() - startWalkingTime > 5000) {
                    int activeTime = (int) ((System.currentTimeMillis() - startWalkingTime) / 1000);
                    DBStepCounter.getInstance(StepCounterService.this).updateSteps(Util.getTodayTimeInMillis(),
                                                                                    currentTotalSteps,
                                                                                    Util.getCalories(getApplicationContext(), activeTime),
                                                                                    Util.getDistance(getApplicationContext(), currentTotalSteps),
                                                                                    activeTime);
                }
                startWalkingTime = 0;
            }
        }
    } ;

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
        unregisterReceiver(mScreenOffBroadcastReceiver);
        if (mWakeLock != null) {
            mWakeLock.release();
        }
        if (Util.isKitkatWithStepSensor(this)) {
            unregisterSensorStep();
        }
    }

    @Override
    public void onCreate() {
        PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "StepCounterSevice");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (Util.isKitkatWithStepSensor(this)) {
            useWakeLock();
            registerEventListener(STEPCOUNTER_DELAY_2S);
        }
        return START_STICKY;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void unregisterSensorStep() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
        stopForeground(true);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void registerEventListener(int maxdelay) {
        makeForgroundService();
        SensorManager sensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
        try {
            sensorManager.unregisterListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST, maxdelay);
    }

    private void useWakeLock() {
        mWakeLock.acquire();

        registerReceiver(mScreenOffBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(mScreenOffBroadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
    }

    private void makeForgroundService() {
        Intent foregroundIntent = new Intent(this, MainActivity.class);
        foregroundIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        foregroundIntent.putExtra(Constant.NOTIFCATION_DESTINATION_FRAGMENT, Constant.NOTIFICATION_STEP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, foregroundIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification=new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.home)
                .setContentTitle("스마트안전건강지킴이")
                .setContentText(String.format("오늘 누적 활동량은 %d 걸음 입니다.", currentTotalSteps))
                .setContentIntent(pendingIntent).build();

        startForeground(Constant.NOTIFICATION_STEP_FOREGROUND, notification);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC, System.currentTimeMillis() + RESTART_DELAY,
                        PendingIntent.getService(this, 3, new Intent(this, StepCounterService.class), 0));
    }

    private void doCheckAchievedTarget(int totalSteps, int calories, int distance) {
        boolean isNotiOn = StepSharePrefrenceUtil.getNoticeOnOff(this);
        boolean isTagetAchieved = StepSharePrefrenceUtil.getTodayAchieved(this);

        if (isNotiOn && !isTagetAchieved) {
            int targetCode = StepSharePrefrenceUtil.getTargetCode(this);
            if (targetCode == WalkingCountFragment.TARGET.WALKING.code) {
                int target = StepSharePrefrenceUtil.getTargetSteps(this);
                if (target <= totalSteps) {
                    StepSharePrefrenceUtil.saveTodayAchieved(this, true);
                    showAchieveNotification();
                }
            } else if (targetCode == WalkingCountFragment.TARGET.CALORIES.code) {
                int target = StepSharePrefrenceUtil.getTargetCalories(this);
                if (target <= calories) {
                    StepSharePrefrenceUtil.saveTodayAchieved(this, true);
                    showAchieveNotification();
                }
            } else {
                float target = StepSharePrefrenceUtil.getTargetDistance(this);
                if (target <= distance) {
                    StepSharePrefrenceUtil.saveTodayAchieved(this, true);
                    showAchieveNotification();
                }
            }
        }
    }

    private void showAchieveNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Constant.NOTIFCATION_DESTINATION_FRAGMENT, Constant.NOTIFICATION_STEP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager mNotiManger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this);
        notiBuilder.setContentTitle("목표량 달성")
                .setContentText("목표량을 달성하였습니다.")
                .setSmallIcon(R.drawable.home)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        mNotiManger.notify(Constant.NOTIFICATION_STEP, notiBuilder.build());
    }

    BroadcastReceiver mScreenOffBroadcastReceiver = new BroadcastReceiver() {

        //When Event is published, onReceive method is called
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) || intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                registerEventListener(STEPCOUNTER_DELAY_2S);

                Runnable runnable = new Runnable() {
                    public void run() {
                        registerEventListener(STEPCOUNTER_DELAY_2S);
                    }
                };

                new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
            }
        }
    };
}