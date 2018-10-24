package com.arcfun.uhfclient.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.arcfun.uhfclient.R;
import com.arcfun.uhfclient.data.OwnerInfo;
import com.arcfun.uhfclient.utils.LogUtils;
import com.arcfun.uhfclient.utils.Utils;

public class WeixinEnsureFragment extends BaseLoginFragment implements
        OnClickListener {
    private static final String TAG = "Weixin|Ensure";
    private OnActionCallBack mListener;
    private int mIndex;
    private TextView mAccount, mName, mNumber;
    private Button mOk, mBack;
    private OwnerInfo mInfo;

    public WeixinEnsureFragment() {
    }

    public void setInfo(OwnerInfo info) {
        this.mInfo = info;
    }

    public WeixinEnsureFragment(OnActionCallBack listener, OwnerInfo info,
            int index) {
        this.mListener = listener;
        this.mInfo = info;
        this.mIndex = index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ahs_login_ensure_fragment,
                container, false);
        mAccount = (TextView) view.findViewById(R.id.account_title);
        mName = (TextView) view.findViewById(R.id.account_name);
        mNumber = (TextView) view.findViewById(R.id.account_number);
        mOk = (Button) view.findViewById(R.id.account_login);
        mBack = (Button) view.findViewById(R.id.account_switch);
        mOk.setOnClickListener(this);
        mBack.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAccount.setText(getActivity().getResources().getString(
                R.string.login_qr_title2));
        if (mInfo != null) {
            mName.setText(mInfo.getNickName());
            mNumber.setText("(" + Utils.formatPhoneNumber(mInfo.getMobile())
                    + ")");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.d(TAG, "onHiddenChanged mIndex = " + mIndex);
        if (mInfo != null && !hidden) {
            mName.setText(mInfo.getNickName());
            mNumber.setText("(" + Utils.formatPhoneNumber(mInfo.getMobile())
                    + ")");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.account_login:
            if (mListener != null) {
                mListener.onUpdate(mIndex + 1, null);
            }
            break;
        case R.id.account_switch:
            if (mListener != null) {
                mInfo = null;
                mListener.onUpdate(FRAGMENT_LOGIN_WEIXIN_1, null);
            }
            break;

        default:
            break;
        }
    }

}