package com.well.swipe.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by mingwei on 3/19/16.
 */
public class SwipeWindowManager {

    public Context mContext;

    private WindowManager mManager;

    private WindowManager.LayoutParams mParams;

    private int mX;

    private int mY;

    public SwipeWindowManager(int x, int y, Context context) {
        mParams = new WindowManager.LayoutParams();
        mManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
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

    public void show(View view) {
        if (isManager()) {
            if (view.getParent() == null) {
                mManager.addView(view, mParams);
            }
        }
    }

    public boolean hasView(View view) {
        if (isManager()) {
            return view.getParent() != null;
        }
        return false;
    }

    public void hide(View view) {
        if (isManager()) {
            if (view.getParent() != null) {
                mManager.removeView(view);
            }
        }
    }

    public void setParams(int x, int y) {
        mParams.x = x;
        mParams.y = y;
    }
}
