package com.user.box;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.uhf.uhf.R;

public class PopListDialog extends Dialog implements OnClickListener {
    private ImageView mBackView, mCommitView;
    private Context mContext;

    public PopListDialog(Context context) {
        this(context, R.style.PopDialog);
    }

    public PopListDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_exchange_layout);
        mBackView = (ImageView) findViewById(R.id.close);
        mCommitView = (ImageView) findViewById(R.id.dialog_commit);
        mBackView.setOnClickListener(this);
        mCommitView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.close:
            dismiss();
            break;
        case R.id.dialog_commit:
            dismiss();
            break;
        default:
            break;
        }

    }
}