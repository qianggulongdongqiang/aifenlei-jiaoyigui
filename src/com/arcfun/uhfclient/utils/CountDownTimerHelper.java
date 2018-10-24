package com.arcfun.uhfclient.utils;

import android.os.CountDownTimer;
import android.widget.Button;

public class CountDownTimerHelper {
    public interface OnFinishListener {
        public void onFinish();
    }

    private Button mView;
    private CountDownTimer mTimer;
    private OnFinishListener listener;
    private int mFinishRes = -1;

    public CountDownTimerHelper(Button view, long millisInFuture,
            long countDownInterval, int resId) {
        this.mView = view;
        this.mFinishRes = resId;
        mTimer = new CountDownTimer(millisInFuture, countDownInterval) {

            @Override
            public void onTick(long time) {
                mView.setText(String.valueOf(time / 1000));
            }

            @Override
            public void onFinish() {
                if (mFinishRes > 0) {
                    mView.setText(mFinishRes);
                }
                if (listener != null) {
                    listener.onFinish();
                }
            }
        };
    }

    public CountDownTimerHelper(Button view, long millisInFuture,
            long countDownInterval) {
        this.mView = view;
        mTimer = new CountDownTimer(millisInFuture, countDownInterval) {

            @Override
            public void onTick(long time) {
                mView.setText(String.valueOf(time / 1000));
            }

            @Override
            public void onFinish() {
                if (mFinishRes > 0) {
                    mView.setText(mFinishRes);
                }
                if (listener != null) {
                    listener.onFinish();
                }
            }
        };
    }

    public void start() {
        mTimer.start();
    }

    public void stop() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mFinishRes > 0) {
            mView.setText(mFinishRes);
        }
    }

    public void setOnFinishListener(OnFinishListener listener) {
        this.listener = listener;
    }
}