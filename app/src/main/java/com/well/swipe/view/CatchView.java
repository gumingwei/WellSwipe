package com.well.swipe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.well.swipe.utils.Utils;

/**
 * Created by mingwei on 3/8/16.
 * 作为触发屏幕触摸事件的View
 */
public class CatchView extends View {

    private Paint mPaint;

    private int mDefaultWidth = 115;

    private Rect mRect = new Rect();

    private int mState = 0;

    public static final int STATE_LEFT = 0;

    public static final int STATE_RIGHT = 1;

    public static final int STATE_LEFT_RIGHT = 2;

    private int mPivotX;

    private int mPivotY;

    private int mRectRangeX;

    private int mRectRangeY;

    private int mRectWidth;

    private int mWidth;

    private int mHeight;

    private WindowManager.LayoutParams mParams;

    private WindowManager mManager;

    public CatchView(Context context, int width, int height) {
        this(context, null);
        mWidth = width;
        mHeight = height;
    }

    public CatchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public CatchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPivotX = context.getResources().getDisplayMetrics().widthPixels;
        mPivotY = context.getResources().getDisplayMetrics().heightPixels + Utils.getStatusBarHeight(context);
        //Log.i("Gmw", "w=" + mWidth + ",h=" + mHeight);

        //initRect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(mRect, mPaint);

    }

    private void initManager(int x, int y) {
        mParams = new WindowManager.LayoutParams();
        mManager = (WindowManager) getContext().getSystemService(getContext().WINDOW_SERVICE);
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        if (mState == STATE_LEFT) {
            mParams.x = 0;
        } else {
            mParams.x = x;
        }
        mParams.y = y;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public void setState(int state) {
        mState = state;
        mRectRangeX = mWidth;
        mRectRangeY = mHeight;
        initManager(mPivotX, mPivotY);
        initRect();
        invalidate();

    }

    private void initRect() {
        mRect.set(0, 0, mRectRangeX, mRectRangeY);
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
}
