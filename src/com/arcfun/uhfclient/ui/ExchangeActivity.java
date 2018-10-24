package com.arcfun.uhfclient.ui;

import android.os.Bundle;

import com.arcfun.uhfclient.R;

public class ExchangeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        bindView();
    }
}