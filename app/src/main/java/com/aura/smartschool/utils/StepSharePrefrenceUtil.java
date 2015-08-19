package com.aura.smartschool.utils;

import android.content.Context;

/**
 * Created by Administrator on 2015-07-05.
 */
public class StepSharePrefrenceUtil {
    private static final String STEP_COUNTER_PREFERENCE_NAME = "step_counter";

    //View
    private static final String KEY_CURRENT_STEPS = "current_steps";
    private static final String KEY_MERGE_STEPS = "merge_steps";
    private static final String KEY_DIFF_STEPS = "diff_steps";

    //Settings
    private static final String KEY_TARGET_NOTICE_ON_OFF = "notice_on_off";
    private static final String KEY_STEP_COUNT_SERVICE_ON_OFF = "steps_on_off";

    private static final String KEY_TARGET_CODE = "target_code";
    private static final String KEY_TARGET_STEPS = "target_steps";
    private static final String KEY_TARGET_CALORIES = "target_calories";
    private static final String KEY_TARGET_DISTANCE = "target_distance";
    private static final String KEY_TARGET_ON_OFF = "target_on_off";
    private static final String KEY_TODAY_ACHIEVED = "today_achieved";


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

    public static void saveNoticeOnOff(Context context, boolean isOn) {
        context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).edit().putBoolean(KEY_TARGET_NOTICE_ON_OFF, isOn).commit();
    }

    public static boolean getNoticeOnOff(Context context) {
        return context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).getBoolean(KEY_TARGET_NOTICE_ON_OFF, false);
    }

    public static void saveCountServiceOnOff(Context context, boolean isOn) {
        context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).edit().putBoolean(KEY_STEP_COUNT_SERVICE_ON_OFF, isOn).commit();
    }

    public static boolean getCountServiceOnOff(Context context) {
        return context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).getBoolean(KEY_STEP_COUNT_SERVICE_ON_OFF, false);
    }

    public static void saveTargetCode(Context context, int targetCode) {
        context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).edit().putInt(KEY_TARGET_CODE, targetCode).commit();
    }

    public static int getTargetCode(Context context) {
        return context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).getInt(KEY_TARGET_CODE, 0);
    }

    public static void saveTargetSteps(Context context, int targetSteps) {
        context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).edit().putInt(KEY_TARGET_STEPS, targetSteps).commit();
    }

    public static int getTargetSteps(Context context) {
        return context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).getInt(KEY_TARGET_STEPS, 0);
    }

    public static void saveTargetCalories(Context context, int targetCalories) {
        context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).edit().putInt(KEY_TARGET_CALORIES, targetCalories).commit();
    }

    public static int getTargetCalories(Context context) {
        return context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).getInt(KEY_TARGET_CALORIES, 0);
    }

    public static void saveTargetDistance(Context context, int targetDistance) {
        context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).edit().putInt(KEY_TARGET_DISTANCE, targetDistance).commit();
    }

    public static int getTargetDistance(Context context) {
        return context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).getInt(KEY_TARGET_DISTANCE, 0);
    }

    public static void saveTargetOnOff(Context context, boolean isOn) {
        context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).edit().putBoolean(KEY_TARGET_ON_OFF, isOn).commit();
    }

    public static boolean getTargetOnOff(Context context) {
        return context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).getBoolean(KEY_TARGET_ON_OFF, false);
    }

    public static void saveTodayAchieved(Context context, boolean isAchieved) {
        context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).edit().putBoolean(KEY_TODAY_ACHIEVED, isAchieved).commit();
    }

    public static boolean getTodayAchieved(Context context) {
        return context.getSharedPreferences(STEP_COUNTER_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS).getBoolean(KEY_TODAY_ACHIEVED, false);
    }
}