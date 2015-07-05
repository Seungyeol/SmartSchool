package com.aura.smartschool.utils;

import android.content.Context;

/**
 * Created by Administrator on 2015-07-05.
 */
public class StepSharePrefrenceUtil {
    private static final String STEP_COUNTER_PREFERENCE_NAME = "step_counter";
    private static final String KEY_CURRENT_STEPS = "current_steps";
    private static final String KEY_MERGE_STEPS = "merge_steps";
    private static final String KEY_DIFF_STEPS = "diff_steps";


    public static void saveCurrentStepCount(Context context, int steps) {
        context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).edit().putInt(KEY_CURRENT_STEPS, steps).commit();
    }

    public static int getCurrentStepCount(Context context) {
        return context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).getInt(KEY_CURRENT_STEPS, 0);
    }

    public static void saveMergeStepCount(Context context, int steps) {
        context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).edit().putInt(KEY_MERGE_STEPS, steps).commit();
    }

    public static int getMergeStepCount(Context context) {
        return context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).getInt(KEY_MERGE_STEPS, 0);
    }

    public static void saveDiffStepCount(Context context, int steps) {
        context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).edit().putInt(KEY_DIFF_STEPS, steps).commit();
    }

    public static int getDiffStepCount(Context context) {
        return context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).getInt(KEY_DIFF_STEPS, 0);
    }
}
