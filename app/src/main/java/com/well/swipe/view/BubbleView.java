package com.well.swipe.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import com.well.swipe.R;
import com.well.swipe.utils.Utils;


/**
 * Created by mingwei on 1/12/16.
 */
public class BubbleView extends ImageView {

    private int mDefaultHeight = 100;

    private int mDefaultWidth = 115;

    private WindowManager.LayoutParams mParams;

    private WindowManager mManager;

    private float mX;

    private float mY;

    private float mStartX;

    private float mStartY;

    private int mStatusH;

    private int x;

    private int y;

    public BubbleView(Context context) {
        this(context, null);
    }

    public BubbleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));
        mStatusH = Utils.getStatusBarHeight(context);
        x = context.getResources().getDisplayMetrics().widthPixels - mDefaultWidth + 60;
        y = context.getResources().getDisplayMetrics().widthPixels + mStatusH - 150;
        initManager(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mX = e.getRawX();
        mY = e.getRawY() - mStatusH;
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = e.getX();
                mStartY = e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mParams.x = (int) (mX - mStartX);
                mParams.y = (int) (mY - mStartY);
                mManager.updateViewLayout(this, mParams);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                break;
        }
        return super.onTouchEvent(e);
    }


    private void initManager(int x, int y) {
        mParams = new WindowManager.LayoutParams();
        mManager = (WindowManager) getContext().getSystemService(getContext().WINDOW_SERVICE);
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mParams.format = PixelFormat.RGBA_8888;
//        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //mParams.flags = WindowManager.LayoutParams.TYPE_TOAST;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;

        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.x = x;
        mParams.y = y;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public boolean isManager() {
        return mManager != null;
    }

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
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP) {
            dismiss();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


}

