package com.user.box;

import android.os.Bundle;

import com.uhf.uhf.R;

public class ExchangeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        bindView();
    }
}
