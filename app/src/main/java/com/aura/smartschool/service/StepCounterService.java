package com.aura.smartschool.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
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
public class StepCounterService extends Service {

    private static final String TAG = StepCounterService.class.getSimpleName();

    private final int RUNNING_NOTI_ID = 1001;
    private final int RUNNING_NOTI_REQUEST_CODE = 5001;

    private Handler mStepCounterHandler;
    private StepListener mStepListener;

    private volatile long mStartWalkingTime;
    private volatile int mCurrentWalkingTime;
    private volatile int mMaxWalkingTime;
    private volatile int mTotalWalkingTime;

    private volatile int mCurrentWalkingCount;
    private volatile int mTotalWalkingCount;

    private volatile int mLastWalkingCount;

    private volatile int count;
    private volatile boolean isWalking;

    private static final int WALKING_THRESHOLD = 5;

    private IBinder mBinder = new StepCounterBinder();
    private Handler mStepUpdateHandler  = new Handler();
    private Runnable StepUpdateTask = new Runnable() {
        @Override
        public void run() {
            //5초 이내 움직임은 무시, 5초이상 변화없음 운동 초기화
            if (mStepCounterHandler != null) {
                mCurrentWalkingCount = mStepListener.getSteps();
                if(mLastWalkingCount < mCurrentWalkingCount && count < WALKING_THRESHOLD) {
                    count++;
                    if(count == WALKING_THRESHOLD && !isWalking) {
                        mStartWalkingTime = System.currentTimeMillis();
                        isWalking = true;
                    }
                } else if (mLastWalkingCount == mCurrentWalkingCount && count > 0){
                    count--;
                    if(count == 0 && isWalking) {
                        isWalking = false;
                        mTotalWalkingTime += mCurrentWalkingTime;
                        mTotalWalkingCount += mCurrentWalkingCount;
                        mCurrentWalkingTime = 0;
                        mCurrentWalkingCount = 0;
                        mStepListener.initSteps();
                    }
                }
                if(isWalking) {
                    Message message = Message.obtain();
                    Bundle data = new Bundle();
                    data.putInt("steps", mCurrentWalkingCount);
                    data.putInt("totalSteps", mTotalWalkingCount + mCurrentWalkingCount);
                    mCurrentWalkingTime = 5 + (int)((System.currentTimeMillis() - mStartWalkingTime)/1000);
                    data.putInt("walkingTime", mCurrentWalkingTime);
                    data.putInt("totalWalkingTime", mTotalWalkingTime + mCurrentWalkingTime);

                    message.setData(data);
                    mStepCounterHandler.sendMessage(message);
                }
                mLastWalkingCount = mStepListener.getSteps();
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
