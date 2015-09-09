package com.aura.smartschool.utils;

import android.util.Log;

/**
 * Created by Administrator on 2015-09-10.
 */
public class SchoolLog {

    private static boolean forceLog = false;

    public static void d(String tag, String message) {
        if (forceLog) {
            Log.d(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (forceLog) {
            Log.i(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (forceLog) {
            Log.v(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (forceLog) {
            Log.e(tag, message);
        }
    }
}
