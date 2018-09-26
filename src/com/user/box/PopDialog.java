package com.user.box;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.uhf.uhf.R;
import com.user.box.data.MessageInfo;
import com.user.box.utils.Utils;

public class PopDialog extends Dialog implements OnClickListener {
    private ImageView mBackView, mCommitView;
    private Context mContext;
    private MessageInfo mMessageInfo;

    public PopDialog(Context context, MessageInfo info) {
        this(context, info, R.style.PopDialog);
    }

    public PopDialog(Context context, MessageInfo info, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        this.mMessageInfo = info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_weight_layout);
        mBackView = (ImageView) findViewById(R.id.close);
        mCommitView = (ImageView) findViewById(R.id.dialog_commit);
        mBackView.setOnClickListener(this);
        mCommitView.setOnClickListener(this);

        initView();
    }

    private void initView() {
        TextView name = (TextView) findViewById(R.id.dialog_name);
        TextView phone = (TextView) findViewById(R.id.dialog_phone);
        TextView owner = (TextView) findViewById(R.id.dialog_owner);
        TextView category = (TextView) findViewById(R.id.dialog_category);
        TextView weight = (TextView) findViewById(R.id.dialog_weight);
        TextView credit = (TextView) findViewById(R.id.dialog_credit_income);

        name.setText(mMessageInfo.getUserName());
        phone.setText(Utils.formatPhoneNumber(mMessageInfo.getPhoneNum()));
        owner.setText(mMessageInfo.getManagerId());
        category.setText(mMessageInfo.getCategory());
        credit.setText(String.valueOf(mMessageInfo.getIncome()));
        if (mMessageInfo.getType() == Utils.TRANS_PIECES) {
            weight.setText(mMessageInfo.getPiece());
        } else {
            weight.setText(mMessageInfo.getWeight());
        }
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