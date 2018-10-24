package com.arcfun.uhfclient.view;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.arcfun.uhfclient.R;
import com.arcfun.uhfclient.data.OwnerInfo;
import com.arcfun.uhfclient.utils.LogUtils;
import com.arcfun.uhfclient.utils.Utils;

public class RFIDListFragment extends BaseLoginFragment implements
        OnClickListener {
    private static final String TAG = "RFID|List";
    private OnActionCallBack mListener;
    private int mIndex;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;
    private TextView mAccount;
    private Button mOk, mBack;
    private List<OwnerInfo> mInfos;
    private int mOwnerId;
    private LayoutInflater mLayoutInflater;

    public RFIDListFragment() {
    }

    public RFIDListFragment(OnActionCallBack listener, List<OwnerInfo> infos,
            int index) {
        this.mListener = listener;
        this.mInfos = infos;
        this.mIndex = index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ahs_login_list_fragment,
                container, false);
        mLayoutInflater = inflater;
        mRadioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        mAccount = (TextView) view.findViewById(R.id.account_title);
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
                R.string.login_rfid_title2));
        mBack.setText(getString(R.string.login_resume));
        addView();
    }

    @SuppressLint("InflateParams")
    private void addView() {
        int length = (mInfos.size() > Utils.MAX_WELCOME_LIST) ?
                Utils.MAX_WELCOME_LIST : mInfos.size();
        for (int i = 0; i < length; i++) {
            mRadioButton = (RadioButton) mLayoutInflater.inflate(R.layout.ahs_login_item, null);
            mRadioButton.setId(mInfos.get(i).getId());
            mRadioButton.setText(mInfos.get(i).getNickName() + "(" + Utils.formatPhoneNumber(
                    mInfos.get(i).getMobile()) + ")");
            mRadioGroup.addView(mRadioButton, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        RadioButton radio = (RadioButton) mRadioGroup.getChildAt(0);
        radio.setChecked(true);
        mOwnerId = radio.getId();
        mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                LogUtils.d(TAG, "onChecked=" + checkedId);
                mOwnerId = checkedId;
            }
        });
    }

    private OwnerInfo getInfoFromId(int id) {
        for (OwnerInfo info : mInfos) {
            if (info.getId() == id) {
                return info;
            }
        }
        return null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.d(TAG, "onHiddenChanged hidden = " + hidden);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.account_login:
            if (mListener != null) {
                mListener.onUpdate(mIndex + 1, getInfoFromId(mOwnerId));
            }
            break;
        case R.id.account_switch:
            if (mListener != null) {
                mInfos = null;
                mListener.onUpdate(FRAGMENT_LOGIN_RFID_1, null);
            }
            break;

        default:
            break;
        }
    }

}