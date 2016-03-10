package com.aura.smartschool.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aura.smartschool.service.MyLocationService;
import com.aura.smartschool.service.SOSIconService;
import com.aura.smartschool.service.StepCounterService;
import com.aura.smartschool.utils.PreferenceUtil;
import com.aura.smartschool.utils.StepSharePrefrenceUtil;
import com.aura.smartschool.utils.Util;

/**
 * Created by Administrator on 2015-07-05.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //부모의 경우는 위치서비스와 활동량 서비스를 하지 않는다.
        if (!PreferenceUtil.getInstance(context).isParent()) {
            //SOS 서비스 시작
            if (PreferenceUtil.getInstance(context).isSOSEnabled()) {
                Intent sosIntent = new Intent(context, SOSIconService.class);
                context.startService(sosIntent);
            }

            //Step Counter 서비스 시작
            if (Util.isKitkatWithStepSensor(context)) {
                StepSharePrefrenceUtil.saveMergeStepCount(context, StepSharePrefrenceUtil.getCurrentStepCount(context));
                StepSharePrefrenceUtil.saveDiffStepCount(context, 0);
                context.startService(new Intent(context, StepCounterService.class));
            }

            //위치 서비스 시작
            context.startService(new Intent(context, MyLocationService.class));
        }
    }
}
