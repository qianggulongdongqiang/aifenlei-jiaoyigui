package com.arcfun.uhfclient.view;

import java.io.File;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.arcfun.uhfclient.R;
import com.arcfun.uhfclient.utils.Constancts;
import com.arcfun.uhfclient.utils.LogUtils;
import com.arcfun.uhfclient.utils.SharedPreferencesUtils;
import com.arcfun.uhfclient.utils.Utils;
import com.reader.helper.ReaderHelper;
import com.uhf.uhf.serialport.SerialPort;

public class BaseLoginFragment extends Fragment {
    private static final String TAG = "BaseLoginFragment";
    protected ReaderHelper mReaderHelper1, mReaderHelper2;
    protected SerialPort mSerialPort1, mSerialPort2;
    public static final int FRAGMENT_DEFAULT = 0;
    public static final int FRAGMENT_LOGIN_RFID_1 = FRAGMENT_DEFAULT;
    public static final int FRAGMENT_LOGIN_WEIXIN_1 = 10;
    public static final int FRAGMENT_LOGIN_PHONE_1 = 20;

    public static final int FRAGMENT_PACKAGE_IN = 50;
    public static final int FRAGMENT_PACKAGE_ENSURE = FRAGMENT_PACKAGE_IN + 1;
    public static final int FRAGMENT_BOTTLE_IN = 60;
    public static final int FRAGMENT_BOTTLE_ENSURE = FRAGMENT_BOTTLE_IN + 1;
    public static final int FRAGMENT_FINISH = 70;
    public static final int FRAGMENT_GUEST_FINISH = 80;
    public static final int FRAGMENT_EXIT = 100;

    public BaseLoginFragment() {
    }

    public interface OnActionCallBack {
        void onUpdate(int index, Parcelable value);
    }

    @Deprecated
    protected void initSerialPort1() {
        LogUtils.d(TAG, "initSerialPort1");
        try {
            mSerialPort1 = new SerialPort(new File(
                    SharedPreferencesUtils.getPort(getActivity(), 1)),
                    Utils.BAUD_RATE_DEF, 0);
            mReaderHelper1.setReader(mSerialPort1.getInputStream(),
                    mSerialPort1.getOutputStream(), Constancts.TYPE_DEV);
        } catch (Exception e) {
            Toast.makeText(getActivity(),
                    getString(R.string.rs232_error) + Utils.SERIAL_PROT_1,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Deprecated
    protected void initSerialPort2() {
        LogUtils.d(TAG, "initSerialPort2");
        try {
            mSerialPort2 = new SerialPort(new File(
                    SharedPreferencesUtils.getPort(getActivity(), 2)),
                    Utils.BAUD_RATE_UHF, 0);
            mReaderHelper2.setReader(mSerialPort2.getInputStream(),
                    mSerialPort2.getOutputStream(), Constancts.TYPE_EPC);
        } catch (Exception e) {
            Toast.makeText(getActivity(),
                    getString(R.string.rs232_error) + Utils.SERIAL_PROT_2,
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void closeSerialPort1() {
        LogUtils.d(TAG, "closeSerialPort1");
    }

    protected void closeSerialPort2() {
        LogUtils.d(TAG, "closeSerialPort2");
    }
}