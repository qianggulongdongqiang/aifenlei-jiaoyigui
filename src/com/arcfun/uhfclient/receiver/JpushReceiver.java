package com.arcfun.uhfclient.receiver;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;

import com.arcfun.uhfclient.data.OwnerInfo;
import com.arcfun.uhfclient.data.ResultInfo;
import com.arcfun.uhfclient.net.HttpRequest;
import com.arcfun.uhfclient.ui.LoginMainActivity;
import com.arcfun.uhfclient.utils.Constancts;
import com.arcfun.uhfclient.utils.LogUtils;
import com.arcfun.uhfclient.utils.SharedPreferencesUtils;
import com.arcfun.uhfclient.utils.Utils;

public class JpushReceiver extends BroadcastReceiver {
    private static final String TAG = "JpushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            LogUtils.d(TAG, "[MyReceiver] onReceive - " + intent.getAction()
                    + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID
                    .equals(intent.getAction())) {
                String regId = bundle
                        .getString(JPushInterface.EXTRA_REGISTRATION_ID);
                LogUtils.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                // send the Registration Id to your server...
                String token = SharedPreferencesUtils.getToken(context);
                requestSetPushCode(context, Utils.buildPushCode(token, regId));

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
                    .getAction())) {
                LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: "
                                + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                processCustomMessage(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED
                    .equals(intent.getAction())) {
                LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                int type;
                if (bundle != null) {
                    type = getNotificationType(bundle);
                    switch (type) {
                    case Constancts.TYPE_LOGIN_WEIXIN:
                        OwnerInfo info = getNotificationData(bundle);
                        if (info != null) {
                            Intent loginIntent = new Intent(context,
                                    LoginMainActivity.class);
                            loginIntent.putExtra("owner_info", info);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(loginIntent);
                        }
                        break;
                    case Constancts.TYPE_UPDATE_IMEI:
                        break;

                    default:
                        break;
                    }
                    LogUtils.d(TAG, "getNotificationType= " + type);
                }
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
                    .getAction())) {
                LogUtils.d(TAG, "[MyReceiver] 用户点击打开了通知");

                // 打开自定义的Activity
                /*Intent i = new Intent(context, HomeMainActivity.class);
                i.putExtras(bundle);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);*/

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
                    .getAction())) {
                LogUtils.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
                        + bundle.getString(JPushInterface.EXTRA_EXTRA));
                // 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，
                // 比如打开新的Activity， 打开一个网页等...

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
                    .getAction())) {
                boolean connected = intent.getBooleanExtra(
                        JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                LogUtils.w(TAG, "[MyReceiver]" + intent.getAction()
                        + " connected state change to " + connected);
            } else {
                LogUtils.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "Exception " , e);
        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle
                        .getString(JPushInterface.EXTRA_EXTRA))) {
                    LogUtils.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(
                            bundle.getString(JPushInterface.EXTRA_EXTRA));
                    @SuppressWarnings("unchecked")
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" + myKey + " - "
                                + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    LogUtils.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return sb.toString();
    }

    // send message to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Intent msgIntent = new Intent(Constancts.MESSAGE_RECEIVED_ACTION);
        msgIntent.putExtra(Constancts.KEY_MESSAGE, message);
        if (!Utils.isEmpty(extras)) {
            try {
                JSONObject extraJson = new JSONObject(extras);
                if (extraJson.length() > 0) {
                    msgIntent.putExtra(Constancts.KEY_EXTRAS, extras);
                }
            } catch (JSONException e) {
                LogUtils.e(TAG, "", e);
            }

        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
    }

    private int getNotificationType(Bundle bundle) {
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        if (!Utils.isEmpty(extras)) {
            try {
                JSONObject extraJson = new JSONObject(extras);
                if (extraJson.length() > 0) {
                    return extraJson.optInt("type");
                }
            } catch (JSONException e) {
                LogUtils.e(TAG, "", e);
            }

        }
        return -1;
    }

    private OwnerInfo getNotificationData(Bundle bundle) {
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        if (!Utils.isEmpty(extras)) {
            try {
                JSONObject extraJson = new JSONObject(extras);
                JSONObject user = extraJson.optJSONObject("user");
                if (extraJson.length() > 0) {
                    return new OwnerInfo(user.optInt("id"),
                            user.optString("user_nickname"),
                            user.optInt("score"),
                            user.optString("mobile"));
                }
            } catch (JSONException e) {
                LogUtils.e(TAG, "", e);
            }

        }
        return null;
    }


    private void requestSetPushCode(final Context c, final String json) {
        String url = HttpRequest.URL_HEAD + HttpRequest.SET_PUSH_CODE;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                if (data == null) {
                    return null;
                }
                String result = new String(data);
                LogUtils.d(TAG, "requestSetPushCode:" + result);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                ResultInfo resultInfo = null;
                if (result != null) {
                    resultInfo = Utils.parsePushCode(result);
                }
                if (resultInfo != null) {
                    LogUtils.d(TAG, "requestSetPushCode Registration Id sucess.");
                    SharedPreferencesUtils.setRegister(c,
                            resultInfo.getCode() == Utils.RESULT_OK);
                }
            }
        }.execute(url);
    }

}