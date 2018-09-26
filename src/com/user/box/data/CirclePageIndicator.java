package com.user.box.data;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.uhf.uhf.R;

public class CirclePageIndicator extends LinearLayout {
    private int mRoundSize, mRoundWidth;
    private int mRoundDefaultDrawable;
    private int mRoundSelectedDrawable;

    private ViewPager mViewPager = null;

    public CirclePageIndicator(Context context) {
        this(context, null);
    }

    public CirclePageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CirclePageIndicator, 0, 0);
        try {
            mRoundDefaultDrawable = a.getResourceId(
                    R.styleable.CirclePageIndicator_backgroud_unchecked,
                    R.drawable.ind_off);
            mRoundSelectedDrawable = a.getResourceId(
                    R.styleable.CirclePageIndicator_backgroud_checked,
                    R.drawable.ind_on);
            mRoundSize = a.getLayoutDimension(
                    R.styleable.CirclePageIndicator_round_size,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mRoundWidth = a.getLayoutDimension(
                    R.styleable.CirclePageIndicator_round_width, "round_width");
        } finally {
            a.recycle();
        }
        init();
    }

    public void setViewPager(final ViewPager viewPager) {
        if (viewPager.getAdapter() == null) {
            throw new IllegalStateException(
                    "ViewPager does not have adapter instance.");
        }

        mViewPager = viewPager;
        populateView();

        mViewPager
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float offset,
                            int offsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        updateRoundIndicator();
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

    }

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
    }

    private void populateView() {
        LayoutParams params = new LayoutParams(mRoundWidth, mRoundSize);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;

        for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
            ImageView roundIndicator = new ImageView(getContext());
            roundIndicator.setImageResource(mRoundDefaultDrawable);
            roundIndicator.setLayoutParams(params);
            addView(roundIndicator);
        }
        updateRoundIndicator();
    }

    private void updateRoundIndicator() {
        for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
            ImageView view = (ImageView)getChildAt(i);
            view.setImageResource(i == mViewPager.getCurrentItem() ?
                    mRoundSelectedDrawable : mRoundDefaultDrawable);
        }
    }

}