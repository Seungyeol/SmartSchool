package com.aura.smartschool.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;

import com.aura.smartschool.R;
import com.aura.smartschool.utils.PreferenceUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2015-10-07.
 */
public class SOSIconService extends Service {
    private static final int GUARDIAN_NUM = 3;

    private static final int CONTINUOUS_TAP = 0;
    private static final int LONG_PRESS_FOR_SOS = 1;

    private static final int CONTINUOUS_TAP_TIMEOUT = 300;
    private static final int CONTINUOUS_TAP_MIN_TIME = 40;
    private static final int LONG_PRESS_FOR_SOS_TIMEOUT = 4000;

    private boolean mInContinuousTapRegion;

    private int mTouchSlopSquare;
    private int mContinuousTapSlopSquare;

    private int mContinuousTapCount;

    private MotionEvent mCurrentDownEvent;
    private MotionEvent mPreviousUpEvent;

    private WindowManager windowManager;
    private BroadcastReceiver mIconAttachReceiver;
    private ImageView iconView;
    private boolean isAdded;

    private Handler mSOSHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case CONTINUOUS_TAP:
                    if (msg.arg1 % 3 == 0) {
                        sendSOS();
                    }
                    break;
                case LONG_PRESS_FOR_SOS:
                    sendSOS();
                    break;
            }
            return false;
        }

        private void sendSOS() {
            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
            SmsManager smsManager = SmsManager.getDefault();
            String sosMessage = PreferenceUtil.getInstance(getApplicationContext()).getSOSMessage();
            for (int i = 0; i < GUARDIAN_NUM; i++) {
                String phoneNum = PreferenceUtil.getInstance(getApplicationContext()).getGuardianInfo(i)[1];
                if (!StringUtils.isEmpty(phoneNum)) {
                    smsManager.sendTextMessage(phoneNum, null, sosMessage, null, null);
                }
            }
            vibrator.vibrate(100);
        }
    });

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mIconAttachReceiver = new IconAttachReceiver();
        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenStateFilter.addAction(Intent.ACTION_USER_PRESENT);
        screenStateFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(mIconAttachReceiver, screenStateFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void addIconToScreen() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (!keyguardManager.inKeyguardRestrictedInputMode() || isAdded) {
            return;
        }

        isAdded = true;

        if (iconView == null) {
            iconView = new ImageView(this);
        }

        iconView.setImageResource(R.drawable.launcher);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        setWindowParams(params,
                PreferenceUtil.getInstance(SOSIconService.this).getSosCoordX(),
                PreferenceUtil.getInstance(SOSIconService.this).getSosCoordY());
        windowManager.addView(iconView, params);

        ViewConfiguration vc = ViewConfiguration.get(iconView.getContext());
        mTouchSlopSquare = vc.getScaledTouchSlop() * vc.getScaledTouchSlop();
        mContinuousTapSlopSquare = vc.getScaledDoubleTapSlop() * vc.getScaledDoubleTapSlop();

        iconView.setOnTouchListener(new View.OnTouchListener() {
            private WindowManager.LayoutParams paramsF = params;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mCurrentDownEvent != null && mPreviousUpEvent != null && isContinuousTap(mCurrentDownEvent, mPreviousUpEvent, event)) {
                            mContinuousTapCount++;
                            Message msg = mSOSHandler.obtainMessage();
                            msg.what = CONTINUOUS_TAP;
                            msg.arg1 = mContinuousTapCount;
                            mSOSHandler.sendMessage(msg);
                        } else {
                            mContinuousTapCount = 1;
                        }

                        if (mCurrentDownEvent != null) {
                            mCurrentDownEvent.recycle();
                        }
                        mCurrentDownEvent = MotionEvent.obtain(event);

                        mInContinuousTapRegion = true;

                        mSOSHandler.removeMessages(LONG_PRESS_FOR_SOS);
                        mSOSHandler.sendEmptyMessageAtTime(LONG_PRESS_FOR_SOS, mCurrentDownEvent.getDownTime() + LONG_PRESS_FOR_SOS_TIMEOUT);

                        initialX = paramsF.x;
                        initialY = paramsF.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        final int deltaX = (int) (event.getRawX() - initialTouchX);
                        final int deltaY = (int) (event.getRawY() - initialTouchY);
                        int distance = (deltaX * deltaX) + (deltaY * deltaY);
                        if (distance > mTouchSlopSquare) {
                            mInContinuousTapRegion = false;
                            mContinuousTapCount = 0;
                            mSOSHandler.removeMessages(CONTINUOUS_TAP);
                            mSOSHandler.removeMessages(LONG_PRESS_FOR_SOS);
                        }
                        paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                        paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(iconView, paramsF);
                        return false;
                    case MotionEvent.ACTION_UP:
                        PreferenceUtil.getInstance(SOSIconService.this).setSosCoordX(paramsF.x);
                        PreferenceUtil.getInstance(SOSIconService.this).setSosCoordY(paramsF.y);
                        if (mPreviousUpEvent != null) {
                            mPreviousUpEvent.recycle();
                        }
                        mPreviousUpEvent = MotionEvent.obtain(event);
                        mSOSHandler.removeMessages(LONG_PRESS_FOR_SOS);
                    default:
                        break;
                }
                return false;
            }

        });
    }

    private void setWindowParams(WindowManager.LayoutParams params, int x, int y) {
        params.height = dpToPx(50);
        params.width = dpToPx(50);
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = x;
        params.y = y;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void removeIcon() {
        isAdded = false;
        if (iconView != null) {
            try {
                windowManager.removeView(iconView);
            } catch (IllegalArgumentException e) {

            }
        }
    }

    private boolean isContinuousTap(MotionEvent firstDown, MotionEvent firstUp, MotionEvent secondDown) {
        if (!mInContinuousTapRegion) {
            return false;
        }

        final long deltaTime = secondDown.getEventTime() - firstUp.getEventTime();
        if (deltaTime > CONTINUOUS_TAP_TIMEOUT || deltaTime < CONTINUOUS_TAP_MIN_TIME) {
            return false;
        }

        int deltaX = (int) firstDown.getX() - (int) secondDown.getX();
        int deltaY = (int) firstDown.getY() - (int) secondDown.getY();
        return (deltaX * deltaX + deltaY * deltaY < mContinuousTapSlopSquare);
    }

    private class IconAttachReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                addIconToScreen();
            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                removeIcon();
            } else if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                String phoneState = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                if (TelephonyManager.EXTRA_STATE_RINGING.equals(phoneState)) {
                    removeIcon();
                } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(phoneState)) {
                    addIconToScreen();
                }
            }
        }
    }
}
