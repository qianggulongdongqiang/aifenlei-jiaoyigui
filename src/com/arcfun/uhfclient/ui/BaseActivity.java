package com.arcfun.uhfclient.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcfun.uhfclient.R;
import com.arcfun.uhfclient.data.GoodInfo;
import com.arcfun.uhfclient.data.ImageLoader;
import com.arcfun.uhfclient.net.HttpRequest;
import com.arcfun.uhfclient.utils.Constancts;
import com.arcfun.uhfclient.utils.LogUtils;
import com.arcfun.uhfclient.utils.Utils;
import com.reader.helper.ReaderHelper;
import com.uhf.uhf.serialport.SerialPort;

public class BaseActivity extends Activity implements OnClickListener {
    protected static final String TAG = "BaseActivity";
    protected ReaderHelper mReaderHelper1, mReaderHelper2;
    private SerialPort mSerialPort1, mSerialPort2;
    protected TextView mHomeBtn;
    protected View mExchangeView, mTransactionView;
    private Button mLoginBtn;
    protected List<GoodInfo> mInfos = new ArrayList<GoodInfo>();

    protected static final int MSG_UPDATE_WEIGHT =1;
    protected static final int MSG_UPDATE_ACCOUNT=2;
    protected static final int MSG_UPDATE_DEBUG=3;
    protected static final int MSG_UPDATE_WELCOME=4;
    protected static final int MSG_UPDATE_WELCOME_LIST=5;
    protected static final int MSG_UPDATE_RFID_NOEXIST=6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mReaderHelper1 = ReaderHelper.getDevHelper();
            mReaderHelper2 = ReaderHelper.getEpcHelper();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void bindView() {
        mHomeBtn = (TextView) findViewById(R.id.main_btn);
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mHomeBtn.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isCurrentPage(v)) return;
        switch (v.getId()) {
        case R.id.main_btn:
            startActivity(new Intent(this, HomeMainActivity.class));
            break;
        case R.id.transaction_btn:
            startActivity(new Intent(this, TransactionActivity.class));
            break;
        case R.id.exchange_btn:
            if (Utils.FEATURE_EXCHANGE) {
                startActivity(new Intent(this, ExchangeActivity.class));
            } else {
                Toast.makeText(this, getString(R.string.feature_not_online),
                        Toast.LENGTH_SHORT).show();
            }
            break;
        case R.id.login_btn:
            break;
        default:
            break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            overridePendingTransition(R.anim.anim_enter,R.anim.anim_exit);
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void initPort1() {
        try { //1
            mSerialPort1 = new SerialPort(
                    new File(Utils.SERIAL_PROT_1), Utils.BAUD_RATE_DEF, 0);
            mReaderHelper1.setReader(mSerialPort1.getInputStream(),
                    mSerialPort1.getOutputStream(), Constancts.TYPE_DEV);
        } catch (Exception e) {
        }
    }

    protected void initPort2() {
        try { //2
            mSerialPort2 = new SerialPort(
                    new File(Utils.SERIAL_PROT_2), Utils.BAUD_RATE_UHF, 0);
            mReaderHelper2.setReader(mSerialPort2.getInputStream(),
                    mSerialPort2.getOutputStream(), Constancts.TYPE_EPC);
        } catch (Exception e) {
        }
    }

    protected void requestURL() {
        String url = HttpRequest.URL_HEAD + HttpRequest.GET_SEC_GOOD;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], "");
                if (data == null || data.length == 0) return "";

                Utils.dumpJson2Db(data, "goods");
                String result = new String(data);
                LogUtils.d(TAG, "SecGoods:" + result);
                mInfos = Utils.parseJsonData(result);
                ((UHFApplication) getApplication()).setInfos(mInfos);
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result == null || result.isEmpty()) {
                    byte[] raw = Utils.getdumpFromDb("goods");
                    if (raw != null) {
                        LogUtils.d(TAG, "onPostExecute goods from Db");
                        String dump = new String(raw);
                        mInfos = Utils.parseJsonData(dump);
                        ((UHFApplication) getApplication()).setInfos(mInfos);
                    }
                    Toast.makeText(BaseActivity.this, getString(R.string.network_exception),
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }.execute(url);
    }

    protected void requestSlideList(final String json, final ImageLoader loader,
            final List<ImageView> views) {
        String url = HttpRequest.URL_HEAD + HttpRequest.GET_SLIDE_LIST;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                if (data == null || data.length == 0) return "";

                Utils.dumpJson2Db(data, "slides");
                String result = new String(data);
                LogUtils.d(TAG, "SlideList:" + result);
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (views == null || loader == null) {
                    return;
                }
                if (result == null || result.isEmpty()) {
                    byte[] raw = Utils.getdumpFromDb("slides");
                    if (raw != null) {
                        LogUtils.d(TAG, "onPostExecute slides from Db");
                        result = new String(raw);
                    }
                    Toast.makeText(BaseActivity.this, getString(R.string.network_exception),
                            Toast.LENGTH_LONG).show();
                }
                List<String> images = Utils.parseSlideList(result);
                for (int j = 0; j < images.size(); j++) {
                    loader.loadImage(images.get(j), views.get(j));
                }
            }
        }.execute(url);
    }

    private boolean isCurrentPage(View v) {
        switch (v.getId()) {
        case R.id.main_btn:
            if (getClass().equals(HomeMainActivity.class)) return true;
            break;
        case R.id.transaction_btn:
            if (getClass().equals(TransactionActivity.class)) return true;
            break;
        case R.id.exchange_btn:
            if (getClass().equals(ExchangeActivity.class)) return true;
            break;
        default:
            return false;
        }
        return false;
    }
}