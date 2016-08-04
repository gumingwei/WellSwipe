package com.well.swipe.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.well.swipe.R;


/**
 * Created by mingwei on 6/21/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public abstract class BaseSettingActivity extends AppCompatActivity {

    private LinearLayout mBaseLayout;

    private FrameLayout mContentLayout;

    private View mBack;

    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.base_setting_layout, null);
        mContentLayout = (FrameLayout) mBaseLayout.findViewById(R.id.base_content);
        mBack = mBaseLayout.findViewById(R.id.base_titlebar_back);
        mTitle = (TextView) mBaseLayout.findViewById(R.id.base_titlebar_title);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftBtnClick();
            }
        });
    }

    public void setContentView(int resId) {
        View view = LayoutInflater.from(this).inflate(resId, null);
        mContentLayout.removeAllViews();
        mContentLayout.addView(view);
        setContentView(mBaseLayout);
    }

    public void leftBtnClick() {
        finish();
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setTitle(int titleId) {
        mTitle.setText(getResources().getString(titleId));
    }

    public void isBack(boolean bool) {
        if (bool) {
            mBack.setVisibility(View.VISIBLE);
        } else {
            mBack.setVisibility(View.INVISIBLE);
        }
    }

}
