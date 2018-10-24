package com.arcfun.uhfclient.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcfun.uhfclient.R;
import com.arcfun.uhfclient.data.OwnerInfo;
import com.arcfun.uhfclient.utils.Utils;

public class PopWelcomeDialog extends Dialog implements OnClickListener {
    private ImageView mBackView, mOkView, mCancelView;
    private TextView mName, mNumber;
    private OwnerInfo mInfo;
    private OnWelcomeListener mListener;

    public PopWelcomeDialog(Context context, OwnerInfo info) {
        this(context, info, R.style.PopDialog);
    }

    public PopWelcomeDialog(Context context, OwnerInfo info, int themeResId) {
        super(context, themeResId);
        this.mInfo = info;
        if (context instanceof OnWelcomeListener) {
            mListener = (OnWelcomeListener)context;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_welcome_layout);
        initView();
    }

    private void initView() {
        mName = (TextView) findViewById(R.id.welcome_name);
        mNumber = (TextView) findViewById(R.id.welcome_number);
        mBackView = (ImageView) findViewById(R.id.close);
        mCancelView = (ImageView) findViewById(R.id.welcome_cancel);
        mOkView = (ImageView) findViewById(R.id.welcome_ok);
        mBackView.setOnClickListener(this);
        mCancelView.setOnClickListener(this);
        mOkView.setOnClickListener(this);
        if (mInfo != null) {
            mName.setText(mInfo.getNickName());
            mNumber.setText("(" + Utils.formatPhoneNumber(mInfo.getMobile()) + ")");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.close:
            dismiss();
            break;
        case R.id.welcome_cancel:
            if (mListener != null) {
                mListener.onNext(0);
            }
            dismiss();
            break;
        case R.id.welcome_ok:
            if (mListener != null) {
                mListener.onSuccess(0, mInfo);
            }
            dismiss();
            break;
        default:
            break;
        }
    }

    public interface OnWelcomeListener{
        public void onSuccess(int type, OwnerInfo info);
        public void onNext(int type);
    }
}