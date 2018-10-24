package com.arcfun.uhfclient.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcfun.uhfclient.R;
import com.arcfun.uhfclient.data.AddAndSubView;
import com.arcfun.uhfclient.data.AddAndSubView.OnNumChangeListener;
import com.arcfun.uhfclient.data.CirclePageIndicator;
import com.arcfun.uhfclient.data.GoodInfo;
import com.arcfun.uhfclient.data.GridAdapter;
import com.arcfun.uhfclient.data.MessageInfo;
import com.arcfun.uhfclient.data.OwnerInfo;
import com.arcfun.uhfclient.data.ViewPagerAdapter;
import com.arcfun.uhfclient.ui.PopDialog.OnUpdateListener;
import com.arcfun.uhfclient.ui.PopResultDialog.OnResultListener;
import com.arcfun.uhfclient.utils.LogUtils;
import com.arcfun.uhfclient.utils.Utils;
import com.reader.base.StringTool;
import com.reader.helper.ReaderHelper.Listener;

public class TransactionActivity extends BaseActivity implements
        OnClickListener, Listener, OnNumChangeListener, OnUpdateListener,
        OnResultListener {
    private TextView mAccountText, mCurCredit, mCurWeight, mCreditInfo;
    private TextView mWeightTitle, mWeightDes, mVersion;
    private AddAndSubView mPieceView;
    private Button mSendBtn;

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private CirclePageIndicator mViewPagerIndicator;
    private List<GridView> mGridList = new ArrayList<GridView>();
    private GoodInfo mGoodInfo;
    private MessageInfo info;
    private OwnerInfo mOwner;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            String data = (String) msg.obj;
            switch (msg.what) {
            case MSG_UPDATE_WEIGHT:
                try {
                    updateWeightInfo(data);
                    getMessageInfo().setWeight(data);
                    if (mGoodInfo != null && mGoodInfo.getType() == Utils.TRANS_WEIGHT) {
                        float value = Float.valueOf(data.split("kg")[0]);
                        int income = Math.round(value * mGoodInfo.getPoint());
                        getMessageInfo().setIncome(income);
                        updateCreditInfo("" + income);
                    }
                } catch (Exception e) {
                }
                break;
            case MSG_UPDATE_DEBUG:
                Utils.showMsg(TransactionActivity.this, "Dev " + data);
                break;

            default:
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ((UHFApplication) getApplication()).addActivity(this);
        Intent intent = getIntent();
        if (intent != null) {
            mOwner = intent.getParcelableExtra("owner_info");
            if (mOwner == null) {
                mOwner = new OwnerInfo(0, "Guest", 0, "");
            }
        }

        initView();
        initSerialPort();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReaderHelper1.setListener(this);
    }

    private void initView() {
        bindView();
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mAccountText = (TextView) findViewById(R.id.user_info);
        mCurCredit = (TextView) findViewById(R.id.cur_credit_info);
        mCurWeight = (TextView) findViewById(R.id.weight_detail);
        mCreditInfo = (TextView) findViewById(R.id.credit_info);
        mSendBtn = (Button) findViewById(R.id.ensure_pad);
        mWeightTitle = (TextView) findViewById(R.id.weight_info);
        mWeightDes = (TextView) findViewById(R.id.weight_tip);
        mPieceView = (AddAndSubView) findViewById(R.id.weight_pieces);
        mPieceView.setOnNumChangeListener(this);
        mSendBtn.setOnClickListener(this);
        mVersion = (TextView) findViewById(R.id.version);
        String version = Utils.getVersion(this);
        mVersion.setText(version);
        if (mOwner != null) {
            updateAccountInfo(mOwner.getNickName(), mOwner.getScore());
        }
        updateWeigthDes(true);
        updateWeightInfo("0.00 kg");
        updateCreditInfo("0");
        mViewPagerAdapter = new ViewPagerAdapter();
        mViewPagerIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

        initData();
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPagerIndicator.setViewPager(mViewPager);

    }

    private void initSerialPort() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                initPort1();
                LogUtils.d(TAG, "initSerialPort");
                return null;
            }

            protected void onPostExecute(Void result) {
                mReaderHelper1.setListener(TransactionActivity.this);
            }
        }.execute();
    }

    private void revertWeight(GoodInfo info) {
        int type = (info != null) ? info.getType() : Utils.TRANS_WEIGHT;
        boolean isWeight = type == Utils.TRANS_WEIGHT;
        mCurWeight.setVisibility(isWeight ? View.VISIBLE : View.GONE);
        mPieceView.setVisibility(isWeight ? View.GONE : View.VISIBLE);
        mWeightTitle.setText(getResources().getString(isWeight ?
                R.string.weight_des1 : R.string.weight_des2));
        updateWeigthDes(isWeight);
    }

    private float getWeightTitle(boolean isWeight) {
        if (mGoodInfo != null) {
            return mGoodInfo.getPoint();
        } else {
            return 0.0f;
        }
    }

    private void updateWeightInfo(String num) {
        mCurWeight.setText(num);
    }

    private void updateCreditInfo(String credit) {
        mCreditInfo.setText(credit);
    }

    private void updateWeigthDes(boolean isWeight) {
        mWeightDes.setText(getResources().getString(isWeight ?
                R.string.weight_tip1 : R.string.weight_tip2,
                getWeightTitle(isWeight)));
    }

    private void updateAccountInfo(String name, int curCredit) {
        if (name != null && !name.isEmpty()) {
            mAccountText.setText(Html.fromHtml(getResources().getString(
                    R.string.account_detail,
                    "<font color='#000000'><big>" + name + "</big></font>")));
        } else {
            mAccountText.setText(R.string.account_detail3);
        }
        mAccountText.append(getResources().getString(R.string.account_detail2));
        if (curCredit >=0) {
            mCurCredit.setText(Html.fromHtml(getResources().getString(
                    R.string.credit_detail,
                    "<font color='#f4c520'><big><b>" + curCredit + "<\b></big></font>")));
        }
    }

    private void initData() {
        mInfos = ((UHFApplication) getApplication()).getInfos();
        int count = (mInfos != null) ? mInfos.size() : 0;
        int pageSize = (count % Utils.MAX_GRID_VIEW_NUM == 0)
                ? (count) / Utils.MAX_GRID_VIEW_NUM
                : (count) / Utils.MAX_GRID_VIEW_NUM +1;
        for (int i = 0; i < pageSize; i++) {
            GridView gridView = (GridView) View.inflate(this, R.layout.gridview_layout, null);
            final GridAdapter adapter = new GridAdapter(this, mInfos, i);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id) {
                    for (GridView grid : mGridList) {
                        GridAdapter item = (GridAdapter)grid.getAdapter();
                        if (item.getPageCount() == adapter.getPageCount()) {
                            item.revertSelection(position);
                        } else {
                            item.resetSelection();
                        }
                        item.notifyDataSetChanged();
                    }
                    mGoodInfo = adapter.isSelected() ?
                            (GoodInfo)adapter.getItem(position) : null;
                    LogUtils.d(TAG, position + "\t" + (mGoodInfo != null ?
                            mGoodInfo.toString() : "null"));
                    if (mGoodInfo != null) {
                        info = null;//reset info
                        mPieceView.resetNum(mGoodInfo.getType());
                        getMessageInfo().updateGoodInfo(mGoodInfo);
                        getMessageInfo().setPiece(1);
                    } else {
                        mPieceView.resetNum(Utils.TRANS_WEIGHT);
                        updateCreditInfo("0");
                    }
                    revertWeight(mGoodInfo);
                }
            });
            mGridList.add(gridView);
        }
        mViewPagerAdapter.add(mGridList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mReaderHelper1.setListener(null);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ensure_pad:
                createDialog();
                break;

            default:
                break;
        }
    }

    private void createDialog() {
        MessageInfo info = getMessageInfo();
        if (info != null) {
            Dialog dialog = new PopDialog(this, info, this);
            dialog.show();
        }
    }

    private MessageInfo getMessageInfo() {
        if (mGoodInfo == null) {
            return null;
        }
        if (info == null) {
            if (mOwner != null) {
                info = new MessageInfo(mGoodInfo, mOwner, mGoodInfo.getName(), "0.00 kg", 1, 0);
            } else {
                // no Internet mOwner is empty
                info = new MessageInfo(mGoodInfo, 0, "", "", 0 , mGoodInfo.getName(), "0.00 kg", 0, 0);
            }
        } else if (mOwner != null) {
            info.setScore(mOwner.getScore());
        }
        return info;
    }

    @Override
    public void onRawInfo(int type, byte[] btData) {}

    @Override
    public void onEPCInfo(int type, byte[] btData) {}

    @Override
    public void onDEVInfo(int type, byte[] weigthData) {
        String value = StringTool.decodeBytesForWeight(weigthData).replaceAll(" +","");
        float weight = 0.0f;
        if (value != null && !value.isEmpty()) {
            weight = Float.valueOf(value);
        }
        Message msg = mHandler.obtainMessage(MSG_UPDATE_WEIGHT);
        msg.obj = weight + "kg";
        msg.sendToTarget();
    }

    @Override
    public void onLostConnect(int type) {
        Toast.makeText(this, getResources().getString(R.string.rs232_lost_connect),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNumChange(View view, int type, int num) {
        if (mGoodInfo != null) {
            int income = Math.round(mGoodInfo.getPoint() * num);
            getMessageInfo().setIncome(income);
            getMessageInfo().setPiece(num);
            updateCreditInfo("" + income);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void OnUpdate(int score) {
        updateAccountInfo(null, score);
        if (mOwner != null) {
            mOwner.setScore(score);
        }
    }

    @Override
    public void onSuccess(int type) {
        startActivity(new Intent(this, HomeMainActivity.class));
    }

    @Override
    public void onBack(int type) {
    }

    @Override
    public void onNext(int type) {
        if (type == 1) {
            if (Utils.FEATURE_EXCHANGE) {
                startActivity(new Intent(this, ExchangeActivity.class));
            } else {
                Toast.makeText(this, getString(R.string.feature_not_online),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            createDialog();
        }
    }

}