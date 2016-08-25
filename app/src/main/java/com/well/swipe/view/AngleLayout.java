package com.well.swipe.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import com.well.swipe.ItemApplication;
import com.well.swipe.ItemSwipeTools;
import com.well.swipe.R;
import com.well.swipe.tools.ToolsStrategy;
import com.well.swipecomm.utils.Utils;
import com.well.swipecomm.view.LoadingView;
import com.well.swipecomm.view.OnScaleChangeListener;
import com.well.swipecomm.view.PositionState;

/**
 * Created by mingwei on 2/26/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ：      721881283
 *
 *
 */
public class AngleLayout extends FrameLayout implements AngleView.OnAngleChangeListener,
        AngleIndicatorView.OnIndexChangedLitener, AngleView.OnEditModeChangeListener, CornerView.OnCornerClickListener,
        AngleView.OnBindListener {
    
    private Context mContext;
    /**
     * 旋转View
     */
    private AngleView mAngleView;
    /**
     * AngleView主题
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
    /**
     * AngleIndicatorView主题
     */
    private AngleIndicatorViewTheme mIndicatorTheme;
    /**
     * 状态view,返回,退出编辑,垃圾桶
     */
    private CornerView mCornerView;

    private CornerThemeView mCornerTheme;

    private LoadingView mLoading;

    private float mThemeScale;

    private int mChildHalfSize;

    private AngleItemStartUp mDragView;
    /**
     * 拖拽view内部触摸点到左left的距离
     */
    private float mDragOffsetLeft;
    /**
     * 拖拽view内部触摸点到上top的距离
     */
    private float mDragoffsetTop;

    private int mAngleLogoSize;

    private int mIndicatorSize;

    private int mLoadingSize;
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

    private ValueAnimator mAnimator;

    private boolean isAnimator = true;

    public OnOffListener mOffListener;
    /**
     * 是否拖拽到了垃圾桶区域
     */
    private boolean isDelTrash;

    private boolean isLockAction;

    private int MOVE_TYPE = TYPE_NULL;

    static final int TYPE_ROTATION = 1;

    static final int TYPE_OFF = 2;

    static final int TYPE_NULL = -1;


    public interface OnOffListener extends OnScaleChangeListener {
        /**
         * 当Angle关闭的时候回调,目的是通知SwipeLayout去dissmis
         */
        void onOff();
    }

    public OnItemDragListener mItemDragListener;

    public interface OnItemDragListener {

        /**
         * 拖拽结束时调用
         *
         * @param index 返回当前的数据索引index
         */
        void onDragEnd(int index);
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
        /**
         * Item尺寸
         */
        mChildHalfSize = getResources().getDimensionPixelSize(R.dimen.angleitem_half_size);
        /**
         * AngleView的大小
         */
        mAngleSize = getResources().getDimensionPixelSize(R.dimen.angleview_size);
        /**
         * IndicatorView的大小
         */
        mIndicatorSize = getResources().getDimensionPixelSize(R.dimen.angleindicator_size);
        /**
         * indicator的大小用像素，因为如果是dp的话，当半径大小不一时，用三段弧线拼接出来的指示器两头对其所需要的ossfert值无法确定的计算
         * 所以用的px，然后再根据IndicatorView的大小求的的scale值来对IndicatorViewTheme来进行缩放，效果就达到了
         */
        mIndicatorThemeSize = getResources().getDimensionPixelSize(R.dimen.angleindicator_theme_size);
        mAngleLogoSize = getResources().getDimensionPixelSize(R.dimen.anglelogo_size);
        mLoadingSize = getResources().getDimensionPixelSize(R.dimen.loadingview_size);

        ViewConfiguration mConfig = ViewConfiguration.get(context);
        mTouchSlop = mConfig.getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mMinmumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        mDragView = (AngleItemStartUp) LayoutInflater.from(context).inflate(R.layout.angle_item_startup, null);
        mDragView.setVisibility(GONE);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAngleView = (AngleView) findViewById(R.id.angleview);
        mAngleViewTheme = (AngleViewTheme) findViewById(R.id.angleview_theme);
        mAngleView.setOnAngleChangeListener(this);
        mAngleView.setOnAngleLongClickListener(this);

        mIndicator = (AngleIndicatorView) findViewById(R.id.indicator);
        mIndicatorTheme = (AngleIndicatorViewTheme) findViewById(R.id.indicator_theme);

        mIndicator.setOnChangeListener(this);
        mIndicator.setCurrent(0);

        mCornerView = (CornerView) findViewById(R.id.corner_view);
        mCornerView.setOnCornerListener(this);

        mCornerTheme = (CornerThemeView) findViewById(R.id.corner_theme);

        mLoading = (LoadingView) findViewById(R.id.recent_loading);
        mAngleView.setOnBindListener(this);


        /**
         * 拖拽view
         */
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) mAngleView.getChildHalfSize() * 2, (int) mAngleView.getChildHalfSize() * 2);
        addView(mDragView, params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        /**
         * AngleView,IndicatorView的大小
         */
        if (mAngleView.isLeft()) {
            mAngleView.layout(0, mHeight - mAngleSize, mAngleSize, mHeight);
            mAngleViewTheme.layout(0, mHeight - mAngleSize, mAngleSize, mHeight);
            mIndicator.layout(0, mHeight - mIndicatorSize, mIndicatorSize, mHeight);
            mIndicatorTheme.layout(0, mHeight - mIndicatorThemeSize, mIndicatorThemeSize, mHeight);
            //mAngleLogo.layout(0, mHeight - mAngleLogoSize, mAngleLogoSize, mHeight);
            //mAngleLogo.setRotationY(0);
            mIndicatorTheme.setPivotX(0);
            mIndicatorTheme.setPivotY(mIndicatorThemeSize);
            mCornerView.layout(0, mHeight - mAngleLogoSize, mAngleLogoSize, mHeight);
            mCornerTheme.layout(0, mHeight - mAngleLogoSize, mAngleLogoSize, mHeight);
            mCornerTheme.setPivotX(0);
            mCornerTheme.setPivotY(mAngleLogoSize);
            mLoading.layout((mAngleSize - mLoadingSize) / 2, mHeight - (mAngleSize + mLoadingSize) / 2,
                    (mAngleSize + mLoadingSize) / 2, mHeight - (mAngleSize - mLoadingSize) / 2);
        } else if (mAngleView.isRight()) {
            mAngleView.layout(mWidth - mAngleSize, mHeight - mAngleSize, mWidth, mHeight);
            mAngleViewTheme.layout(mWidth - mAngleSize, mHeight - mAngleSize, mWidth, mHeight);
            mIndicator.layout(mWidth - mIndicatorSize, mHeight - mIndicatorSize, mWidth, mHeight);
            mIndicatorTheme.layout(mWidth - mIndicatorThemeSize, mHeight - mIndicatorThemeSize, mWidth, mHeight);
            //mAngleLogo.layout(mWidth - mAngleLogoSize, mHeight - mAngleLogoSize, mWidth, mHeight);
            //mAngleLogo.setRotationY(180);
            mIndicatorTheme.setPivotX(mIndicatorThemeSize);
            mIndicatorTheme.setPivotY(mIndicatorThemeSize);
            mCornerView.layout(mWidth - mAngleLogoSize, mHeight - mAngleLogoSize, mWidth, mHeight);
            mCornerTheme.layout(mWidth - mAngleLogoSize, mHeight - mAngleLogoSize, mWidth, mHeight);
            mCornerTheme.setPivotX(mAngleLogoSize);
            mCornerTheme.setPivotY(mAngleLogoSize);
            mLoading.layout(mWidth - mAngleSize + (mAngleSize - mLoadingSize) / 2, mHeight - (mAngleSize + mLoadingSize) / 2,
                    mWidth - mAngleSize + (mAngleSize + mLoadingSize) / 2, mHeight - (mAngleSize - mLoadingSize) / 2);
        }
        /**
         * 根据Indicator来缩放IndicatorTheme保证Theme的质量
         */
        float scale = (float) mIndicatorSize / (float) mIndicatorThemeSize;
        mIndicatorTheme.setScaleX(scale);
        mIndicatorTheme.setScaleY(scale);

        /**
         * 删除时的背景颜色的Scale值
         */
        mThemeScale = (float) mIndicatorSize / (float) mAngleLogoSize;
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

                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();

                mActivePointId = ev.getPointerId(0);
                if (mEditState == STATE_NORMAL) {
                    if (mAngleView.isLeft()) {
                        mAngleView.downAngle(mLastMotionX, mHeight - mLastMotionY);
                    } else if (mAngleView.isRight()) {
                        mAngleView.downAngle(mWidth - mLastMotionX, mHeight - mLastMotionY);
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:

                float newX = ev.getX();
                float newY = ev.getY();
                float diffX = newX - mLastMotionX;
                float diffY = newY - mLastMotionY;
                if (mEditState == STATE_NORMAL) {
                    if ((Math.abs(diffX) > mTouchSlop || Math.abs(diffY) > mTouchSlop) && isAllowAngle) {
                        return true;
                    }

                } else if (mEditState == STATE_EDIT) {
                    //if ((Math.abs(diffX) > mTouchSlop || Math.abs(diffY) > mTouchSlop) && isAllowAngle) {
                    return true;
                    //}
                }

                break;
            case MotionEvent.ACTION_UP:
                break;
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
                mActivePointId = event.getPointerId(0);

                if (mTouchState == TOUCH_STATE_WHIRLING) {
                }
                if (mEditState == STATE_NORMAL) {
                    if (mAngleView.isLeft()) {
                        mAngleView.downAngle(mLastMotionX, mHeight - mLastMotionY);
                        return true;
                    } else if (mAngleView.isRight()) {
                        mAngleView.downAngle(mWidth - mLastMotionX, mHeight - mLastMotionY);
                        return true;
                    }
                } else if (mEditState == STATE_EDIT) {
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
                if (mEditState == STATE_NORMAL) {
                    if (mTouchState == TOUCH_STATE_WHIRLING && newY < mHeight) {

                        if (mAngleView.isLeft()) {

                            /**
                             * 动作处理：先判断手指滑动的区域，一旦满足条件就设置MOVE类型，并锁定
                             */
                            if (newX > mLastMotionX && newY > mLastMotionY || newX < mLastMotionX && newY < mLastMotionY) {
                                /**
                                 * 判断区域转动
                                 */
                                if (!isLockAction) {
                                    isLockAction = true;
                                    MOVE_TYPE = TYPE_ROTATION;
                                }


                            } else if (newX < mLastMotionX && newY > mLastMotionY) {
                                /**
                                 *判断滑动关闭区域
                                 */
                                float dis = (float) Math.sqrt(Math.pow((newX - mLastMotionX), 2) + Math.pow((newY - mLastMotionY), 2));
                                if (dis > 200) {
                                    if (!isLockAction) {
                                        isLockAction = true;
                                        MOVE_TYPE = TYPE_OFF;
                                    }
                                }
                            }
                            /**
                             * 处理TYPE
                             */
                            if (MOVE_TYPE == TYPE_ROTATION) {
                                mAngleView.changeAngle(newX, mHeight - newY);
                            }
                        } else if (mAngleView.isRight()) {
                            if (newX < mLastMotionX && newY > mLastMotionY || newX > mLastMotionX && newY < mLastMotionY) {
                                if (!isLockAction) {
                                    isLockAction = true;
                                    MOVE_TYPE = TYPE_ROTATION;
                                }
                            } else if (newX > mLastMotionX && newY > mLastMotionY) {
                                float dis = (float) Math.sqrt(Math.pow((newX - mLastMotionX), 2) + Math.pow((newY - mLastMotionY), 2));
                                if (dis > mAngleSize / 2) {
                                    if (!isLockAction) {
                                        isLockAction = true;
                                        MOVE_TYPE = TYPE_OFF;
                                    }
                                }
                            }

                            if (MOVE_TYPE == TYPE_ROTATION) {
                                mAngleView.changeAngle(mWidth - newX, mHeight - newY);
                            }
                        }
                    }
                } else if (mEditState == STATE_EDIT) {
                    onDrag(event.getX(), event.getY(), true);
                }

                break;
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                float vx = mVelocityTracker.getXVelocity();
                float vy = mVelocityTracker.getYVelocity();
                if (mEditState == STATE_NORMAL && MOVE_TYPE == TYPE_ROTATION) {
                    mAngleView.fling(vx, vy);
                } else if (mEditState == STATE_EDIT) {
                    onEndDrag();
                    if (isDelTrash) {
                        /**
                         * 拖拽到了垃圾箱区域删除
                         */
                        if (mTargetView != null) {
                            Object tag = mTargetView.getTag();
                            if (tag instanceof ItemApplication) {
                                int index = ((ItemApplication) tag).delete(mContext);
                                if (index > 0) {
                                    /**
                                     *删除成功后更新界面
                                     */

                                    mDragView.setVisibility(GONE);
                                    mTargetView = null;
                                    getAngleView().removeItem();

                                }
                            } else if (tag instanceof ItemSwipeTools) {
                                int index = ((ItemSwipeTools) tag).delete(mContext);
                                if (index > 0) {

                                    mDragView.setVisibility(GONE);
                                    mTargetView = null;
                                    getAngleView().removeItem();
                                }
                            }
                        }

                    } else {
                        /**
                         * 没有拖拽到垃圾箱区域就复原动画
                         */
                        float xy[] = findEndCoordinate();

                        restoreDragView(event.getX(), xy[0], event.getY(), xy[1]);
                    }

                }
                recyleVelocityTracker();
                float upX = event.getX();
                float upY = event.getY();
                long upTime = System.currentTimeMillis();
                if (mAngleView.isLeft()) {
                    float upDistance = (float) Math.sqrt(Math.pow((upX - 0), 2) + Math.pow((upY - mHeight), 2));
                    if (Math.abs(upX - mLastMotionX) < 8 && Math.abs(upY - mLastMotionY) < 8 &&
                            (upTime - mLastTime) < 200 && (upDistance > mAngleView.getMeasuredHeight())) {
                        if (mEditState == STATE_EDIT) {
                            setEditState(AngleLayout.STATE_NORMAL);
                        } else {
                            off();
                        }
                    }
                } else if (mAngleView.isRight()) {
                    float upDistance = (float) Math.sqrt(Math.pow((upX - mWidth), 2) + Math.pow((upY - mHeight), 2));
                    if (Math.abs(upX - mLastMotionX) < 8 && Math.abs(upY - mLastMotionY) < 8 &&
                            (upTime - mLastTime) < 200 && (upDistance > mAngleView.getMeasuredHeight())) {
                        if (mEditState == STATE_EDIT) {
                            setEditState(AngleLayout.STATE_NORMAL);
                        } else {
                            off();
                        }
                    }
                }

                if (MOVE_TYPE == TYPE_OFF && upTime - mLastTime < 400) {
                    off();
                }
                isLockAction = false;
                MOVE_TYPE = TYPE_NULL;
                break;
            case MotionEvent.ACTION_CANCEL:
                recyleVelocityTracker();
                /**
                 * 复原动作锁定和MOVE类型
                 */
                isLockAction = false;
                MOVE_TYPE = TYPE_NULL;
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
    public void onIndexChanged(int index) {
        /**
         * 点击Indicator的时候自动旋转AngleView
         */
        if (mEditState == STATE_NORMAL) {
            mAngleView.setViewsIndex(index);
        }
    }

    @Override
    public void onEnterEditMode(View view) {
        /**
         * 长安之后进入编辑模式
         */
        mEditState = STATE_EDIT;
        /**
         * 角落View的状态
         */
        mCornerView.setState(CornerView.STATE_EDIT);
    }

    @Override
    public void onExitEditMode() {
        mEditState = STATE_NORMAL;
        mCornerView.setState(CornerView.STATE_NORMAL);
    }

    /**
     * 拖拽前赋的值，在松开手之后用来结束时隐藏拖拽View的替身
     */
    AngleItemCommon mTargetView;

    @Override
    public void onStartDrag(AngleItemCommon view, float newleft, float newtop, float offsetLeft, float offsetTop) {

        mTargetView = view;
        if (isRestoreFinish) {
            Object object = view.getTag();
            if (object instanceof ItemApplication) {
                ItemApplication item = (ItemApplication) object;
                mDragView.setTitle(item.mTitle.toString());
                mDragView.setItemIcon(item.mIconBitmap);
                mDragView.setItemIconBackground(null);
                mDragView.setVisibility(VISIBLE);
            } else if (object instanceof ItemSwipeTools) {
                ItemSwipeTools item = (ItemSwipeTools) object;
                mDragView.setTitle(item.mTitle.toString());
                mDragView.setItemIconBackground(getResources().getDrawable(R.drawable.angle_item_bg));
                ToolsStrategy.getInstance().initView(getContext(), mDragView, item);
                mDragView.setVisibility(VISIBLE);
            }
            mAngleView.exchangePre();
            mDragOffsetLeft = offsetLeft - 1;
            mDragoffsetTop = offsetTop - 1;
            if (mAngleView.isLeft()) {
                startDrag(newleft, newtop);
            } else if (mAngleView.isRight()) {
                startDrag(newleft + mWidth - mAngleSize, newtop);
            }
        }
        mCornerView.setState(CornerView.STATE_DRAG);
    }

    /**
     * 开始Drag是根据接口返回的X，Y设定View的初始位置
     * 长按时调用一次
     *
     * @param x 初始X
     * @param y 初始Y
     */
    public void startDrag(float x, float y) {
        if (isRestoreFinish) {
            onDragging(x, y + mHeight - mAngleSize, false);
        }
    }

    /**
     * 拖拽中,松开手自动复原时调用
     *
     * @param x    坐标X
     * @param y    坐标Y
     * @param drag 是否主动拖拽
     */
    public void onDrag(float x, float y, boolean drag) {
        onDragging(x - mDragOffsetLeft, y - mDragoffsetTop, drag);
    }

    /**
     * 拖拽中
     *
     * @param x    eventX
     * @param y    eventY
     * @param drag drag==true时检测AngleView的位置
     */
    public void onDragging(float x, float y, boolean drag) {
        if (isRestoreFinish) {
            mDragView.setTranslationX(x);
            mDragView.setTranslationY(y);
        }
        /**
         * 拖拽中
         */
        if (drag) {
            if (mAngleView.isLeft()) {
                /**
                 * 不为空的时候触发删除，触发开始动画，开始动画开始之后，结束动画才能才能开始
                 * 松手之后执行复原动画，在动画结束时吧mTargetView置空，用来保证垃圾箱不在拖拽的时候触发
                 */
                if (mTargetView != null) {
                    mAngleView.checkAndChange(x, y - (mHeight - mAngleSize));
                    checkTrashView(x + mChildHalfSize, y + mChildHalfSize, 0, mHeight);
                }
            } else if (mAngleView.isRight()) {
                if (mTargetView != null) {
                    mAngleView.checkAndChange(x - (mWidth - mAngleSize), y - (mHeight - mAngleSize));
                    checkTrashView(x + mChildHalfSize, y + mChildHalfSize, mWidth, mHeight);
                }
            }
        } else {
            /**
             * 松手后位移
             */
            mDragView.setTranslationX(x);
            mDragView.setTranslationY(y);
        }
    }

    public void checkTrashView(float x, float y, float centerX, float centerY) {
        float upDistance = (float) Math.sqrt(Math.pow((x - centerX), 2) + Math.pow((y - centerY), 2));
        if (upDistance <= mIndicatorSize * (0.7f)) {
            isDelTrash = true;
            cornerAnimatorStart();
        } else {
            isDelTrash = false;
            cornerAnimatorReverse();
        }
    }

    /**
     * 找到 松手之后要复原的view的位置坐标
     *
     * @return xy坐标数组
     */
    public float[] findEndCoordinate() {
        AngleView.Coordinate coordinate = mAngleView.findEmpty();
        float x = 0f;
        float y = 0f;
        if (coordinate != null) {
            if (mAngleView.isLeft()) {
                x = coordinate.x + mDragOffsetLeft - mAngleView.getChildHalfSize();
            } else if (mAngleView.isRight()) {
                x = coordinate.x + mDragOffsetLeft - mAngleView.getChildHalfSize() + mWidth - mAngleSize;
            }
            y = coordinate.y + mDragoffsetTop - mAngleView.getChildHalfSize() + mHeight - mAngleSize;
        }
        return new float[]{x, y};
    }


    @Override
    public void onCancelDrag() {
        float xy[] = findEndCoordinate();
        restoreDragView(xy[0], xy[0], xy[1], xy[1]);
        mCornerView.setState(CornerView.STATE_EDIT);
    }

    public void onEndDrag() {
        cornerAnimatorReverse();
        mCornerView.setState(CornerView.STATE_EDIT);
    }

    boolean isRestoreFinish = true;

    /**
     * 复原动画
     *
     * @param startX
     * @param endX
     * @param startY
     * @param endY
     */
    public void restoreDragView(final float startX, final float endX, final float startY, final float endY) {
        if (isRestoreFinish) {
            isRestoreFinish = false;
            mAngleView.setIsRestoreFinish(false);
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0f);
            valueAnimator.setDuration(250);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float v = (float) animation.getAnimatedValue();
                    onDrag((startX - endX) * v + endX, (startY - endY) * v + endY, false);
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mDragView.setVisibility(GONE);
                    if (null != mTargetView) {
                        mTargetView.setVisibility(VISIBLE);
                    }
                    mTargetView = null;
                    isRestoreFinish = true;
                    mAngleView.setIsRestoreFinish(true);
                    /**
                     *最后检测数据，发生变化就跟新，否则忽略
                     */
                    mItemDragListener.onDragEnd(mAngleView.getViewsIndex());
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            valueAnimator.start();
        }

    }

    public void setEditState(final int state) {
        mEditState = state;
        mAngleView.endEditMode();
        mCornerView.setState(CornerView.STATE_NORMAL);
    }

    public int getEditState() {
        return mEditState;
    }


    @Override
    public void cornerEvent() {
        if (mEditState == STATE_EDIT) {
            setEditState(AngleLayout.STATE_NORMAL);
            return;
        }
        if (mEditState == STATE_NORMAL) {
            off();
        }
    }

    @Override
    public void bindComplete() {
        mLoading.stop();
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
        mCornerView.setPositionState(state);
        mCornerTheme.setPositionState(state);
    }

    public int getPositionState() {
        return mAngleView.getPositionState();
    }

    public void setOnOffListener(OnOffListener listener) {
        mOffListener = listener;
    }

    public void setOnDragItemListener(OnItemDragListener listener) {
        mItemDragListener = listener;
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

    private boolean isCornerAnimatorStart = true;

    /**
     * 垃圾箱红色背景展开
     */
    public void cornerAnimatorStart() {
        if (isCornerAnimatorStart) {
            isCornerAnimatorStart = false;
            final float diff = mThemeScale - 1f;
            ValueAnimator animator = ValueAnimator.ofFloat(1f, mThemeScale);
            animator.setDuration(150);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float v = (float) animation.getAnimatedValue();
                    mCornerTheme.setScaleX(v);
                    mCornerTheme.setScaleY(v);
                    mCornerView.setTrashState((v - 1) / diff);

                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mCornerTheme.setWarnColor();
                    isCornerAnimatorReverse = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
        }

    }

    private boolean isCornerAnimatorReverse = false;

    /**
     * 垃圾箱背景关闭
     */
    public void cornerAnimatorReverse() {
        if (isCornerAnimatorReverse) {
            isCornerAnimatorReverse = false;
            final float diff = mThemeScale - 1f;
            ValueAnimator animator = ValueAnimator.ofFloat(1f, mThemeScale);
            animator.setDuration(150);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float v = (float) animation.getAnimatedValue();
                    mCornerTheme.setScaleX(v);
                    mCornerTheme.setScaleY(v);
                    mCornerView.setTrashState((v - 1) / diff);
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isCornerAnimatorStart = true;
                    mCornerTheme.setGreenColor();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.reverse();
        }

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
