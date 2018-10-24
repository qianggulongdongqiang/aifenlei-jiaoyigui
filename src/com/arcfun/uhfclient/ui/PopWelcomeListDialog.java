package com.arcfun.uhfclient.ui;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.arcfun.uhfclient.R;
import com.arcfun.uhfclient.data.OwnerInfo;
import com.arcfun.uhfclient.ui.PopWelcomeDialog.OnWelcomeListener;
import com.arcfun.uhfclient.utils.LogUtils;
import com.arcfun.uhfclient.utils.Utils;

public class PopWelcomeListDialog extends Dialog implements OnClickListener {
    private static final String TAG = "PopWelcomeListDialog";
    private ImageView mBackView, mOkView, mCancelView;
    private RadioGroup mRadioGroup;
    private List<OwnerInfo> mInfos;
    private OnWelcomeListener mListener;
    private Context mContext;
    private int mOwnerId;

    private float mPopListDimen;
    private int mPopListColor;

    public PopWelcomeListDialog(Context context, List<OwnerInfo> infos) {
        this(context, infos, R.style.PopDialog);
    }

    public PopWelcomeListDialog(Context context, List<OwnerInfo> infos, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        mPopListDimen = context.getResources().getDimension(R.dimen.dialog_welcome_title);
        mPopListColor = context.getResources().getColor(R.color.dialog_black);
        this.mInfos = infos;
        if (context instanceof OnWelcomeListener) {
            mListener = (OnWelcomeListener)context;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_welcome_list_layout);
        initView();
    }

    private void initView() {
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mBackView = (ImageView) findViewById(R.id.close);
        mCancelView = (ImageView) findViewById(R.id.welcome_list_cancel);
        mOkView = (ImageView) findViewById(R.id.welcome_list_ok);
        mBackView.setOnClickListener(this);
        mCancelView.setOnClickListener(this);
        mOkView.setOnClickListener(this);

        addView();
    }

    private void addView() {
        int length = (mInfos.size() > Utils.MAX_WELCOME_LIST) ?
                Utils.MAX_WELCOME_LIST : mInfos.size();
        for (int i = 0; i < length; i++) {
            RadioButton radioBtn = new RadioButton(mContext);
            radioBtn.setId(mInfos.get(i).getId());
            radioBtn.setButtonDrawable(R.drawable.radio_group_selector);
            radioBtn.setPadding(20, 20, 20, 20);
            radioBtn.setTextSize(mPopListDimen);
            radioBtn.setTextColor(mPopListColor);
            radioBtn.setText(mInfos.get(i).getNickName() + "(" + Utils.formatPhoneNumber(
                    mInfos.get(i).getMobile()) + ")");
            mRadioGroup.addView(radioBtn, LinearLayout.LayoutParams.MATCH_PARENT,
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
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.close:
            dismiss();
            break;
        case R.id.welcome_list_cancel:
            if (mListener != null) {
                mListener.onNext(1);
            }
            dismiss();
            break;
        case R.id.welcome_list_ok:
            LogUtils.d(TAG, "ok id=" + mOwnerId);
            if (mListener != null) {
                mListener.onSuccess(1, getInfoFromId(mOwnerId));
            }
            dismiss();
            break;
        default:
            break;
        }
    }
}