package com.arcfun.uhfclient.view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcfun.uhfclient.R;
import com.arcfun.uhfclient.utils.LogUtils;

public class RFIDLoginFragment extends BaseLoginFragment implements
        OnClickListener {
    private static final String TAG = "RFID|Login";
    private OnActionCallBack mListener;
    private int mIndex;
    private ImageView mSwitchQr;
    private TextView mSwitchPhone;

    public RFIDLoginFragment() {
    }

    public RFIDLoginFragment(OnActionCallBack listener, int index) {
        this.mListener = listener;
        this.mIndex = index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ahs_rfid_fragment, container,
                false);
        mSwitchQr = (ImageView) view.findViewById(R.id.switch_qr);
        mSwitchPhone = (TextView) view.findViewById(R.id.switch_rfid_phone);
        mSwitchQr.setOnClickListener(this);
        mSwitchPhone.setOnClickListener(this);
        Drawable right = getResources().getDrawable(R.drawable.icon_enter);
        int size = getResources().getDimensionPixelSize(R.dimen.enter_btn_size);
        right.setBounds(0, 0, size, size);
        mSwitchPhone.setCompoundDrawables(null, null, right, null);
        LogUtils.d(TAG, "onCreateView " + mIndex);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.d(TAG, "onActivityCreated " + mIndex);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.d(TAG, "onHiddenChanged hidden = " + hidden);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.switch_qr:
            if (mListener != null) {
                mListener.onUpdate(FRAGMENT_LOGIN_WEIXIN_1, null);
            }
            break;
        case R.id.switch_rfid_phone:
            if (mListener != null) {
                mListener.onUpdate(FRAGMENT_LOGIN_PHONE_1, null);
            }
            break;

        default:
            break;
        }
    }

}