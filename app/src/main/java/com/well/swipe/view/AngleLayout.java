package com.well.swipe.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.well.swipe.R;
import com.well.swipe.utils.Utils;

/**
 * Created by mingwei on 2/26/16.
 */
public class AngleLayout extends FrameLayout implements AngleView.OnAngleChangeListener,
        AngleIndicatorView.OnIndexChangedLitener, AngleView.OnLongClickListener {
    /**
     *
     */
    private Context mContext;
    /**
     * 旋转View
     */
    private AngleView mAngleView;
    /**
     *
     */
    private AngleViewTheme mAngleViewTheme;
    /**
     *
     */
    private int mAngleSize;
    /**
     * 底部的指示器
     */
    private AngleIndicatorView mIndicator;

    private AngleIndicatorViewTheme mIndicatorTheme;

    private ImageView mAngleLogo;

    private int mAngleLogoSize;

    private int mIndicatorSize;
    /**
     * 主题的小用像素，然后根据Indocator再做大小变化
     */
    private int mIndicatorThemeSize;
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

    private float mLastMotionX;

    private float mLastMotionY;

    private long mLastTime;

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

    private float mAngleLayoutScale;

    /**
     * 切换状态，开和关
     */
    private int mSwitchType = SWITCH_TYPE_OFF;

    public static final int SWITCH_TYPE_ON = 0;

    public static final int SWITCH_TYPE_OFF = 1;
    /**
     * AngleView 的编辑状态
     */
    private int mEditState = STATE_NORMAL;
    /**
     * 正常模式
     */
    public static final int STATE_NORMAL = 0;
    /**
     * 编辑模式
     */
    public static final int STATE_EDIT = 1;
    /**
     * 拖拽模式
     */
    public static final int STATE_DRAG = 2;

    private ValueAnimator mAnimator;

    private boolean isAnimator = true;

    public OnOffListener mOffListener;

    public interface OnOffListener extends OnScaleChangeListener {
        /**
         * 当Angle关闭的时候回调,目的是通知SwipeLayout去dissmis
         */
        void onOff();
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
        mAngleViewTheme = (AngleViewTheme) findViewById(R.id.angleview_theme);
        mAngleView.setOnAngleChangeListener(this);
        mAngleView.setOnLongClickListener(this);
        mIndicator = (AngleIndicatorView) findViewById(R.id.indicator);
        mIndicatorTheme = (AngleIndicatorViewTheme) findViewById(R.id.indicator_theme);
        mIndicator.setOnChangeListener(this);
        mIndicator.setCurrent(0);
        mAngleLogo = (ImageView) findViewById(R.id.angle_logo);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        /**
         * AngleView的大小
         */
        mAngleSize = getResources().getDimensionPixelSize(R.dimen.angleview_size);
        /**
         * IndicatorView的大小
         */
        mIndicatorSize = getResources().getDimensionPixelSize(R.dimen.angleindicator_size);

        mIndicatorThemeSize = getResources().getDimensionPixelSize(R.dimen.angleindicator_theme_size);

        mAngleLogoSize = getResources().getDimensionPixelSize(R.dimen.anglelogo_size);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        /**
         * AngleView,IndicatorView的大小
         */
        if (mAngleView.getPositionState() == PositionState.POSITION_STATE_LEFT) {
            mAngleView.layout(0, mHeight - mAngleSize, mAngleSize, mHeight);
            mAngleViewTheme.layout(0, mHeight - mAngleSize, mAngleSize, mHeight);
            mIndicator.layout(0, mHeight - mIndicatorSize, mIndicatorSize, mHeight);
            mIndicatorTheme.layout(0, mHeight - mIndicatorThemeSize, mIndicatorThemeSize, mHeight);
            mAngleLogo.layout(0, mHeight - mAngleLogoSize, mAngleLogoSize, mHeight);
            mAngleLogo.setRotationY(0);
            mIndicatorTheme.setPivotX(0);
            mIndicatorTheme.setPivotY(mIndicatorThemeSize);
        } else if (mAngleView.getPositionState() == PositionState.POSITION_STATE_RIGHT) {
            mAngleView.layout(mWidth - mAngleSize, mHeight - mAngleSize, mWidth, mHeight);
            mAngleViewTheme.layout(mWidth - mAngleSize, mHeight - mAngleSize, mWidth, mHeight);
            mIndicator.layout(mWidth - mIndicatorSize, mHeight - mIndicatorSize, mWidth, mHeight);
            mIndicatorTheme.layout(mWidth - mIndicatorThemeSize, mHeight - mIndicatorThemeSize, mWidth, mHeight);
            mAngleLogo.layout(mWidth - mAngleLogoSize, mHeight - mAngleLogoSize, mWidth, mHeight);
            mAngleLogo.setRotationY(180);
            mIndicatorTheme.setPivotX(mIndicatorThemeSize);
            mIndicatorTheme.setPivotY(mIndicatorThemeSize);
        }
        /**
         * 根据Indicator来缩放IndicatorTheme保证Theme的质量
         */
        float scale = (float) mIndicatorSize / (float) mIndicatorThemeSize;
        mIndicatorTheme.setScaleX(scale);
        mIndicatorTheme.setScaleY(scale);
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
                if (mEditState == STATE_NORMAL) {
                    if (mAngleView.getPositionState() == PositionState.POSITION_STATE_LEFT) {
                        mAngleView.downAngle(mLastMotionX, mHeight - mLastMotionY);
                    } else if (mAngleView.getPositionState() == PositionState.POSITION_STATE_RIGHT) {
                        mAngleView.downAngle(mWidth - mLastMotionX, mHeight - mLastMotionY);
                    }
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
                mLastTime = System.currentTimeMillis();
                //Log.i("Gmw", "down=" + mLastMotionX + "," + mLastMotionY);
                mActivePointId = event.getPointerId(0);
                if (mTouchState == TOUCH_STATE_WHIRLING) {
                    //正在滚动的时候
                }
                if (mEditState == STATE_NORMAL) {
                    if (mAngleView.getPositionState() == PositionState.POSITION_STATE_LEFT) {
                        mAngleView.downAngle(mLastMotionX, mHeight - mLastMotionY);
                        return true;
                    } else if (mAngleView.getPositionState() == PositionState.POSITION_STATE_RIGHT) {
                        mAngleView.downAngle(mWidth - mLastMotionX, mHeight - mLastMotionY);
                        return true;
                    }
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
                if (mEditState == STATE_NORMAL) {
                    if (mTouchState == TOUCH_STATE_WHIRLING && newY < mHeight) {
                        if (mAngleView.getPositionState() == PositionState.POSITION_STATE_LEFT) {
                            mAngleView.changeAngle(newX, mHeight - newY);
                        } else if (mAngleView.getPositionState() == PositionState.POSITION_STATE_RIGHT) {
                            mAngleView.changeAngle(mWidth - newX, mHeight - newY);
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                float vx = mVelocityTracker.getXVelocity();
                float vy = mVelocityTracker.getYVelocity();
                if (mEditState == STATE_NORMAL) {
                    mAngleView.fling(vx, vy);
                }
                recyleVelocityTracker();
                float upX = event.getX();
                float upY = event.getY();
                long upTime = System.currentTimeMillis();
                if (mAngleView.getPositionState() == PositionState.POSITION_STATE_LEFT) {
                    float upDistance = (float) Math.sqrt(Math.pow((upX - 0), 2) + Math.pow((upY - mHeight), 2));
                    if (Math.abs(upX - mLastMotionX) < 8 && Math.abs(upY - mLastMotionY) < 8 &&
                            (upTime - mLastTime) < 200 && (upDistance > mAngleView.getMeasuredHeight())) {
                        off();
                    }
                } else if (mAngleView.getPositionState() == PositionState.POSITION_STATE_RIGHT) {
                    float upDistance = (float) Math.sqrt(Math.pow((upX - mWidth), 2) + Math.pow((upY - mHeight), 2));
                    if (Math.abs(upX - mLastMotionX) < 8 && Math.abs(upY - mLastMotionY) < 8 &&
                            (upTime - mLastTime) < 200 && (upDistance > mAngleView.getMeasuredHeight())) {
                        off();
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                recyleVelocityTracker();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onAngleChanged(int cur, float p) {
        mIndicator.onAngleChanged2(cur, p);
        mIndicatorTheme.changeStartAngle(cur, p);
    }

    @Override
    public void end(int endIndex) {
        //mIndicator.setCurrent(endIndex);
    }

    @Override
    public void onIndexChanged(int index) {
        /**
         * 点击Indicator的时候自动旋转AngleView
         */
        if (mEditState == STATE_NORMAL) {
            mAngleView.setViewsIndex(index);
        }

    }

    @Override
    public void onLongClick(View view) {
        /**
         * 长安之后进入编辑模式
         */
        mEditState = STATE_EDIT;

    }

    public void setEditState(final int state) {
        mEditState = state;
        mAngleView.endEditMode();
    }

    public int getEditState() {
        return mEditState;
    }

    /**
     * 重要方法:反转AngleLayout
     * 反转之后根据不同情况对子控件做反转
     */
    public void setPositionLeft() {
        setPivotX(0);
        setPivotY(mContext.getResources().getDisplayMetrics().heightPixels - Utils.getStatusBarHeight(mContext));
        setPositionState(PositionState.POSITION_STATE_LEFT);
        /**
         * 左右两边的的角度一样，但是显示的限象不一样，通过一个公倍数12来换算成限象一样
         */
        if (mAngleView.getCurrentIndex() != 0) {
            mAngleView.setBaseAngle((12 - mAngleView.getCurrentIndex()) * AngleView.DEGREES_90);
        }
        requestLayout();
    }

    /**
     * 重要方法:反转AngleLayout
     * 反转之后根据不同情况对子控件做反转
     */
    public void setPositionRight() {
        setPivotX(mContext.getResources().getDisplayMetrics().widthPixels);
        setPivotY(mContext.getResources().getDisplayMetrics().heightPixels - Utils.getStatusBarHeight(mContext));
        setPositionState(PositionState.POSITION_STATE_RIGHT);

        /**
         * 左右两百年的角度一样，但是显示的限象不一样，通过一个公倍数12来换算成限象一样
         */
        if (mAngleView.getCurrentIndex() != 0) {
            mAngleView.setBaseAngle(mAngleView.getCurrentIndex() * AngleView.DEGREES_90);
        }
        requestLayout();
    }

    public void setPositionState(int state) {
        mIndicator.setPositionState(state);
        mIndicatorTheme.setPositionState(state);
        mAngleView.setPositionState(state);
        mAngleViewTheme.setPositionState(state);
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
     * 放大和缩小的状态
     *
     * @param scale
     */
    public void setAngleLayoutScale(float scale) {
        mAngleLayoutScale = scale;
        setScaleX(mAngleLayoutScale);
        setScaleY(mAngleLayoutScale);
    }

    /**
     * 自动切换菜单
     */
    public void switchAngleLayout() {
        if (mAngleLayoutScale < 0.3) {
            off(mAngleLayoutScale);
        } else if (mAngleLayoutScale >= 0.3) {
            on(mAngleLayoutScale);
        }
    }

    /**
     * 获取当前的Scale值
     *
     * @return
     */
    public float getAngleLayoutScale() {
        return mAngleLayoutScale;
    }

    /**
     * 打开
     */
    public void on() {
        on(mAngleLayoutScale);
    }

    public void on(float start) {
        if (isAnimator) {
            mSwitchType = SWITCH_TYPE_ON;
            animator(start, 1.0f);
        }
    }

    public void off() {
        off(mAngleLayoutScale);
    }

    public void offnoAnimator() {
        mAngleLayoutScale = 0f;
        mSwitchType = SWITCH_TYPE_OFF;
    }

    /**
     * 关闭
     *
     * @param start
     */
    private void off(float start) {
        if (isAnimator) {
            mSwitchType = SWITCH_TYPE_OFF;
            animator(start, 0f);
        }
    }

    public void animator(float start, float end) {
        isAnimator = false;
        mAnimator = ValueAnimator.ofFloat(start, end);
        mAnimator.setDuration(500);
        if (mSwitchType == SWITCH_TYPE_ON) {
            mAnimator.setInterpolator(new OvershootInterpolator(1.2f));
        } else if (mSwitchType == SWITCH_TYPE_OFF) {
            mAnimator.setInterpolator(new AnticipateOvershootInterpolator(0.9f));
        }
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
                isAnimator = true;
                if (mSwitchType == SWITCH_TYPE_OFF) {
                    mOffListener.onOff();
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

    /**
     * 返回AngleView
     *
     * @return
     */
    public AngleView getAngleView() {
        return mAngleView;
    }

    /**
     * 获取开还是关
     *
     * @return
     */
    public int getSwitchType() {
        return mSwitchType;
    }

    public void setSwitchType(int type) {
        mSwitchType = type;
    }

}
