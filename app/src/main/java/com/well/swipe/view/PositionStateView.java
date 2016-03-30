package com.well.swipe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mingwei on 3/29/16.
 */
public class PositionStateView extends View {

    public int mPositionState = PositionState.POSITION_STATE_LEFT;

    public PositionStateView(Context context) {
        super(context);
    }

    public PositionStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PositionStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPositionState(int state) {
        this.mPositionState = state;
        invalidate();
    }
}
