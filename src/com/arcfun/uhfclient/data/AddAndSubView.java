package com.arcfun.uhfclient.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arcfun.uhfclient.R;
import com.arcfun.uhfclient.utils.Utils;

public class AddAndSubView extends LinearLayout implements View.OnClickListener {

    public static final int TYPE_SUBTRACT = 0;// 减
    public static final int TYPE_ADD = 1;// 加
    private static final int DEFAULT_NUM = 1;// 默认num值

    private View mLayoutView;
    private ImageView mBtnAdd;// 加按钮
    private ImageView mBtnSub;// 减按钮
    private TextView mTvCount;// 数量显示
    private int mNum;
    private OnNumChangeListener mOnNumChangeListener;

    /**
     * 设置监听回调
     * 
     * @param onNumChangeListener
     */
    public void setOnNumChangeListener(OnNumChangeListener onNumChangeListener) {
        this.mOnNumChangeListener = onNumChangeListener;
    }

    public AddAndSubView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mLayoutView = LayoutInflater.from(context).inflate(
                R.layout.add_sub_view, this);

        initView();
        initData();
        setListener();
    }

    private void initView() {
        mBtnAdd = (ImageView) mLayoutView.findViewById(R.id.btn_add);
        mBtnSub = (ImageView) mLayoutView.findViewById(R.id.btn_sub);
        mTvCount = (TextView) mLayoutView.findViewById(R.id.tv_count);

        setPadding(1, 1, 1, 1);
    }

    private void initData() {
        setAddBtnImageResource(R.drawable.plus);
        setSubBtnImageResource(R.drawable.reduce);
        setNum(DEFAULT_NUM);// DEFAULT_NUM
    }

    private void setListener() {
        mBtnAdd.setOnClickListener(this);
        mBtnSub.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String countText = mTvCount.getText().toString();
        if (TextUtils.isEmpty(countText)) {
            mNum = DEFAULT_NUM;
            mTvCount.setText(String.valueOf(mNum));
            return;
        }
        switch (v.getId()) {
        case R.id.btn_add:// 加号
            mNum++;
            setNum(mNum);
            if (mOnNumChangeListener != null) {
                mOnNumChangeListener.onNumChange(mLayoutView, TYPE_ADD,
                        getNum());
            }
            break;
        case R.id.btn_sub:// 减号
            mNum--;
            setNum(mNum);
            if (mOnNumChangeListener != null) {
                mOnNumChangeListener.onNumChange(mLayoutView, TYPE_SUBTRACT,
                        getNum());
            }
            break;
        default:
            break;
        }
    }

    /**
     * 设置数量
     * 
     * @param num
     */
    public void setNum(int num) {
        this.mNum = num;
        mBtnSub.setEnabled(mNum > DEFAULT_NUM);
        mTvCount.setText(String.valueOf(mNum));
    }

    public void resetNum(int type) {
        setNum((type == Utils.TRANS_PIECES) ? DEFAULT_NUM : 0);
        if (mOnNumChangeListener != null) {
            mOnNumChangeListener.onNumChange(mLayoutView, TYPE_SUBTRACT,
                    getNum());
        }
    }

    /**
     * 获取值
     * @return
     */
    public int getNum() {
        String countText = mTvCount.getText().toString().trim();
        if (!TextUtils.isEmpty(countText)) {
            return Integer.parseInt(countText);
        } else {
            return DEFAULT_NUM;
        }
    }

    /**
     * 设置加号图片
     * 
     * @param addBtnDrawable
     */
    public void setAddBtnImageResource(int addBtnDrawable) {
        mBtnAdd.setImageResource(addBtnDrawable);
    }

    /**
     * 设置减法图片
     * 
     * @param subBtnDrawable
     */
    public void setSubBtnImageResource(int subBtnDrawable) {
        mBtnSub.setImageResource(subBtnDrawable);
    }

    /**
     * 设置加法减法的背景色
     * 
     * @param addBtnColor
     * @param subBtnColor
     */
    public void setButtonBgColor(int addBtnColor, int subBtnColor) {
        mBtnAdd.setBackgroundColor(addBtnColor);
        mBtnSub.setBackgroundColor(subBtnColor);
    }

    public interface OnNumChangeListener {
        /**
         * @param view
         * @param type 点击按钮的类型
         * @param num 返回的数值
         */
        void onNumChange(View view, int type, int num);
    }

}