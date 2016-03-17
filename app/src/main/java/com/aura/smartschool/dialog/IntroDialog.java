package com.aura.smartschool.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aura.smartschool.R;

public class IntroDialog {
    private static Dialog mIntroDialog = null;
    private static final int MSG_DISAPPEAR = 0;
    private static ImageView ivArea;
    private static Context mContext;

    public static void showLoading(Context context){
        mContext = context;

        if(mIntroDialog == null){
            mIntroDialog = new Dialog(context, android.R.style.Theme_Holo_Light_NoActionBar);
            View layout = View.inflate(context, R.layout.dialog_intro, null);
            ivArea = (ImageView) layout.findViewById(R.id.area);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mIntroDialog.addContentView(layout, params);
            mIntroDialog.setCancelable(true);
        }
        if(!mIntroDialog.isShowing()) {
            mIntroDialog.show();
        }
    }

    public static void showArea(int area){
        switch(area) {
            case 0:
                mHandler.sendEmptyMessageDelayed(MSG_DISAPPEAR, 1000);
                break;
            case 1:
                ivArea.setBackgroundResource(R.drawable.area_bucheon);
                mHandler.sendEmptyMessageDelayed(MSG_DISAPPEAR, 2000);
                break;
            case 2:
                ivArea.setBackgroundResource(R.drawable.area_siheung);
                mHandler.sendEmptyMessageDelayed(MSG_DISAPPEAR, 2000);
                break;
        }
        mHandler.sendEmptyMessageDelayed(0, 1000);

    }

    public static void hideLoading(){
        if(mIntroDialog != null){
            mIntroDialog.dismiss();
            mIntroDialog = null;
        }
    }

    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            hideLoading();
        }
    };
}
