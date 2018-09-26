package com.user.box;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.uhf.uhf.R;

public class BaseActivity extends Activity implements OnClickListener {
    protected static final String TAG = "zxz";
    private TextView mHomeBtn, mExchangeBtn, mTransactionbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void bindView() {
        mHomeBtn = (TextView) findViewById(R.id.main_btn);
        mTransactionbtn = (TextView) findViewById(R.id.transaction_btn);
        mExchangeBtn = (TextView) findViewById(R.id.exchange_btn);
        mHomeBtn.setOnClickListener(this);
        mTransactionbtn.setOnClickListener(this);
        mExchangeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isCurrentPage(v)) return;
        switch (v.getId()) {
        case R.id.main_btn:
            startActivity(new Intent(this, HomeListActivity.class));
            break;
        case R.id.transaction_btn:
            startActivity(new Intent(this, TransactionActivity.class));
            break;
        case R.id.exchange_btn:
            startActivity(new Intent(this, ExchangeActivity.class));
            break;

        default:
            break;
        }
        overridePendingTransition(R.anim.anim_enter,R.anim.anim_exit);
    }

    private boolean isCurrentPage(View v) {
        switch (v.getId()) {
        case R.id.main_btn:
            if (getClass().equals(HomeListActivity.class)) return true;
            break;
        case R.id.transaction_btn:
            if (getClass().equals(TransactionActivity.class)) return true;
            break;
        case R.id.exchange_btn:
            if (getClass().equals(ExchangeActivity.class)) return true;
            break;
        default:
            return false;
        }
        return false;
    }
}