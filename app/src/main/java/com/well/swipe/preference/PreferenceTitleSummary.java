package com.well.swipe.preference;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/30/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class PreferenceTitleSummary extends SwipePreference {

    private TextView mTitle;

    private TextView mSummary;

    private ImageView mIcon;

    private ImageView mArrow;

    private String mSummaryArray[] = new String[]{};

    public PreferenceTitleSummary(Context context) {
        this(context, null);
    }

    public PreferenceTitleSummary(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreferenceTitleSummary(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitle = (TextView) findViewById(R.id.preference_title);
        mSummary = (TextView) findViewById(R.id.preference_summary);
        mIcon = (ImageView) findViewById(R.id.preference_icon);
        mArrow = (ImageView) findViewById(R.id.preference_arraw);
    }

    public void setTitle(int title) {
        setTitle(getContext().getString(title));
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setSummary(int summary) {
        setSummary(getContext().getString(summary));
    }

    public void setSummaryArray(String array[]) {
        mSummaryArray = array;
    }

    public void setSummary(String summary) {
        mSummary.setText(summary);
    }

    public String[] getSummaryArray() {
        return mSummaryArray;
    }

    public void refreshSummary() {
        mSummary.setText(mSummaryArray[getIntValue()]);
    }

    public void refreshSummary(int def) {
        mSummary.setText(mSummaryArray[getIntValue(def)]);
    }

    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        if (clickable) {
            mTitle.setTextColor(getResources().getColor(R.color.text_white));
            mSummary.setTextColor(getResources().getColor(R.color.text_gray));
        } else {
            mTitle.setTextColor(getResources().getColor(R.color.text_white_unclick));
            mSummary.setTextColor(getResources().getColor(R.color.text_gray_unclick));
        }
    }

    public void setIcon(Drawable drawable) {
        mIcon.setVisibility(VISIBLE);
        mIcon.setImageDrawable(drawable);
    }

    public void showArrow() {
        mArrow.setVisibility(VISIBLE);
    }

}
