package com.user.box;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.reader.base.StringTool;
import com.reader.helper.ReaderHelper;
import com.reader.helper.ReaderHelper.Listener;
import com.uhf.uhf.R;
import com.uhf.uhf.serialport.SerialPort;
import com.user.box.data.CirclePageIndicator;
import com.user.box.data.GridAdapter;
import com.user.box.data.MessageInfo;
import com.user.box.data.ViewPagerAdapter;
import com.user.box.utils.Utils;

public class TransactionActivity extends BaseActivity implements OnClickListener, Listener{
    private TextView mAccountText, mCurCredit, mAvaCredit, mCurWeight, mCreditInfo;
    private Button mSendBtn;
    private Dialog mDialog;

    private int[] mIcons = new int[28];
    private ReaderHelper mReaderHelper1, mReaderHelper2;
    private SerialPort mSerialPort1, mSerialPort2;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private CirclePageIndicator mViewPagerIndicator;
    private List<GridView> mGridList = new ArrayList<GridView>();
    private String[] mName = new String[6];
    private String[] mEpcID = new String[5];
    private String mUserName = "";
    private int mUserCredit = 6000;

    private static final int MSG_UPDATE_WEIGHT =1;
    private static final int MSG_UPDATE_ACCOUNT=2;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            String data = (String) msg.obj;
            switch (msg.what) {
            case MSG_UPDATE_WEIGHT:
                updateWeightInfo(data);
                try {
                    float value = Float.valueOf(data.replace(" ", "").split("kg")[0]);
                    updateCreditInfo("" + value * 10);
                } catch (Exception e) {
                }
                break;
            case MSG_UPDATE_ACCOUNT:
                updateAccountInfo(getInfoName(data), mUserCredit, mUserCredit);
                break;

            default:
                break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ((UHFApplication) getApplication()).addActivity(this);

        initView();
        try {
            mReaderHelper1 = ReaderHelper.getInfoHelper();
            mReaderHelper2 = ReaderHelper.getEpcHelper();
            mReaderHelper1.setListener(this);
            mReaderHelper2.setListener(this);
            intPort();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        bindView();
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mAccountText = (TextView) findViewById(R.id.user_info);
        mCurCredit = (TextView) findViewById(R.id.cur_credit_info);
        mAvaCredit = (TextView) findViewById(R.id.ava_credit_info);
        mCurWeight = (TextView) findViewById(R.id.weight_detail);
        mCreditInfo = (TextView) findViewById(R.id.credit_info);
        mUserName = getResources().getString(R.string.name6);
        mSendBtn = (Button) findViewById(R.id.ensure_pad);
        mSendBtn.setOnClickListener(this);
        updateAccountInfo(mUserName, mUserCredit, mUserCredit);
        updateWeightInfo("0.00 kg");
        updateCreditInfo("66.6");
        mViewPagerAdapter = new ViewPagerAdapter();
        mViewPagerIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

        initData();
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPagerIndicator.setViewPager(mViewPager);

    }

    private void updateWeightInfo(String num) {
        mCurWeight.setText(num);
    }

    private void updateCreditInfo(String credit) {
        mCreditInfo.setText(credit);
    }

    private void updateAccountInfo(String name, int curCredit, int avaCredit) {
        if (name != null) {
            mAccountText.setText(Html.fromHtml(getResources().getString(
                    R.string.account_detail,
                    "<font color='#000000'><big>" + name + "</big></font>")));
            mAccountText.append(getResources().getString(R.string.account_detail2));
        }
        if (curCredit >=0) {
            mCurCredit.setText(Html.fromHtml(getResources().getString(
                    R.string.credit_detail,
                    "<font color='#f4c520'><big><b>" + curCredit + "<\b></big></font>")));
        }
        if (avaCredit >=0) {
            mAvaCredit.setText(Html.fromHtml(getResources().getString(
                    R.string.credit_detail2,
                    "<font color='#f4c520'><big><b>" + avaCredit + "<\b></big></font>")));
        }
    }

    private void initData() {
        mName = getResources().getStringArray(R.array.name_list);
        mEpcID = getResources().getStringArray(R.array.epc_list);
        TypedArray ar = null;
        try {
            ar = getResources().obtainTypedArray(R.array.collect_icons_list);
            for (int i = 0; i < ar.length(); i++) {
                mIcons[i] = ar.getResourceId(i, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ar.recycle();
        }
        int pageSize = (mIcons.length % Utils.MAX_GRID_VIEW_NUM == 0)
                ? (mIcons.length / 2) / Utils.MAX_GRID_VIEW_NUM
                : (mIcons.length / 2) / Utils.MAX_GRID_VIEW_NUM +1;
        for (int i = 0; i < pageSize; i++) {
            GridView gridView = (GridView) View.inflate(this, R.layout.gridview_layout, null);
            final GridAdapter adapter = new GridAdapter(this, mIcons, i);
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
                }
            });
            mGridList.add(gridView);
        }
        mViewPagerAdapter.add(mGridList);
    }

    private void intPort() {
        try { //1
            mSerialPort1 = new SerialPort(
                    new File(Utils.SERIAL_PROT_1), Utils.BAUD_RATE_DEF, 0);
            mReaderHelper1.setReader(mSerialPort1.getInputStream(),
                    mSerialPort1.getOutputStream());
        } catch (Exception e) {
            Toast.makeText(this, getResources().getString(R.string.rs232_error)
                    + Utils.SERIAL_PROT_1, Toast.LENGTH_SHORT).show();
        }
        try { //2
            mSerialPort2 = new SerialPort(
                    new File(Utils.SERIAL_PROT_2), Utils.BAUD_RATE_UHF, 0);
            mReaderHelper2.setReader(mSerialPort2.getInputStream(),
                    mSerialPort2.getOutputStream());
        } catch (Exception e) {
            Toast.makeText(this, getResources().getString(R.string.rs232_error)
                    + Utils.SERIAL_PROT_2, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Log.i(TAG, "onClick: " + v.getId());
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
        mDialog = new PopDialog(this, info);
        mDialog.show();
    }

    private MessageInfo getMessageInfo() {
        MessageInfo info = new MessageInfo(
                Utils.TRANS_WEIGHT, "李彦磊", "13612346227", "SH00011",
                "纸板箱", "6.66kg", 0, 66.6f);
        return info;
    }

    public int checkDataType(final String data) {
        if (data.contains("6B670D0A")) {
            return Utils.TYPE_WEIGHT;
        }
        return Utils.TYPE_EPC;
    }

    @Override
    public void onRawInfo(byte[] receiveData) {
        Message msg = mHandler.obtainMessage(MSG_UPDATE_WEIGHT);
        msg.obj = "length:" + receiveData.length;
        /*msg.obj = receiveData.length + "\n" + StringTool.byteArrayToString(
                receiveData, 0, receiveData.length);*/
        msg.sendToTarget();
    }

    @Override
    public void onEPCInfo(byte[] epcData) {
        Message msg = mHandler.obtainMessage(MSG_UPDATE_ACCOUNT);
        String strLog = StringTool.byteArrayToString(epcData, 0, Utils.LENGTH_EPC);
        msg.obj = Utils.decode(strLog.replaceAll(" ", ""));
        msg.sendToTarget();
    }

    @Override
    public void onWeightInfo(byte[] weigthData) {
        Message msg = mHandler.obtainMessage(MSG_UPDATE_WEIGHT);
        String strLog = StringTool.byteArrayToString(weigthData, 1, weigthData.length - 1);
        msg.obj = Utils.decode(strLog.replaceAll(" ", ""));
        msg.sendToTarget();
    }

    private String getInfoName(String data) {
        for (int i = 0; i < mEpcID.length; i++) {
            if (mEpcID[i].equals(data)) {
                mUserName = mName[i];
                mUserCredit = 1000 * (i+1);
                return mName[i];
            }
        }
        return mUserName;
    }

    @Override
    public void onLostConnect() {
        Toast.makeText(this, getResources().getString(R.string.rs232_lost_connect),
                Toast.LENGTH_SHORT).show();
    }
}
