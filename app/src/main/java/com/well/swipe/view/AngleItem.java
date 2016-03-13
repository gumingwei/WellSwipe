package com.well.swipe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by mingwei on 3/13/16.
 */
public class AngleItem extends RelativeLayout {

    public AngleItem(Context context) {
        super(context);
    }

    public AngleItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AngleItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        requestFocus();

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                //requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }
}
