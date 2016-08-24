package com.well.swipe.preference;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.well.swipe.R;
import com.well.swipecomm.view.SwitchButton;

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
public class PreferenceCategory extends SwipePreference {

    private ImageView mIcon;

    private TextView mTitle;

    private SwitchButton mSwitchBtn;

    public PreferenceCategory(Context context) {
        this(context, null);
    }

    public PreferenceCategory(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIcon = (ImageView) findViewById(R.id.preference_icon);
        mTitle = (TextView) findViewById(R.id.preference_title);
        mSwitchBtn = (SwitchButton) findViewById(R.id.preference_switchbtn);
    }

    public void setTitle(int title) {
        setTitle(getContext().getString(title));
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public SwitchButton getSwitchBtn() {
        return mSwitchBtn;
    }

    public void setToggleVisiable(int visiable) {
        mSwitchBtn.setVisibility(visiable);
    }

    public void setIcon(Drawable drawable) {
        mIcon.setVisibility(VISIBLE);
        mIcon.setImageDrawable(drawable);
    }

}
