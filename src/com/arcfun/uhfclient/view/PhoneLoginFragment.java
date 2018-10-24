package com.arcfun.uhfclient.view;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arcfun.uhfclient.R;
import com.arcfun.uhfclient.data.OwnerInfo;
import com.arcfun.uhfclient.net.HttpRequest;
import com.arcfun.uhfclient.utils.LogUtils;
import com.arcfun.uhfclient.utils.Utils;
import com.arcfun.uhfclient.view.NumKeyBoard.OnInputDoneListener;

public class PhoneLoginFragment extends BaseLoginFragment implements
        OnClickListener, OnInputDoneListener {
    private static final String TAG = "Phone|Login";
    private OnActionCallBack mListener;
    private int mIndex;
    private EditText mInputNumber;
    private TextView mTips, mSwitchText;
    private LinearLayout mSwitchOther;
    private NumKeyBoard mKeyBoardView;
    private OwnerInfo ownerInfo = null;
    private AsyncTask<String, Void, String> mTask = null;
    private String number;

    public PhoneLoginFragment() {
    }

    public PhoneLoginFragment(OnActionCallBack listener, int index) {
        this.mListener = listener;
        this.mIndex = index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ahs_phone_fragment, container,
                false);
        mInputNumber = (EditText) view.findViewById(R.id.input);
        mTips = (TextView) view.findViewById(R.id.input_tips);
        mSwitchOther = (LinearLayout) view.findViewById(R.id.switch_rfid_phone);
        mSwitchText = (TextView) view.findViewById(R.id.txt_rfid_phone);
        mInputNumber.setInputType(InputType.TYPE_NULL);
        mKeyBoardView = (NumKeyBoard) view.findViewById(R.id.input_keyboard);
        mKeyBoardView.setEditText(mInputNumber, this);
        mSwitchOther.setOnClickListener(this);
        Drawable right = getResources().getDrawable(R.drawable.icon_enter);
        int size = getResources().getDimensionPixelSize(R.dimen.enter_btn_size);
        right.setBounds(0, 0, size, size);
        mSwitchText.setCompoundDrawables(null, null, right, null);
        LogUtils.d(TAG, "onCreateView " + mIndex);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.d(TAG, "onActivityCreated mIndex = " + mIndex);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mTask != null) {
            mTask.cancel(true);
        }
        mInputNumber.setText("");//clear
        mTips.setVisibility(View.INVISIBLE);
        LogUtils.d(TAG, "onHiddenChanged index = " + mIndex + "," + hidden);
    }

    private void requestLogin(final String json) {
        String url = HttpRequest.URL_HEAD + HttpRequest.MACHINE_LOGIN;
        if (mTask != null) {
            mTask.cancel(true);
        }
        mTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                if (data == null) {
                    return null;
                }
                String result = new String(data);
                LogUtils.d(TAG, "requestLogin:" + result);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    ownerInfo = Utils.parseLoginCode(result);
                    if (ownerInfo != null) {
                        if (mListener != null) {
                            mListener.onUpdate(mIndex + 1, ownerInfo);
                        }
                        mInputNumber.setText("");//clear
                        return;
                    }
                }
                mTips.setVisibility(View.VISIBLE);
            }
        }.execute(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.switch_rfid_phone:
            if (mListener != null) {
                mListener.onUpdate(FRAGMENT_DEFAULT, null);
            }
            break;

        default:
            break;
        }
    }

    @Override
    public void onInput(int type) {
        switch (type) {
        case -1:
            if (mTask != null) {
                mTask.cancel(true);
            }
            mTips.setVisibility(View.VISIBLE);
            break;
        case 0:
            if (mTask != null) {
                mTask.cancel(true);
            }
            mTips.setVisibility(View.INVISIBLE);
            break;
        case 1:
            number = mInputNumber.getText().toString();
            LogUtils.d(TAG, "onInput " + number);
            requestLogin(Utils.buildLoginJson(number, true));
            break;

        default:
            break;
        }
    }

}