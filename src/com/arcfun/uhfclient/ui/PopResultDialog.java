package com.arcfun.uhfclient.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcfun.uhfclient.R;
import com.arcfun.uhfclient.data.ResultDataInfo;
import com.arcfun.uhfclient.utils.Utils;

public class PopResultDialog extends Dialog implements OnClickListener {
    private ImageView mBackView, mCommitView, mResultBack, mResultNext;
    private ResultDataInfo mInfo;
    private Context mContext;
    private OnResultListener mListener;

    public PopResultDialog(Context context, ResultDataInfo info) {
        this(context, info, R.style.PopDialog);
    }

    public PopResultDialog(Context context, ResultDataInfo info, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        this.mInfo = info;
        if (context instanceof OnResultListener) {
            mListener = (OnResultListener) context;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_result_layout);
        mBackView = (ImageView) findViewById(R.id.close);
        mCommitView = (ImageView) findViewById(R.id.commit_back);
        mResultBack = (ImageView) findViewById(R.id.result_back);
        mResultNext = (ImageView) findViewById(R.id.result_continue);
        mBackView.setOnClickListener(this);
        mCommitView.setOnClickListener(this);
        mResultBack.setOnClickListener(this);
        mResultNext.setOnClickListener(this);

        initView();
    }

    private void initView() {
        TextView time = (TextView) findViewById(R.id.dialog_time);
        TextView account = (TextView) findViewById(R.id.dialog_account);
        TextView owner = (TextView) findViewById(R.id.dialog_owner);
        TextView main0 = (TextView) findViewById(R.id.dialog_result_main0);
        TextView maint1 = (TextView) findViewById(R.id.dialog_result_maint1);
        TextView main1 = (TextView) findViewById(R.id.dialog_result_main1);
        TextView maint2 = (TextView) findViewById(R.id.dialog_result_maint2);
        TextView main2 = (TextView) findViewById(R.id.dialog_result_main2);
        TextView maint3 = (TextView) findViewById(R.id.dialog_result_maint3);
        TextView main3 = (TextView) findViewById(R.id.dialog_result_main3);

        time.setText(Utils.timeFormat(mInfo.getTimestamp()));
        account.setText(mInfo.getAccount() + "(" +
                Utils.formatPhoneNumber(mInfo.getPhone()) +")");
        owner.setText(mInfo.getManager());
        if (mInfo.getCode() == 1) {
            main0.setText(mContext.getResources().getString(R.string.dialog_success_welcome));
            maint1.setText(mContext.getResources().getString(R.string.dialog_success_before));
            maint2.setText(mContext.getResources().getString(R.string.dialog_success_income));
            maint3.setText(mContext.getResources().getString(R.string.dialog_success_after));

            main1.setText(mInfo.getSuccessBefore());
            main2.setText(mInfo.getSuccessIncome());
            main3.setText(mInfo.getSuccessAfter());
            mResultBack.setImageDrawable(mContext.getResources().getDrawable(R.drawable.success_back));
            mResultNext.setImageDrawable(mContext.getResources().getDrawable(R.drawable.success_continue));
        } else {
            main0.setText(mContext.getResources().getString(R.string.dialog_fail_welcome));
            maint1.setText(mContext.getResources().getString(R.string.dialog_category));
            maint2.setText(mContext.getResources().getString(
                    (mInfo.getType() == Utils.TRANS_WEIGHT) ?
                    R.string.dialog_weight : R.string.dialog_pieces));
            maint3.setText(mContext.getResources().getString(R.string.dialog_fail_credit));

            main1.setText(mInfo.getFailCategory());
            main2.setText((mInfo.getType() == Utils.TRANS_WEIGHT) ?
                    mInfo.getFailWeight() : String.valueOf(mInfo.getFailPiece()));
            main3.setText(String.valueOf(mInfo.getFailCredit()));
            mResultBack.setImageDrawable(mContext.getResources().getDrawable(R.drawable.fail_back));
            mResultNext.setImageDrawable(mContext.getResources().getDrawable(R.drawable.fail_continue));
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
        case R.id.close:
            break;
        case R.id.commit_back:
            if (mListener != null) {
                mListener.onSuccess(mInfo.getCode());
            }
            break;
        case R.id.result_back:
            if (mListener != null) {
                mListener.onBack(mInfo.getCode());
            }
            break;
        case R.id.result_continue:
            if (mListener != null) {
                mListener.onNext(mInfo.getCode());
            }
            break;
        default:
            break;
        }

    }

    public interface OnResultListener {
        public void onSuccess(int type);
        public void onBack(int type);
        public void onNext(int type);
    }
}