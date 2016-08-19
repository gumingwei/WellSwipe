package com.well.swipe.preference;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/31/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class PreferenceTitle extends SwipePreference {

    private TextView mTitle;

    private ImageView mIcon;

    private ImageView mArrow;

    public PreferenceTitle(Context context) {
        this(context, null);
    }

    public PreferenceTitle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreferenceTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitle = (TextView) findViewById(R.id.preference_title);
        mIcon = (ImageView) findViewById(R.id.preference_icon);
        mArrow = (ImageView) findViewById(R.id.preference_arraw);
    }

    public void setTitle(int title) {
        setTitle(getResources().getString(title));
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setIcon(Drawable drawable) {
        mIcon.setVisibility(VISIBLE);
        mIcon.setImageDrawable(drawable);
    }

    public void showArrow() {
        mArrow.setVisibility(VISIBLE);
    }

}
