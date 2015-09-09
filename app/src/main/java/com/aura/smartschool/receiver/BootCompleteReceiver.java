package com.aura.smartschool.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aura.smartschool.service.StepCounterService;
import com.aura.smartschool.utils.StepSharePrefrenceUtil;
import com.aura.smartschool.utils.Util;

/**
 * Created by Administrator on 2015-07-05.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO Login Check
        if (Util.isKitkatWithStepSensor(context)) {
            StepSharePrefrenceUtil.saveMergeStepCount(context, StepSharePrefrenceUtil.getCurrentStepCount(context));
            StepSharePrefrenceUtil.saveDiffStepCount(context, 0);
            context.startService(new Intent(context, StepCounterService.class));
        }
    }
}
