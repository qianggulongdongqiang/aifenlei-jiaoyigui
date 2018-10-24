package com.arcfun.uhfclient.ui;

import java.io.File;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.arcfun.uhfclient.utils.Constancts;
import com.arcfun.uhfclient.utils.LogUtils;
import com.arcfun.uhfclient.utils.SharedPreferencesUtils;
import com.arcfun.uhfclient.utils.Utils;
import com.reader.helper.ReaderHelper;
import com.uhf.uhf.serialport.SerialPort;

public class AhsBaseActivity extends FragmentActivity implements
        OnClickListener {
    protected static final String TAG = "AhsBaseActivity";
    protected ReaderHelper mReaderHelper1, mReaderHelper2;
    protected SerialPort mSerialPort1, mSerialPort2;

    protected static final int MSG_UPDATE_DEBUG = 1;
    protected static final int MSG_SCROLL_MSG = 10;
    protected static final int MSG_SYNC_RFID_MSG = 11;

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
    }

    protected void initSerialPort1() {
        LogUtils.d(TAG, "initSerialPort1");
        try {
            mSerialPort1 = new SerialPort(new File(
                    SharedPreferencesUtils.getPort(this, 1)),
                    Utils.BAUD_RATE_DEF, 0);
            mReaderHelper1.setReader(mSerialPort1.getInputStream(),
                    mSerialPort1.getOutputStream(), Constancts.TYPE_DEV);
        } catch (Exception e) {
        }
    }

    protected void initSerialPort2() {
        LogUtils.d(TAG, "initSerialPort2");
        try {
            mSerialPort2 = new SerialPort(new File(
                    SharedPreferencesUtils.getPort(this, 2)),
                    Utils.BAUD_RATE_UHF, 0);
            mReaderHelper2.setReader(mSerialPort2.getInputStream(),
                    mSerialPort2.getOutputStream(), Constancts.TYPE_EPC);
        } catch (Exception e) {
        }
    }

    protected void closeSerialPort1() {
        LogUtils.d(TAG, "closeSerialPort1");
        mSerialPort1 = null;
    }

    protected void closeSerialPort2() {
        LogUtils.d(TAG, "closeSerialPort2");
        mSerialPort2 = null;
    }

    @Override
    public void onClick(View v) {
    }

}