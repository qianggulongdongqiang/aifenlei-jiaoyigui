package com.arcfun.uhfclient.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.arcfun.uhfclient.data.DeviceInfo;

public class SharedPreferencesUtils {
    private static final String TAG = "SharedPreferences";
    private static final String FILE_NAME = "user";
    private static final String KEY_DEVICEID = "deviceId";
    private static final String KEY_SIGNATURE = "signature";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_QRCODE = "qrcode";
    private static final String KEY_REGISTER = "isRegister";
    private static final String KEY_STATE = "state";
    private static final String KEY_PROT1 = "port1";
    private static final String KEY_PROT2 = "port2";
    private static final String KEY_DEBUG = "isDebug";
    private static SharedPreferences sp = null;

    public static void setDeviceInfo(Context c, DeviceInfo info) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sp.edit();
        if (info != null) {
            editor.putString(KEY_TOKEN, info.getToken());
            editor.putInt(KEY_DEVICEID, info.getId());
            editor.putString(KEY_SIGNATURE, info.getSignature());
            // machine
            editor.commit();
        }
    }

    public static String getToken(Context c) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getString(KEY_TOKEN, "");
    }

    public static void setState(Context c, int state) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_STATE, state);
        editor.commit();
    }

    public static int getState(Context c) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getInt(KEY_STATE, 10);
    }

    public static int getDeviceId(Context c) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getInt(KEY_DEVICEID, 0);
    }

    public static String getSignature(Context c) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getString(KEY_SIGNATURE, "00001");
    }

    public static void setPort(Context c, int port, String path) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sp.edit();
        if (port == 1) {
            editor.putString(KEY_PROT1, path);
        } else {
            editor.putString(KEY_PROT2, path);
        }
        editor.commit();
    }

    public static String getPort(Context c, int port) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        if (port == 1) {
            return sp.getString(KEY_PROT1, Utils.SERIAL_PROT_1);
        } else {
            return sp.getString(KEY_PROT2, Utils.SERIAL_PROT_2);
        }
    }

    public static void setQrCode(Context c, String url) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        LogUtils.d(TAG, "updateQrCode = " + url);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_QRCODE, url);
        editor.commit();
    }

    public static void setDebug(Context c, boolean isEnable) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(KEY_DEBUG, isEnable);
        editor.commit();
    }

    public static boolean getDebug(Context c) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getBoolean(KEY_DEBUG, Utils.DEBUG);
    }

    public static String getQrCode(Context c) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getString(KEY_QRCODE, "");
    }

    public static void setRegister(Context c, boolean able) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        LogUtils.d(TAG, "setRegister = " + able);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(KEY_REGISTER, able);
        editor.commit();
    }

    public static boolean getRegister(Context c) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getBoolean(KEY_REGISTER, false);
    }
}