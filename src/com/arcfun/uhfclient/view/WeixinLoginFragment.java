package com.arcfun.uhfclient.view;

import android.graphics.Bitmap;
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
import com.arcfun.uhfclient.utils.Utils;

public class WeixinLoginFragment extends BaseLoginFragment implements
        OnClickListener {
    private static final String TAG = "Weixin|Login";
    private ImageView mBarcode;
    private OnActionCallBack mListener;
    private int mIndex;
    private ImageView mSwitchRFID;
    private TextView mSwitchPhone;

    public WeixinLoginFragment() {
    }

    public WeixinLoginFragment(OnActionCallBack listener, int index) {
        this.mListener = listener;
        this.mIndex = index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ahs_qrcode_fragment, container,
                false);
        mBarcode = (ImageView) view.findViewById(R.id.barcode);
        mSwitchRFID = (ImageView) view.findViewById(R.id.switch_rfid);
        mSwitchPhone = (TextView) view.findViewById(R.id.switch_qr_phone);
        mSwitchRFID.setOnClickListener(this);
        mSwitchPhone.setOnClickListener(this);
        Drawable right = getResources().getDrawable(R.drawable.icon_enter);
        int size = getResources().getDimensionPixelSize(R.dimen.enter_btn_size);
        right.setBounds(0, 0, size, size);
        mSwitchPhone.setCompoundDrawables(null, null, right, null);
        mBarcode.setVisibility(View.VISIBLE);
        String path = Utils.getQrFile();
        LogUtils.d(TAG, "onCreateView path = " + path);
        if (path != null && !path.isEmpty()) {
            Bitmap bitmap = Utils.obtainQRcode(path);
            if (bitmap != null) {
                mBarcode.setImageBitmap(bitmap);
            }
        }
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.d(TAG, "onHiddenChanged mIndex = " + mIndex);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.switch_rfid:
            if (mListener != null) {
                mListener.onUpdate(FRAGMENT_LOGIN_RFID_1, null);
            }
            break;
        case R.id.switch_qr_phone:
            if (mListener != null) {
                mListener.onUpdate(FRAGMENT_LOGIN_PHONE_1, null);
            }
            break;

        default:
            break;
        }
    }

}