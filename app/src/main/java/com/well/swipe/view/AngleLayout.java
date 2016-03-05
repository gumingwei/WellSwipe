package com.well.swipe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.well.swipe.R;
import com.well.swipe.utils.Utils;

/**
 * Created by mingwei on 2/26/16.
 */
public class AngleLayout extends FrameLayout implements AngleView.OnAngleChangeListener {
    /**
     * 旋转View
     */
    private AngleView mAngleView;

    private IndicatorView mIndicator;
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

    private static final int TOUCH_STATE_AUTO = 4;

    private boolean isAllowAngle = true;


    private float mDownMotionX;

    private float mLastMotionX;

    private float mLastMotionY;

    private int mActivePointId;
    /**
     * 最小移动距离
     */
    private int mTouchSlop;
    /**
     * 速度检测
     */
    private VelocityTracker mVelocityTracker;

    private int mMaximumVelocity, mMinmumVelocity;
    /**
     * 容器的宽高
     */
    private int mWidth;
    private int mHeight;

    private int mFanMumOffset = 25;

    public AngleLayout(Context context) {
        this(context, null);
    }

    public AngleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AngleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewConfiguration mConfig = ViewConfiguration.get(context);
        mTouchSlop = mConfig.getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mMinmumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAngleView = (AngleView) findViewById(R.id.angleview);
        mAngleView.setOnAngleChangeListener(this);
        mIndicator = (IndicatorView) findViewById(R.id.indicator);
        mIndicator.setCurrent(0);

        //setRotationY();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        /**
         * AngleView的大小
         */
        int offset = Utils.dp2px(getContext(), mFanMumOffset);
        int fanSize = mWidth - offset;
        LayoutParams params = new LayoutParams(fanSize, fanSize);
        mAngleView.setLayoutParams(params);
        /**
         * IndicatorView的大小
         */
        float indicatorSize = mWidth / 2.5f;
        LayoutParams indicatorParams = new LayoutParams((int) indicatorSize, (int) indicatorSize);
        mIndicator.setLayoutParams(indicatorParams);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int offset = Utils.dp2px(getContext(), mFanMumOffset);
        int fanSize = mWidth - offset;
        mAngleView.layout(0, mHeight - fanSize, fanSize, mHeight);
        /**
         * IndicatorView的大小
         */
        float indicatorSize = mWidth / 2.5f;
        mIndicator.layout(0, mHeight - (int) indicatorSize, (int) indicatorSize, mHeight);
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
                mAngleView.downAngle(mLastMotionX, mHeight - mLastMotionY);
                break;
            case MotionEvent.ACTION_MOVE:
                float newX = event.getX();
                float newY = event.getY();
                float diffX = newX - mLastMotionX;
                float diffY = newY - mLastMotionY;
                if ((Math.abs(diffX) > mTouchSlop || Math.abs(diffY) > mTouchSlop) && isAllowAngle) {
                    mTouchState = TOUCH_STATE_WHIRLING;
                }

                if (mTouchState == TOUCH_STATE_WHIRLING && !mAngleView.isAnimatorComplete() && newY < mHeight) {
                    mAngleView.changeAngle(newX, mHeight - newY);
                }

                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                float vx = mVelocityTracker.getXVelocity();
                float vy = mVelocityTracker.getYVelocity();
                mAngleView.flingAngle(vx, vy);
                recyleVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                recyleVelocityTracker();
                break;
        }
        return true;
    }

    @Override
    public void forward(int cur, float pre) {
        mIndicator.onForward(cur, pre);
    }

    @Override
    public void reverse(int cur, float pre) {
        mIndicator.onReverse(cur, pre);
    }

    /**
     * 重要方法:反转AngleLayout
     * 反转之后根据不同情况对子控件做反转
     */
    private void setRotationY() {
        setRotationY(180);
        mIndicator.setSTATE(IndicatorView.STATE_RIGHT);
        mAngleView.POSITION_STATE = AngleView.RIGHT;
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
