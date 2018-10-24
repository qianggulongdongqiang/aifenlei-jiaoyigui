package com.arcfun.uhfclient.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcfun.uhfclient.R;
import com.arcfun.uhfclient.data.GoodInfo;
import com.arcfun.uhfclient.data.OwnerInfo;
import com.arcfun.uhfclient.net.HttpRequest;
import com.arcfun.uhfclient.utils.Constancts;
import com.arcfun.uhfclient.utils.CountDownTimerHelper;
import com.arcfun.uhfclient.utils.CountDownTimerHelper.OnFinishListener;
import com.arcfun.uhfclient.utils.LogUtils;
import com.arcfun.uhfclient.utils.SharedPreferencesUtils;
import com.arcfun.uhfclient.utils.Utils;
import com.arcfun.uhfclient.view.BaseLoginFragment.OnActionCallBack;
import com.arcfun.uhfclient.view.PhoneEnsureFragment;
import com.arcfun.uhfclient.view.PhoneLoginFragment;
import com.arcfun.uhfclient.view.RFIDEnsureFragment;
import com.arcfun.uhfclient.view.RFIDListFragment;
import com.arcfun.uhfclient.view.RFIDLoginFragment;
import com.arcfun.uhfclient.view.WeixinEnsureFragment;
import com.arcfun.uhfclient.view.WeixinLoginFragment;
import com.reader.base.StringTool;
import com.reader.helper.ReaderHelper.Listener;

public class LoginMainActivity extends AhsBaseActivity implements Listener,
        OnFinishListener, OnActionCallBack {
    private ImageView mBack;
    private TextView mLable;
    private AsyncTask<String, Void, String> mRFIDTask = null;
    /** rfid */
    private RFIDLoginFragment mRFIDFragment;
    private RFIDEnsureFragment mRfidEnsureFragment;
    private RFIDListFragment mRfidListFragment;

    /** wechat */
    private WeixinLoginFragment mWeixinFragment;
    private WeixinEnsureFragment mWeixinEnsureFragment;

    /** phone */
    private PhoneLoginFragment mPhoneFragment;
    private PhoneEnsureFragment mPhoneEnsureFragment;

    private FragmentManager mManager;
    private FragmentTransaction mTransaction;
    private Button mCountDown;
    private CountDownTimerHelper mHelper;
    /** 扫描登录 */
    private OwnerInfo mInfo;
    private List<OwnerInfo> mOwnerList;
    private List<String> mRfidList = new ArrayList<String>();
    /** 工控机的数据 */
    private String mEpcData = null;
    protected List<GoodInfo> mInfos = new ArrayList<GoodInfo>();

    public static final String POSITION = "position";
    public static final int FRAGMENT_DEFAULT = 0;
    public static final int FRAGMENT_LOGIN_RFID_1 = FRAGMENT_DEFAULT;
    public static final int FRAGMENT_LOGIN_RFID_2 = 1;
    public static final int FRAGMENT_LOGIN_RFID_3 = 2;
    public static final int FRAGMENT_LOGIN_WEIXIN_1 = 10;
    public static final int FRAGMENT_LOGIN_WEIXIN_2 = 11;
    public static final int FRAGMENT_LOGIN_PHONE_1 = 20;
    public static final int FRAGMENT_LOGIN_PHONE_2 = 21;
    private int mPosition = FRAGMENT_DEFAULT;
    public static final int PERIOD = 200 * 1000;
    public static final int INTERVAL = 1 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ahs_login_home);
        ((UHFApplication) getApplication()).addActivity(this);
        mManager = getSupportFragmentManager();
        initView();
        initSerialPort();
        initData();
    }

    private void initView() {
        showFragment(FRAGMENT_DEFAULT);
        mBack = (ImageView) findViewById(R.id.login_back);
        mLable = (TextView) findViewById(R.id.barcode_text);
        mCountDown = (Button) findViewById(R.id.login_count_down);
        mHelper = new CountDownTimerHelper(mCountDown, PERIOD, INTERVAL);
        mHelper.setOnFinishListener(this);
        mBack.setOnClickListener(this);
        if (Utils.DEBUG) {
            //updateEpcMap("002101000489");
            //updateEpcMap("002101000490");
            //updateEpcMap("002101000491");
            getOwnerByRfid(Utils.buildRfidJson("ABC11111111"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String lable = SharedPreferencesUtils.getSignature(getApplicationContext());
        mLable.setText(getString(R.string.title_des4, lable));
        mHelper.start();
        mReaderHelper2.setListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHelper.stop();
        mReaderHelper2.setListener(null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mPosition == FRAGMENT_LOGIN_WEIXIN_1) {
            mInfo = intent.getParcelableExtra("owner_info");
            LogUtils.d(TAG, "onNewIntent " + mInfo.toString());
            showFragment(FRAGMENT_LOGIN_WEIXIN_2);
        }
        super.onNewIntent(intent);
    }

    private void initData() {
        mInfo = null;//reset best info
        mOwnerList = null;
        try {
            requestURL();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateEpcMap(String info) {
        if (mRfidList.isEmpty() || !mRfidList.contains(info)) {
            mRfidList.add(info);
        }
    }

    private void clear() {
        mRfidList.clear();
        mOwnerList = null;
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
                    Toast.makeText(LoginMainActivity.this, getString(R.string.network_exception),
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }.execute(url);
    }

    private void getOwnerByRfid(final String json) {
        String url = HttpRequest.URL_HEAD + HttpRequest.GET_CUSTOMER_BY_RFIDS;
        if (mRFIDTask != null) {
            mRFIDTask.cancel(true);
        }
        mRFIDTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                if (data == null) {
                    return null;
                }
                String result = new String(data);
                LogUtils.d(TAG, "Owner:" + result);
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    List<OwnerInfo> best = Utils.parseOwnerData(result);
                    if (best != null && best.size() > 0) {
                        mInfo = best.get(0);
                        if (mInfo != null && mPosition == FRAGMENT_LOGIN_RFID_1) {
                            showFragment(FRAGMENT_LOGIN_RFID_2);
                        }
                    }
                }
                mEpcData = null;
            };
        }.execute(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.login_back:
            if (mPosition == FRAGMENT_LOGIN_RFID_1) {
                onBackPressed();
                return;
            }
            showFragment(getLastId(mPosition));
            break;

        default:
            showFragment(FRAGMENT_LOGIN_WEIXIN_1);
            break;
        }
    }

    private int getLastId(int index) {
        switch (index) {
        case FRAGMENT_LOGIN_RFID_1:
            return FRAGMENT_LOGIN_RFID_1;
        case FRAGMENT_LOGIN_WEIXIN_1:
            return FRAGMENT_LOGIN_RFID_1;
        case FRAGMENT_LOGIN_PHONE_1:
            return FRAGMENT_LOGIN_RFID_1;

        default:
            return --index;
        }
    }

    @Override
    public void onLostConnect(int type) {
        Toast.makeText(this,
                getResources().getString(R.string.rs232_lost_connect),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRawInfo(int type, byte[] receiveData) {
    }

    @Override
    public void onEPCInfo(int type, byte[] epcData) {
        handleEpcInfo(epcData);
    }

    @Override
    public void onDEVInfo(int type, byte[] devData) {}

    private void handleEpcInfo(byte[] epcData) {
        if (epcData != null && epcData.length == Constancts.LENGTH_EPC) {
            String code = StringTool.decodeBytes(epcData, 1, 13);
            updateEpcMap(code);
            if (mEpcData != code && mPosition == FRAGMENT_LOGIN_RFID_1) {
                mEpcData = code;
                try {
                    getOwnerByRfid(Utils.buildRfidJson(code));
                } catch (Exception e) {
                    mEpcData = null;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEpcData = null;
        if (mRFIDTask != null) {
            mRFIDTask.cancel(false);
            mRFIDTask = null;
        }
        mRFIDFragment = null;
        mRfidEnsureFragment = null;
        mRfidListFragment = null;
        mWeixinFragment = null;
        mWeixinEnsureFragment = null;
        mPhoneFragment = null;
        mPhoneEnsureFragment = null;
    }

    @Override
    public void onUpdate(int index, Parcelable value) {
        LogUtils.d(TAG, "onUpdate " + index);
        if (value != null) {
            mInfo = (OwnerInfo) value;
        }
        showFragment(index);
    }

    @Override
    public void onFinish() {
        onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(POSITION, mPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        showFragment(savedInstanceState.getInt(POSITION));
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void showFragment(int index) {
        LogUtils.d(TAG, "showFragment pos = " + index);
        switch (index) {
            case FRAGMENT_LOGIN_RFID_2 + 2:
            case FRAGMENT_LOGIN_WEIXIN_2 + 1:
            case FRAGMENT_LOGIN_PHONE_2 + 1:
                Intent intent = new Intent(LoginMainActivity.this,
                        TransactionActivity.class);
                intent.putExtra("owner_info", mInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                clear();
                finish();
                return;

            default:
                break;
        }
        mTransaction = mManager.beginTransaction();
        hideAllFragment();
        mPosition = index;
        switch (index) {
        case FRAGMENT_LOGIN_RFID_1:
            if (mRFIDFragment == null) {
                mRFIDFragment = new RFIDLoginFragment(this, index);
                mTransaction.add(R.id.content, mRFIDFragment);
            } else {
                mTransaction.show(mRFIDFragment);
            }
            break;
        case FRAGMENT_LOGIN_RFID_2:
            if (mRfidEnsureFragment == null) {
                mRfidEnsureFragment = new RFIDEnsureFragment(this, mInfo, index);
                mTransaction.add(R.id.content, mRfidEnsureFragment);
            } else {
                mRfidEnsureFragment.setInfo(mInfo);
                mTransaction.show(mRfidEnsureFragment);
            }
            break;
        case FRAGMENT_LOGIN_RFID_3:
            if (mOwnerList == null) {
                return;
            }
            if (mRfidListFragment == null) {
                mRfidListFragment = new RFIDListFragment(this, mOwnerList, index);
                mTransaction.add(R.id.content, mRfidListFragment);
            } else {
                mTransaction.remove(mRfidListFragment);
                mRfidListFragment = new RFIDListFragment(this, mOwnerList, index);
                mTransaction.add(R.id.content, mRfidListFragment);
            }
            break;
        case FRAGMENT_LOGIN_WEIXIN_1:
            if (mWeixinFragment == null) {
                mWeixinFragment = new WeixinLoginFragment(this, index);
                mTransaction.add(R.id.content, mWeixinFragment);
            } else {
                mTransaction.show(mWeixinFragment);
            }
            break;
        case FRAGMENT_LOGIN_WEIXIN_2:
            if (mWeixinEnsureFragment == null) {
                mWeixinEnsureFragment = new WeixinEnsureFragment(this, mInfo,
                        index);
                mTransaction.add(R.id.content, mWeixinEnsureFragment);
            } else {
                mWeixinEnsureFragment.setInfo(mInfo);
                mTransaction.show(mWeixinEnsureFragment);
            }
            break;
        case FRAGMENT_LOGIN_PHONE_1:
            if (mPhoneFragment == null) {
                mPhoneFragment = new PhoneLoginFragment(this, index);
                mTransaction.add(R.id.content, mPhoneFragment);
            } else {
                mTransaction.show(mPhoneFragment);
            }
            break;
        case FRAGMENT_LOGIN_PHONE_2:
            if (mPhoneEnsureFragment == null) {
                mPhoneEnsureFragment = new PhoneEnsureFragment(this, mInfo,
                        index);
                mTransaction.add(R.id.content, mPhoneEnsureFragment);
            } else {
                mPhoneEnsureFragment.setInfo(mInfo);
                mTransaction.show(mPhoneEnsureFragment);
            }
            break;
        }

        mTransaction.commitAllowingStateLoss();
    }

    /** must commit end */
    public void hideAllFragment() {
        if (mRFIDFragment != null) {
            mTransaction.hide(mRFIDFragment);
        }
        if (mRfidEnsureFragment != null) {
            mTransaction.hide(mRfidEnsureFragment);
        }
        if (mRfidListFragment != null) {
            mTransaction.hide(mRfidListFragment);
        }
        if (mWeixinFragment != null) {
            mTransaction.hide(mWeixinFragment);
        }
        if (mWeixinEnsureFragment != null) {
            mTransaction.hide(mWeixinEnsureFragment);
        }
        if (mPhoneFragment != null) {
            mTransaction.hide(mPhoneFragment);
        }
        if (mPhoneEnsureFragment != null) {
            mTransaction.hide(mPhoneEnsureFragment);
        }
    }

    private void initSerialPort() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                initSerialPort2();
                LogUtils.d(TAG, "initSerialPort");
                return null;
            }

            protected void onPostExecute(Void result) {
                mReaderHelper2.setListener(LoginMainActivity.this);
            }
        }.execute();
    }

}