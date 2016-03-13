package com.well.swipe.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.well.swipe.R;
import com.well.swipe.utils.Utils;

/**
 * Created by mingwei on 2/26/16.
 */
public class AngleLayout extends FrameLayout implements AngleView.OnAngleChangeListener,
        AngleIndicatorView.OnIndexChangedLitener {
    Context mContext;
    /**
     * 旋转View
     */
    private AngleView mAngleView;
    /**
     * 底部的指示器
     */
    private AngleIndicatorView mIndicator;
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

    //private float mDownMotionX;

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

    private float mAngleLayoutScale;

    private int mSwitchType = SWITCH_TYPE_ON;

    private static final int SWITCH_TYPE_ON = 0;

    private static final int SWITCH_TYPE_OFF = 1;

    private ValueAnimator mAnimator;

    public OnOffListener mOffListener;

    public interface OnOffListener extends OnScaleChangeListener {
        /**
         * 当Angle关闭的时候回调,目的是通知SwipeLayout去dissmis
         */
        void off();
    }

    public AngleLayout(Context context) {
        this(context, null);
    }

    public AngleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AngleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
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
        mIndicator = (AngleIndicatorView) findViewById(R.id.indicator);
        mIndicator.setOnChangeListener(this);
        mIndicator.setCurrent(0);
        //setPositionRight();
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
        /**
         * AngleView,IndicatorView的大小
         */
        int indicatorSize = (int) (mWidth / 2.5f);
        if (mAngleView.getPositionState() == AngleView.POSITION_STATE_LEFT) {
            mAngleView.layout(0, mHeight - fanSize, fanSize, mHeight);
            mIndicator.layout(0, mHeight - indicatorSize, indicatorSize, mHeight);
        } else if (mAngleView.getPositionState() == AngleView.POSITION_STATE_RIGHT) {
            mAngleView.layout(mWidth - fanSize, mHeight - fanSize, mWidth, mHeight);
            mIndicator.layout(mWidth - indicatorSize, mHeight - indicatorSize, mWidth, mHeight);
        }

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getChildCount() <= 0) {
            return super.onInterceptTouchEvent(ev);
        }
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mTouchState = TOUCH_STATE_REST;
                //mDownMotionX = ev.getX();
                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();
                mActivePointId = ev.getPointerId(0);
                if (mAngleView.getPositionState() == AngleView.POSITION_STATE_LEFT) {
                    mAngleView.downAngle(mLastMotionX, mHeight - mLastMotionY);
                } else if (mAngleView.getPositionState() == AngleView.POSITION_STATE_RIGHT) {
                    mAngleView.downAngle(mWidth - mLastMotionX, mHeight - mLastMotionY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float newX = ev.getX();
                float newY = ev.getY();
                float diffX = newX - mLastMotionX;
                float diffY = newY - mLastMotionY;
                if ((Math.abs(diffX) > mTouchSlop || Math.abs(diffY) > mTouchSlop) && isAllowAngle) {
                    mTouchState = TOUCH_STATE_WHIRLING;
                }
                if (mTouchState == TOUCH_STATE_WHIRLING) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
                break;
        }

        return super.onInterceptTouchEvent(ev);
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
                mTouchState = TOUCH_STATE_REST;
                mLastMotionX = event.getX();
                mLastMotionY = event.getY();
                mActivePointId = event.getPointerId(0);
                if (mTouchState == TOUCH_STATE_WHIRLING) {
                    //正在滚动的时候
                }
                if (mAngleView.getPositionState() == AngleView.POSITION_STATE_LEFT) {
                    mAngleView.downAngle(mLastMotionX, mHeight - mLastMotionY);
                    return true;
                } else if (mAngleView.getPositionState() == AngleView.POSITION_STATE_RIGHT) {
                    mAngleView.downAngle(mWidth - mLastMotionX, mHeight - mLastMotionY);
                    return true;
                }

                break;
            case MotionEvent.ACTION_MOVE:

                float newX = event.getX();
                float newY = event.getY();
                float diffX = newX - mLastMotionX;
                float diffY = newY - mLastMotionY;
                if ((Math.abs(diffX) > mTouchSlop || Math.abs(diffY) > mTouchSlop) && isAllowAngle) {
                    mTouchState = TOUCH_STATE_WHIRLING;
                }
                /**
                 *转动AngleView
                 */
                if (mTouchState == TOUCH_STATE_WHIRLING && newY < mHeight) {
                    if (mAngleView.getPositionState() == AngleView.POSITION_STATE_LEFT) {
                        mAngleView.changeAngle(newX, mHeight - newY);
                    } else if (mAngleView.getPositionState() == AngleView.POSITION_STATE_RIGHT) {
                        mAngleView.changeAngle(mWidth - newX, mHeight - newY);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                float vx = mVelocityTracker.getXVelocity();
                float vy = mVelocityTracker.getYVelocity();
                mAngleView.fling(vx, vy);
                recyleVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                recyleVelocityTracker();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onAngleChanged(int cur, float p) {
        mIndicator.onAngleChanged(cur, p);
    }

    @Override
    public void onIndexChanged(int index) {
        mAngleView.setViewsIndex(index);
    }

    /**
     * 重要方法:反转AngleLayout
     * 反转之后根据不同情况对子控件做反转
     */
    public void setPositionLeft() {
        setPivotX(0);
        setPivotY(mContext.getResources().getDisplayMetrics().heightPixels - Utils.getStatusBarHeight(mContext));
        mIndicator.setPositionState(AngleIndicatorView.POSITION_STATE_LEFT);
        mAngleView.setPositionState(AngleView.POSITION_STATE_LEFT);
        /**
         * 左右两百年的角度一样，但是显示的限象不一样，通过一个公倍数12来换算成限象一样
         */
        if (mAngleView.getCurrentIndex() != 0) {
            mAngleView.setBaseAngle((12 - mAngleView.getCurrentIndex()) * AngleView.DEGREES_90);
        }
    }

    /**
     * 重要方法:反转AngleLayout
     * 反转之后根据不同情况对子控件做反转
     */
    public void setPositionRight() {
        setPivotX(mContext.getResources().getDisplayMetrics().widthPixels);
        setPivotY(mContext.getResources().getDisplayMetrics().heightPixels - Utils.getStatusBarHeight(mContext));
        mIndicator.setPositionState(AngleIndicatorView.POSITION_STATE_RIGHT);
        mAngleView.setPositionState(AngleView.POSITION_STATE_RIGHT);
        /**
         * 左右两百年的角度一样，但是显示的限象不一样，通过一个公倍数12来换算成限象一样
         */
        if (mAngleView.getCurrentIndex() != 0) {
            mAngleView.setBaseAngle(mAngleView.getCurrentIndex() * AngleView.DEGREES_90);
        }
    }

    public int getPositionState() {
        return mAngleView.getPositionState();
    }

    public void setOnOffListener(OnOffListener listener) {
        mOffListener = listener;
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

    /**
     * @param scale
     */
    public void setAngleLayoutScale(float scale) {
        mAngleLayoutScale = scale;
        setScaleX(mAngleLayoutScale);
        setScaleY(mAngleLayoutScale);
    }

    public void switchAngleLayout() {
        if (mAngleLayoutScale < 0.3) {
            off(mAngleLayoutScale);
        } else if (mAngleLayoutScale >= 0.3) {
            on(mAngleLayoutScale);
        }
    }

    public float getAngleLayoutScale() {
        return mAngleLayoutScale;
    }

    public void on() {
        on(mAngleLayoutScale);
    }

    public void on(float start) {
        mSwitchType = SWITCH_TYPE_ON;
        animator(start, 1.0f);
    }

    public void off(float start) {
        mSwitchType = SWITCH_TYPE_OFF;
        animator(start, 0f);
    }

    public void animator(float start, float end) {
        mAnimator = ValueAnimator.ofFloat(start, end);
        mAnimator.setDuration(300);
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                mOffListener.change(v);
                setAngleLayoutScale(v);
            }
        });
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mSwitchType == SWITCH_TYPE_OFF) {
                    mOffListener.off();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.start();
    }


}
