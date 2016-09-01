package com.well.swipecomm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.well.swipecomm.R;

/**
 * Created by mingwei on 3/22/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class GridLayoutItemView extends AngleItemCommon {

    private CheckBox mCheckBox;

    public GridLayoutItemView(Context context) {
        this(context, null);
    }

    public GridLayoutItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridLayoutItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCheckBox = (CheckBox) findViewById(R.id.appindex_item_check);
    }

    public void setChecked(boolean check) {
        mCheckBox.setChecked(check);
    }

    public CheckBox getCheckBox() {
        return mCheckBox;
    }
}
