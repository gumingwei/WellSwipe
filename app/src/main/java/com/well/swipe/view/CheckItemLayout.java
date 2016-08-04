package com.well.swipe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
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
public class CheckItemLayout extends RelativeLayout {

    private TextView mTitle;

    private CheckBox mCheckBox;

    public CheckItemLayout(Context context) {
        this(context, null);
    }

    public CheckItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitle = (TextView) findViewById(R.id.check_item_title);
        mCheckBox = (CheckBox) findViewById(R.id.check_item_check);
    }

    public void setTitle(int title) {
        setTitle(getResources().getString(title));
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setChecked(boolean check) {
        mCheckBox.setChecked(check);
    }

    public boolean isChecked() {
        return mCheckBox.isChecked();
    }
}
