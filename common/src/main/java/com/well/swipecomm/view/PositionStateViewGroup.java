package com.well.swipecomm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by mingwei on 3/29/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class PositionStateViewGroup extends ViewGroup {

    private int mPositionState = PositionState.POSITION_STATE_LEFT;

    public PositionStateViewGroup(Context context) {
        super(context);
    }

    public PositionStateViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PositionStateViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    public void setPositionState(int state) {
        this.mPositionState = state;
        requestLayout();
    }

    public int getPositionState() {
        return mPositionState;
    }

    public boolean isLeft() {
        return mPositionState == PositionState.POSITION_STATE_LEFT;
    }

    public boolean isRight() {
        return mPositionState == PositionState.POSITION_STATE_RIGHT;
    }
}
