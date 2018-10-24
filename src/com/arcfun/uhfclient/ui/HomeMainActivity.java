package com.arcfun.uhfclient.ui;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arcfun.uhfclient.R;
import com.arcfun.uhfclient.data.DeviceInfo;
import com.arcfun.uhfclient.data.ImageLoader;
import com.arcfun.uhfclient.data.QrCode;
import com.arcfun.uhfclient.data.ResultInfo;
import com.arcfun.uhfclient.net.HttpRequest;
import com.arcfun.uhfclient.utils.Constancts;
import com.arcfun.uhfclient.utils.LogUtils;
import com.arcfun.uhfclient.utils.SharedPreferencesUtils;
import com.arcfun.uhfclient.utils.Utils;

public class HomeMainActivity extends AhsBaseActivity implements
        OnClickListener, OnLongClickListener {
    private static final String TAG = "Home";
    private LinearLayout mLayout;
    private Button mLoginBtn;
    private TextView mVersion;
    private ImageView mBanner;
    private AsyncTask<String, Void, String> mTask;
    private ImageLoader mImageLoader;
    private QrCode mCode;
    private String mToken;
    private int mPageCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);
        ((UHFApplication) getApplication()).addActivity(this);
        mImageLoader = new ImageLoader();
        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestSlide(Utils.buildSlideListJson(), mImageLoader, mBanner);
        if (!SharedPreferencesUtils.getRegister(this)) {
            requestSetPushCode(this, Utils.buildPushCode(mToken,
                    Utils.getRegistrationID(this)));
        }
        LogUtils.d(TAG, "onResume " + mPageCount);
    }

    private void requestSlide(final String json, final ImageLoader loader,
            final ImageView view) {
        String url = HttpRequest.URL_HEAD + HttpRequest.GET_SLIDE_LIST;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                if (data == null || data.length == 0) return "";

                Utils.dumpJson2Db(data, "banner");
                String result = new String(data);
                LogUtils.d(TAG, "SlideList:" + result);
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (view == null || loader == null) {
                    return;
                }
                if (result == null || result.isEmpty()) {
                    byte[] raw = Utils.getdumpFromDb("banner");
                    if (raw != null) {
                        LogUtils.d(TAG, "onPostExecute banner from Db");
                        result = new String(raw);
                    }
                }
                if (result != null && !result.isEmpty()) {
                    List<String> images = Utils.parseSlideList(result);
                    loader.loadImage(images.get(0), view);
                }
            }
        }.execute(url);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTask != null) {
            mTask.cancel(false);
        }
    }

    private void initData() {
        if (mToken == null || mToken.isEmpty()) {
            requestLogin(Utils.buildLoginJson(Utils.getImei(this)));
        }
    }

    private void initView() {
        mLayout = (LinearLayout) findViewById(R.id.slide_List_layout);
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mVersion = (TextView) findViewById(R.id.version);
        mBanner = (ImageView) findViewById(R.id.banner1);
        String version = Utils.getVersion(this);
        mVersion.setText(version);
        mLayout.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.slide_List_layout ||
                v.getId() == R.id.login_btn) {
            if (mToken == null || mToken.isEmpty()) {
                requestLogin(Utils.buildLoginJson(Utils.getImei(this)));
                Utils.showMsg(this,
                        getResources().getString(R.string.network_exception2)
                                + "IMEI = " + Utils.getImei(this));
                return;
            }
            if (Utils.getQrFile() == null) {
                requestUrl(Utils.buildQrJson(mToken));
                Utils.showMsg(this, R.string.network_exception1);
                return;
            }
            if (!SharedPreferencesUtils.getRegister(this)) {
                requestSetPushCode(this, Utils.buildPushCode(mToken,
                        Utils.getRegistrationID(this)));
                Utils.showMsg(this, R.string.network_exception);
                return;
            }
            Intent intent = new Intent(this, LoginMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void requestLogin(final String json) {
        String url = HttpRequest.URL_HEAD + HttpRequest.DEVICE_LOGIN;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                if (data == null) {
                    return null;
                }
                String result = new String(data);
                LogUtils.d(TAG, "requestLogin: " + result);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    DeviceInfo info = Utils.parseLoginResult(result);
                    if (info != null && info.getCode() == Utils.RESULT_OK) {
                        mToken = info.getToken();
                        SharedPreferencesUtils.setDeviceInfo(
                                HomeMainActivity.this, info);
                        requestUrl(Utils.buildQrJson(info.getToken()));
                    }
                }
            }
        }.execute(url);
    }

    private void requestUrl(final String json) {
        String url = HttpRequest.URL_HEAD + HttpRequest.GET_QR_CODE;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                if (data == null) {
                    return null;
                }
                String result = new String(data);
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    mCode = Utils.parseQrResult(result);
                    if (mCode != null && mCode.getCode() == Utils.RESULT_OK) {
                        LogUtils.d(TAG, "requestUrl: " + mCode.getUrl());
                        buildQrFile(mCode.getUrl());
                        SharedPreferencesUtils.setQrCode(HomeMainActivity.this,
                                mCode.getUrl());
                    }
                }
            }
        }.execute(url);
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

    private void buildQrFile(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Utils.createQRcode(url, Constancts.PIX_QR_CODE,
                        Constancts.PIX_QR_CODE);
            }
        }).start();
    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }

}