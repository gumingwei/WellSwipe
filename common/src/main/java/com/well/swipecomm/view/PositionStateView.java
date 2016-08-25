package com.well.swipecomm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

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
public class PositionStateView extends View {

    public int mPositionState = PositionState.POSITION_STATE_LEFT;

    public PositionStateView(Context context) {
        this(context, null);
    }

    public PositionStateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PositionStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPositionState(int state) {
        this.mPositionState = state;
        invalidate();
    }

    public boolean isLeft() {
        return mPositionState == PositionState.POSITION_STATE_LEFT;
    }

    public boolean isRight() {
        return mPositionState == PositionState.POSITION_STATE_RIGHT;
    }
}
