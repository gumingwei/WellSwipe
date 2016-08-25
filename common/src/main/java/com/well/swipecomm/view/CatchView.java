package com.well.swipecomm.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.well.swipecomm.R;
import com.well.swipecomm.utils.Utils;

/**
 * Created by mingwei on 3/8/16.
 * <p/>
 * <p/>
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 * <p/>
 * <p/>
 * 作为触发屏幕触摸事件的View
 */
public class CatchView extends PositionStateView {

    private Paint mPaint;

    private Rect mRect = new Rect();

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

    private int mTouchState = 2;

    public static final int TOUCH_STATE_REST = 0;

    public static final int TOUCH_STATE_SLIDE = 1;

    public static final int VELOCITY_2500 = 2500;

    public int mAngleSize;

    private WindowManager.LayoutParams mParams;

    private WindowManager mManager;

    private OnEdgeSlidingListener mListener;

    /**
     * 速度检测
     */
    private VelocityTracker mVelocityTracker;

    private int mMaximumVelocity, mMinmumVelocity;

    public interface OnEdgeSlidingListener extends OnScaleChangeListener {
        /**
         * 打开
         */
        void openLeft();

        void openRight();

        /**
         * true速度满足自动打开
         * false速度不满足根据抬手时的状态来判断是否打开
         *
         * @param view
         * @param flag
         */
        void cancel(View view, boolean flag);

    }

    public CatchView(Context context) {
        this(context, null);

    }

    public CatchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public CatchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(Color.TRANSPARENT);
        mDisplayWidth = context.getResources().getDisplayMetrics().widthPixels;
        mDisplayHeight = context.getResources().getDisplayMetrics().heightPixels + Utils.getStatusBarHeight(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mMinmumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        mAngleSize = getResources().getDimensionPixelSize(R.dimen.angleview_size);
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

    private boolean dispatchInnerChild(MotionEvent ev) {
        ev.setAction(MotionEvent.ACTION_CANCEL);
        MotionEvent newMotionEvent = MotionEvent.obtain(ev);
        dispatchTouchEvent(ev);
        newMotionEvent.setAction(MotionEvent.ACTION_DOWN);
        return dispatchTouchEvent(newMotionEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        initVeloCityTracker(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float newx = event.getX();
                float newy = event.getY();
                if (mPositionState == PositionState.POSITION_STATE_LEFT) {
                    if (newx - mLastX > mTouchSlop && Math.abs(newy - mLastY) > mTouchSlop && mTouchState != TOUCH_STATE_SLIDE) {
                        mTouchState = TOUCH_STATE_SLIDE;
                        mListener.openLeft();

                    }
                } else if (mPositionState == PositionState.POSITION_STATE_RIGHT) {
                    if (Math.abs(newx - mLastX) > mTouchSlop && Math.abs(newy - mLastY) > mTouchSlop && mTouchState != TOUCH_STATE_SLIDE) {
                        mTouchState = TOUCH_STATE_SLIDE;
                        mListener.openRight();

                    }
                }
                if (mTouchState == TOUCH_STATE_SLIDE) {
                    /**
                     * 根据手指的滑动回传一个百分比，用于计算scale，取较大的值
                     */
                    float p = (float) Math.sqrt(Math.pow((newx - mLastX), 2) + Math.pow((newy - mLastY), 2));
                    mListener.change(p < mAngleSize ? p / mAngleSize : ((p - mAngleSize) / 5) / mAngleSize + 1);
                }
                break;
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                float vx = mVelocityTracker.getXVelocity();
                float vy = mVelocityTracker.getYVelocity();
                if (vx > VELOCITY_2500 || vy < -VELOCITY_2500 || vx < -VELOCITY_2500) {
                    mListener.cancel(this, true);
                } else {
                    mListener.cancel(this, false);
                }
                recyleVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                mListener.cancel(this, false);
                recyleVelocityTracker();
                break;
        }
        return true;
    }

    /**
     * 设置颜色
     *
     * @param color
     */
    public void setColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    /**
     * 初始化
     *
     * @param x
     * @param y
     */
    private void initManager(int x, int y) {
        mParams = new WindowManager.LayoutParams();
        mManager = (WindowManager) getContext().getSystemService(getContext().WINDOW_SERVICE);
        mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        if (mPositionState == PositionState.POSITION_STATE_LEFT) {
            mParams.x = 0;
        } else if (mPositionState == PositionState.POSITION_STATE_RIGHT) {
            mParams.x = x;
        }

        mParams.y = y;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public void setState(int state, int left, int top, int width, int height) {
        mPositionState = state;
        initManager(mDisplayWidth, mDisplayHeight);
        initRect(left, top, width, height);
    }

    public int getState() {
        return mPositionState;
    }

    /**
     * 初始化范围
     */
    private void initRect(int left, int top, int width, int height) {
        mRectLeft = left;
        mRectTop = top;
        mRectRight = mWidth = width;
        mRectBottom = mHeight = height;
        mRect.set(mRectLeft, mRectTop, mRectRight, mRectBottom);
        invalidate();
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

    public void updata() {
        if (isManager()) {
            if (this.getParent() != null) {
                mManager.updateViewLayout(this, mParams);
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

    /**
     * 初始化VelocityTracker
     *
     * @param event
     */
    private void initVeloCityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 回收VelocityTracker
     */
    private void recyleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}
