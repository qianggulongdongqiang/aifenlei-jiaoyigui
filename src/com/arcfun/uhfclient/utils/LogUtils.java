package com.arcfun.uhfclient.utils;

import android.util.Log;

public class LogUtils {
    private static final String TAG = "zxzuhf|";
    private static final boolean ENABLE = Log.isLoggable("aifenx",
            Log.DEBUG);
    public static void d(String name, String msg) {
        if (ENABLE) {
            Log.d(TAG + name, msg);
        }
    }
    public static void i(String name, String msg) {
        if (ENABLE) {
            Log.i(TAG + name, msg);
        }
    }
    public static void w(String name, String msg) {
        if (ENABLE) {
            Log.w(TAG + name, msg);
        }
    }
    public static void v(String name, String msg) {
        if (ENABLE) {
            Log.v(TAG + name, msg);
        }
    }
    public static void e(String name, String msg) {
        if (ENABLE) {
            Log.e(TAG + name, msg);
        }
    }
    public static void e(String name, String msg, Throwable e) {
        if (ENABLE) {
            Log.e(TAG + name, msg, e);
        }
    }
}
