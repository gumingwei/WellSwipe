package com.well.swipe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.well.swipe.utils.Utils;

/**
 * Created by mingwei on 3/8/16.
 * 作为触发屏幕触摸事件的View
 */
public class CatchView extends View {

    private Paint mPaint;

    private Rect mRect = new Rect();

    private int mPosition = POSITION_STATE_LEFT;

    public static final int POSITION_STATE_LEFT = 0;

    public static final int POSITION_STATE_RIGHT = 1;

    public int mTouchSlop;

    private int mDisplayWidth;

    private int mDisplayHeight;

    private int mRectLeft;

    private int mRectTop;

    private int mRectRight;

    private int mRectBottom;

    private int mWidth;

    private int mHeight;

    private float mLastX;

    private float mLastY;

    //private int mState;

    private int mTouchState = 2;

    public static final int TOUCH_STATE_REST = 0;

    public static final int TOUCH_STATE_SLIDE = 1;

    private WindowManager.LayoutParams mParams;

    private WindowManager mManager;

    private OnEdgeSlidingListener mListener;

    public interface OnEdgeSlidingListener {
        /**
         * 打开
         */
        void openLeft();

        void openRight();

        /**
         * 打开的百分比
         *
         * @param precent
         */
        void change(float precent);
    }

    public CatchView(Context context, int left, int top, int width, int height) {
        this(context, null);
        mRectLeft = left;
        mRectTop = top;
        mRectRight = mWidth = width;
        mRectBottom = mHeight = height;
    }

    public CatchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public CatchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mDisplayWidth = context.getResources().getDisplayMetrics().widthPixels;
        mDisplayHeight = context.getResources().getDisplayMetrics().heightPixels + Utils.getStatusBarHeight(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float newx = event.getX();
                float newy = event.getY();
                if (mPosition == POSITION_STATE_LEFT) {
                    if (newx - mLastX > mTouchSlop && Math.abs(newy - mLastY) > mTouchSlop) {
                        mTouchState = TOUCH_STATE_SLIDE;
                        mListener.openLeft();
                    }
                } else if (mPosition == POSITION_STATE_RIGHT) {
                    if (Math.abs(newx - mLastX) > mTouchSlop && Math.abs(newy - mLastY) > mTouchSlop) {
                        mTouchState = TOUCH_STATE_SLIDE;
                        mListener.openRight();
                    }
                }
                if (mTouchState == TOUCH_STATE_SLIDE) {
                    mListener.change(Math.abs(newx / mDisplayWidth));
                }
                break;
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                break;
        }
        return true;
    }

    private void initManager(int x, int y) {
        mParams = new WindowManager.LayoutParams();
        mManager = (WindowManager) getContext().getSystemService(getContext().WINDOW_SERVICE);
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        if (mPosition == POSITION_STATE_LEFT) {
            mParams.x = 0;
        } else if (mPosition == POSITION_STATE_RIGHT) {
            mParams.x = x;
        }

        mParams.y = y;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public void setState(int state) {
        mPosition = state;
        initManager(mDisplayWidth, mDisplayHeight);
        initRect();
        invalidate();

    }

    /**
     * 初始化范围
     */
    private void initRect() {

        mRect.set(mRectLeft, mRectTop, mRectRight, mRectBottom);
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

    public void setOnEdgeSlidingListener(OnEdgeSlidingListener listener) {
        mListener = listener;
    }
}
