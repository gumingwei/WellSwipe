package com.well.swipe.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/9/16.
 */
public class SwipeLayout extends RelativeLayout {


    private WindowManager.LayoutParams mParams;

    private WindowManager mManager;

    private AngleLayout mAngleLayout;

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAngleLayout = (AngleLayout) findViewById(R.id.anglelayout);

        initManager(0, 0);
    }

    private void initManager(int x, int y) {
        mParams = new WindowManager.LayoutParams();
        mManager = (WindowManager) getContext().getSystemService(getContext().WINDOW_SERVICE);
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mParams.format = PixelFormat.RGBA_8888;
        //mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.x = x;
        mParams.y = y;
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    }

    public boolean isManager() {
        return mManager != null;
    }

    public void setR() {
        mAngleLayout.setPositionRight();
    }

    public void setL() {
        mAngleLayout.setPositionLeft();
    }

//    public void setPositionLeft() {
//        mAngleLayout.setPositionLeft();
//    }
//
//    public void setPositionRight() {
//        mAngleLayout.setPositionRight();
//    }

    public void show() {
        if (isManager()) {
            if (this.getParent() == null) {
                mManager.addView(this, mParams);
            }
        }
    }

    public void dismiss() {
        if (isManager()) {
            if (this.getParent() != null) {
                mManager.removeView(this);
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            dismiss();
        }
        return super.dispatchKeyEvent(event);
    }

}
