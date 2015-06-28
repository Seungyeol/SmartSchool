package com.aura.smartschool.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
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
public class StepCounterService extends Service {

    private static final String TAG = StepCounterService.class.getSimpleName();

    private final int RUNNING_NOTI_ID = 1001;
    private final int RUNNING_NOTI_REQUEST_CODE = 5001;

    private Handler mStepCounterHandler;
    private StepListener mStepListener;

    private IBinder mBinder = new StepCounterBinder();
    private Handler mStepUpdateHandler  = new Handler();
    private Runnable StepUpdateTask = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "StepUpdateTask >> step count  = " + mStepListener.getSteps());
            if (mStepCounterHandler != null) {
                Message message = Message.obtain();
                message.arg1 = mStepListener.getSteps();
                message.arg2 = mStepListener.getBigsteps();
                mStepCounterHandler.sendMessage(message);
            }
            mStepUpdateHandler.postDelayed(this, 1000);
        }
    } ;

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
        mStepUpdateHandler.removeCallbacks(StepUpdateTask);
        mStepListener.stop();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mStepListener = new StepListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (isLogin()) {
            mStepListener.start();
            showRunningNotification();
            mStepUpdateHandler.post(StepUpdateTask);
        }
        return START_STICKY;
    }

    private void showRunningNotification() {
        Notification notification = new Notification(R.drawable.home, null, System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, RUNNING_NOTI_REQUEST_CODE, new Intent(this, MainActivity.class), 0);
        notification.setLatestEventInfo(this, getText(R.string.app_name), "",contentIntent);
        startForeground(RUNNING_NOTI_ID, notification);
    }

    private boolean isLogin() {
        String id = PreferenceUtil.getInstance(this).getHomeId();
        String mdn = Util.getMdn(this);

        if(TextUtils.isEmpty(id) || TextUtils.isEmpty(mdn)) {
            return false;
        }
        return true;
    }
}
