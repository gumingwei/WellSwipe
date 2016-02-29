package com.well.swipe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;


import com.well.swipe.R;
import com.well.swipe.utils.Utils;

/**
 * Created by mingwei on 2/26/16.
 */
public class DragLayer extends FrameLayout {

    /**
     * 旋转View
     */
    private FanMum mFanMum;
    /**
     * 当前的旋转状态
     */
    private int mTouchState = TOUCH_STATE_REST;
    /**
     * 停滞状态
     */
    private static final int TOUCH_STATE_REST = 0;
    /**
     * 旋转中
     */
    private static final int TOUCH_STATE_WHIRLING = 1;
    /**
     * 转向上一个
     */
    private static final int TOUCH_STATE_PRE = 2;
    /**
     * 转向下一个
     */
    private static final int TOUCH_STATE_NEXT = 3;


    private float mDownMotionX;

    private float mLastMotionX;

    private float mLastMotionY;

    private int mActivePointId;
    /**
     * 最小移动距离
     */
    private int mTouchSlop;

    private VelocityTracker mVelocityTracker;

    private int mMaximumVelocity, mMinmumVelocity;

    private static final int ALLOW_AUTO = 3000;
    /**
     * 容器的宽高
     */
    private int mWidth;
    private int mHeight;

    private int mFanMumOffset = 25;

    public DragLayer(Context context) {
        this(context, null);
    }

    public DragLayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewConfiguration mConfig = ViewConfiguration.get(context);
        mTouchSlop = mConfig.getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mMinmumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mFanMum = (FanMum) findViewById(R.id.fanmum);
        //mFanMum.setRotationY(180);
        //mFanMum.setTranslationX(mFanMumOffset);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        int offset = Utils.dp2px(getContext(), mFanMumOffset);
        int fanSize = mWidth - offset;
        LayoutParams params = new LayoutParams(fanSize, fanSize);
        mFanMum.setLayoutParams(params);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int offset = Utils.dp2px(getContext(), mFanMumOffset);
        int fanSize = mWidth - offset;
        if (mFanMum.POSITION == FanMum.LEFT) {
            mFanMum.layout(0, mHeight - fanSize, fanSize, mHeight);
        } else if (mFanMum.POSITION == FanMum.RIGHT) {
            mFanMum.layout(offset, mHeight - fanSize, mWidth, mHeight);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (getChildCount() <= 0) {
            return super.onTouchEvent(event);
        }

        initVeloCityTracker(event);
        final int action = event.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                mDownMotionX = event.getX();
                mLastMotionX = event.getX();
                mLastMotionY = event.getY();
                mActivePointId = event.getPointerId(0);
                if (mTouchState == TOUCH_STATE_WHIRLING) {
                    //正在滚动的时候
                }
                if (mFanMum.POSITION == FanMum.LEFT) {
                    mFanMum.downAngle(mLastMotionX, mHeight - mLastMotionY);
                } else {
                    mFanMum.downAngle(mWidth - mLastMotionX, mHeight - mLastMotionY);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                float newX = event.getX();
                float newY = event.getY();
                float diffX = newX - mLastMotionX;
                float diffY = newY - mLastMotionY;
                if (Math.abs(diffX) > mTouchSlop || Math.abs(diffY) > mTouchSlop) {
                    mTouchState = TOUCH_STATE_WHIRLING;
                }

                if (mTouchState == TOUCH_STATE_WHIRLING) {
                    if (mFanMum.POSITION == FanMum.LEFT) {
                        mFanMum.changeAngle(newX, mHeight - newY);
                    } else {
                        mFanMum.changeAngle(mWidth - newX, mHeight - newY);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                mFanMum.upAngle();
                mTouchState = TOUCH_STATE_REST;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                float vy = mVelocityTracker.getYVelocity();
                float vx = mVelocityTracker.getXVelocity();
                if (mFanMum.POSITION == FanMum.LEFT) {
                    if (vy > ALLOW_AUTO || vx > ALLOW_AUTO) {
                        mFanMum.flingAlong();
                    } else if (vx < -ALLOW_AUTO || vy < -ALLOW_AUTO) {
                        mFanMum.flingInvrse();
                    }
                } else {
                    if (vx > ALLOW_AUTO || vy < -ALLOW_AUTO) {
                        mFanMum.flingAlong();
                    } else if (vx < -ALLOW_AUTO || vy > ALLOW_AUTO) {
                        mFanMum.flingInvrse();
                    }
                }
                recyleVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                recyleVelocityTracker();
                break;
        }

        return true;
    }

    private void initVeloCityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void recyleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}
