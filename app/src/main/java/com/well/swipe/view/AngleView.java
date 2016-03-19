package com.well.swipe.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.well.swipe.ItemApplication;
import com.well.swipe.R;
import com.well.swipe.ItemSwipeSwitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by mingwei on 2/25/16.
 * 旋转的扇形View
 * 1.扇形可以放在左边或者右边
 * 2.根据手指在上一层容器的touch事件来处理AngleView的转动，松开手指转动到指定位置
 * 3.通过捕捉飞快的手指滑动来转动容器
 * 4.给子控件设置扇形的布局坐标
 * 5.长按进入编辑模式
 */
public class AngleView extends ViewGroup {
    /**
     * 旋转的基数角度
     */
    private float mBaseAngle = 0;
    /**
     * 跟随手指转动的是的变量
     */
    private float mChangeAngle = 0;
    /**
     * 按下时的角度
     */
    private double mDownAngle;
    /**
     * 转轴PivotX,PivotY
     */
    private int mPivotX = 0;

    private int mPivotY = 0;
    /**
     * 控件的宽高
     */
    private int mHeight;

    private int mWidth;

    static int POSITION_STATE_LEFT = 1;

    static int POSITION_STATE_RIGHT = 2;

    /**
     * 用于计算单击时间的X&Y
     */
    private float mMotionX;

    private float mMotionY;
    /**
     * 单击事件的第一个时间
     */
    private long mClickTime1;
    /**
     * 判断是否点击到了删除按钮
     */
    private boolean isDelClick;
    /**
     * AngleView点击时候找到的ItemView
     */
    private AngleItemCommon mTargetItem;

    private AngleItemAddTo mItemExtra;
    /**
     * 容器在做右下角区分
     */
    private int mPositionState = POSITION_STATE_LEFT;
    /**
     * 顺时针/逆时针
     */
    private int ANGLE_STATE = ANGLE_STATE_REST;

    public static final int ANGLE_STATE_REST = 0;

    public static final int ANGLE_STATE_ALONG = 1;

    public static final int ANGLE_STATE_INVERSE = 2;

    public static final int DEGREES_360 = 360;

    public static final int DEGREES_1080 = DEGREES_360 * 3;

    private int mChildHalfSize;

    public int mInnerRadius;

    public int mOuterRadius;

    public int mDeleteBtnSize;
    /**
     * 单位度数
     */
    public static final int DEGREES_90 = 90;
    /**
     * 判定范围
     */
    public static final int DEGREES_OFFSET = 20;
    /**
     * 判定Fling动作的速度范围
     */
    private static final int ALLOW_FLING = 2500;

    private static final int COUNT_4 = 4;

    private static final int COUNT_3 = 3;

    private static final int COUNT_12 = COUNT_4 * COUNT_3;


    private int mCurrentIndex;

    private ValueAnimator mAngleAnimator;

    private Map<Integer, ArrayList<View>> mMap = new HashMap<>();

    private ArrayList<View> mRecentAppList = new ArrayList<>();

    private ArrayList<View> mSwitchList = new ArrayList<>();

    private ArrayList<View> mFavoriteAppList = new ArrayList<>();

    private OnAngleChangeListener mAngleListener;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    /**
     * 长按处理
     */
    private LongClickRunable mLongRunable = new LongClickRunable();
    /**
     * 震动
     */
    private Vibrator mVibrator;

    public interface OnAngleChangeListener {

        /**
         * 角度发生变化时传递当前的所显示的数据索引值&当前的百分比
         * 用于改变Indicator的选中状态，百分比则用来渲染过渡效果
         *
         * @param cur 正在显示的是数据index
         * @param p   百分比
         */
        void onAngleChanged(int cur, float p);

        /**
         * 旋转停止时调用
         * 在快速点击切换Indicator的时候，由于设计机制的原因，比如：在indicator==0时点击了1，当动画还没有完全执行
         * 完成的时候再点击2，此时执行的是0-2的动画过程，so...刚刚执行了一半的1就停在半路了，此方法的目的是动画执行
         * 完成时强行校对选中状态
         *
         * @param endIndex 结束的当前数据索引Index
         */
        void end(int endIndex);

    }

    OnEditModeListener mEditModeListener;

    public interface OnEditModeListener {
        /**
         * 进入编辑模式
         */
        void startEditMode();

        /**
         * 结束编辑模式
         */
        void endEditMode();
    }

    OnClickListener mOnClickListener;

    public interface OnClickListener {
        /**
         * 本身的OnClick无效，所以自己根据在onTouch中定义一个Onclick
         */
        void onClick(View view);

        /**
         * 删除按钮
         *
         * @param view
         */
        void onDeleteClick(View view);
    }

    OnLongClickListener mOnLongClickListener;

    public interface OnLongClickListener {
        /**
         * 本身的OnLongClick无效，所以自己根据在onTouch中定义一个OnLongClick
         *
         * @param view
         */
        void onLongClick(View view);
    }

    public AngleView(Context context) {
        this(context, null);
    }

    public AngleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AngleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mChildHalfSize = getResources().getDimensionPixelSize(R.dimen.angleitem_half_size);
        mInnerRadius = getResources().getDimensionPixelSize(R.dimen.angleview_inner_radius);
        mOuterRadius = getResources().getDimensionPixelSize(R.dimen.angleview_outer_radius);
        mDeleteBtnSize = getResources().getDimensionPixelSize(R.dimen.angleview_item_delete_size);

        mMap.put(0, mRecentAppList);
        mMap.put(1, mSwitchList);
        mMap.put(2, mFavoriteAppList);
    }

    public void refresh() {
        removeAllViews();
        Iterator<Map.Entry<Integer, ArrayList<View>>> it = mMap.entrySet().iterator();
        //LayoutParams params = new LayoutParams(120, 120);
        while (it.hasNext()) {
            Map.Entry<Integer, ArrayList<View>> arraylist = it.next();
            ArrayList<View> views = arraylist.getValue();

            for (View view : views) {
                if (view.getParent() == null) {
                    addView(view);
                }
            }


        }
        requestLayout();
    }

    public void putItemApplications(ArrayList<ItemApplication> itemlist) {
        if (mFavoriteAppList.size() > 0) {
            mFavoriteAppList.clear();
        }
        AngleItemStartUp itemview;
        for (ItemApplication appitem : itemlist) {
            itemview = (AngleItemStartUp) LayoutInflater.from(getContext()).inflate(R.layout.angle_item, null);
            itemview.setTitle(appitem.mTitle.toString());
            itemview.setItemIcon(appitem.mIconBitmap);
            mFavoriteAppList.add(itemview);
        }
        /**
         * 添加额外的add按钮
         */
        AngleItemAddTo mTargetItem = (AngleItemAddTo) LayoutInflater.from(getContext()).inflate(R.layout.angle_item_extra, null);
        mFavoriteAppList.add(mTargetItem);
    }

    public void putItemQuickSwitch(ArrayList<ItemSwipeSwitch> itemlist) {
        if (mSwitchList.size() > 0) {
            mSwitchList.clear();
        }
        AngleItemStartUp itemview;
        for (ItemSwipeSwitch appitem : itemlist) {
            itemview = (AngleItemStartUp) LayoutInflater.from(getContext()).inflate(R.layout.angle_item, null);
            itemview.setTitle(appitem.mTitle.toString());
            mSwitchList.add(itemview);
        }
        /**
         * 添加额外的add按钮
         */
        AngleItemAddTo mTargetItem = (AngleItemAddTo) LayoutInflater.from(getContext()).inflate(R.layout.angle_item_extra, null);
        mSwitchList.add(mTargetItem);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mPivotX = getMeasuredWidth();
        mPivotY = getMeasuredHeight();
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        //setBaseAngle(90);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).measure(mChildHalfSize * 2, mChildHalfSize * 2);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int index = (int) ((mBaseAngle) / DEGREES_90);
        if (mPositionState == POSITION_STATE_LEFT) {
            itemLayout(index);
        } else if (mPositionState == POSITION_STATE_RIGHT) {
            itemLayout2(index);
        }
    }

    /**
     * 布局子控件
     * 通过一个当前值，计算上一个，下一个值
     *
     * @param index
     */
    private void itemLayout(int index) {
        mCurrentIndex = getRealIndex(index);
        itemLayout(mMap.get(getPreViewsIndex(getViewsIndex(index))), getPreQuaIndex(getQuaIndex(index)));
        itemLayout(mMap.get(getViewsIndex(index)), getQuaIndex(index));
        itemLayout(mMap.get(getNextViewsIndex(getViewsIndex(index))), getNextQuaIndex(getQuaIndex(index)));
    }

    /**
     * mPositionState＝Right的时候
     *
     * @param index
     */
    private void itemLayout2(int index) {
        mCurrentIndex = index;
        itemLayout(mMap.get(getPreViewsIndex(getViewsIndex2(index))), getPreQuaIndex(getQuaIndex2(index)));
        itemLayout(mMap.get(getViewsIndex2(index)), getQuaIndex2(index));
        itemLayout(mMap.get(getNextViewsIndex(getViewsIndex2(index))), getNextQuaIndex(getQuaIndex2(index)));
    }

    /**
     * 把一组数据指定布局到一个限象
     *
     * @param views 需要布局的数据组
     * @param qua   限象
     */
    private void itemLayout(ArrayList<View> views, int qua) {
        for (int index = 0; index < views.size(); index++) {
            /**
             * size按照当前views的总数，以4为区分，分别计算出<4,=4,超出4的部分剪掉4即从1，2，3重新开始计数
             */
            int size = 0;
            /**
             * group可认为是跟随环数而变化的一个值，用来计算index非0时的子控件的角度增长
             * 角度增为只有一个子控件的时候：90/1=45；
             * index非0的时候：(group＋0.5)*newdegree(按照当前环中子控件的总数平分90的值)
             */
            int group = 0;
            /**
             * 半径变化，环数增加，半径增加
             */
            int radius = 0;
            if (views.size() <= COUNT_4) {
                size = views.size();
                group = index;
                radius = mInnerRadius;
            } else if (views.size() <= 9) {
                if (index < COUNT_4) {
                    /**
                     * 总数大于4时内环正好是4
                     */
                    size = COUNT_4;
                    group = index;
                    radius = mInnerRadius;
                } else {
                    /**
                     * 总数大于4时外环
                     * size＝总数－4
                     * group＝views(index)-4
                     */
                    size = views.size() - COUNT_4;
                    group = index - COUNT_4;
                    radius = mOuterRadius;


                }
            } else {
                if (index < COUNT_4) {
                    /**
                     * 总数大于4时内环正好是4
                     */
                    size = COUNT_4;
                    group = index;
                    radius = mInnerRadius;
                } else {
                    /**
                     * 总数大于4时外环
                     * size＝总数－4
                     * group＝views(index)-4
                     */
                    size = COUNT_4 + 1;
                    group = index - COUNT_4;
                    radius = mOuterRadius;


                }
            }
            /**
             * 按照views(index)所在的当前环的个数平分90度
             */
            float degree = (float) DEGREES_90 / (float) (size);
            /**
             * 得出一个新的递增的角度，用来后面按照三角函数计算子控的位置
             */
            float newdegree;
            if (index == 0) {
                newdegree = degree / 2;
            } else {
                newdegree = (int) ((group + 0.5) * degree);
            }
            /**
             * 1.按照限象使用不同的三角函数计算所得x,y坐标
             * 2.子控件根据不同的呃限象旋转位置满足在第0限象的正常显示效果
             * 3.当整个控件的容器反转之后，为保证显示效果，要做一定的反转
             */
            double x = 0l;
            double y = 0l;
            if (mPositionState == POSITION_STATE_LEFT) {
                if (qua == 0) {
                    x = Math.sin(Math.toRadians(newdegree)) * radius;
                    y = mHeight - Math.cos(Math.toRadians(newdegree)) * radius;
                } else if (qua == 1) {
                    x = Math.cos(Math.toRadians(newdegree)) * radius;
                    y = mHeight + Math.sin(Math.toRadians(newdegree)) * radius;
                } else if (qua == 2) {
                    x = -Math.sin(Math.toRadians(newdegree)) * radius;
                    y = mHeight + Math.cos(Math.toRadians(newdegree)) * radius;
                } else if (qua == 3) {
                    x = -Math.cos(Math.toRadians(newdegree)) * radius;
                    y = mHeight - Math.sin(Math.toRadians(newdegree)) * radius;
                }
            } else if (mPositionState == POSITION_STATE_RIGHT) {
                if (qua == 0) {
                    x = mWidth - Math.sin(Math.toRadians(newdegree)) * radius;
                    y = mHeight - Math.cos(Math.toRadians(newdegree)) * radius;
                } else if (qua == 1) {
                    x = mWidth - Math.cos(Math.toRadians(newdegree)) * radius;
                    y = mHeight + Math.sin(Math.toRadians(newdegree)) * radius;
                } else if (qua == 2) {
                    x = mWidth + Math.sin(Math.toRadians(newdegree)) * radius;
                    y = mHeight + Math.cos(Math.toRadians(newdegree)) * radius;
                } else if (qua == 3) {
                    x = mWidth + Math.cos(Math.toRadians(newdegree)) * radius;
                    y = mHeight - Math.sin(Math.toRadians(newdegree)) * radius;
                }
            }

            /**
             * 矫正子view
             * 旋转一定的角度,以保证旋转至第0限象时方向是正的
             */
            if (mPositionState == POSITION_STATE_LEFT) {
                views.get(index).setRotation(DEGREES_90 * qua);
            } else if (mPositionState == POSITION_STATE_RIGHT) {
                views.get(index).setRotation(-DEGREES_90 * qua);
            }
            /**
             * 指定位置
             */
            if (index < 9) {
                views.get(index).layout((int) (x - mChildHalfSize), (int) (y - mChildHalfSize), (int) (x + mChildHalfSize), (int) (y + mChildHalfSize));
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        if (mPositionState == POSITION_STATE_LEFT) {
            canvas.rotate(mBaseAngle + mChangeAngle, 0, mPivotY);
        } else if (mPositionState == POSITION_STATE_RIGHT) {
            canvas.rotate(mBaseAngle + mChangeAngle, mPivotX, mPivotY);
        }
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                mMotionX = event.getX();
                mMotionY = event.getY();
                //float newx = event.getX();
                //float newy = event.getY();
                ArrayList<View> views = getData();
                for (int index = 0; index < views.size(); index++) {
                    /**
                     * size按照当前views的总数，以4为区分，分别计算出<4,=4,超出4的部分剪掉4即从1，2，3重新开始计数
                     */
                    int size = 0;
                    /**
                     * group可认为是跟随环数而变化的一个值，用来计算index非0时的子控件的角度增长
                     * 角度增为只有一个子控件的时候：90/1=45；
                     * index非0的时候：(group＋0.5)*newdegree(按照当前环中子控件的总数平分90的值)
                     */
                    int group = 0;
                    /**
                     * 半径变化，环数增加，半径增加
                     */
                    int radius = 0;
                    if (views.size() <= COUNT_4) {
                        size = views.size();
                        group = index;
                        radius = mInnerRadius;
                    } else if (views.size() <= 9) {
                        if (index < COUNT_4) {
                            /**
                             * 总数大于4时内环正好是4
                             */
                            size = COUNT_4;
                            group = index;
                            radius = mInnerRadius;
                        } else if (index < 9) {
                            /**
                             * 总数大于4时外环
                             * size＝总数－4
                             * group＝views(index)-4
                             */
                            size = views.size() - COUNT_4;
                            group = index - COUNT_4;
                            radius = mOuterRadius;
                        }
                    } else {
                        if (index < COUNT_4) {
                            /**
                             * 总数大于4时内环正好是4
                             */
                            size = COUNT_4;
                            group = index;
                            radius = mInnerRadius;
                        } else if (index < 9) {
                            /**
                             * 总数大于4时外环
                             * size＝总数－4
                             * group＝views(index)-4
                             */
                            size = COUNT_4 + 1;
                            group = index - COUNT_4;
                            radius = mOuterRadius;
                        }
                    }
                    /**
                     * 按照views(index)所在的当前环的个数平分90度
                     */
                    float degree = (float) DEGREES_90 / (float) (size);
                    /**
                     * 得出一个新的递增的角度，用来后面按照三角函数计算子控的位置
                     */
                    float newdegree;
                    if (index == 0) {
                        newdegree = degree / 2;
                    } else {
                        newdegree = (int) ((group + 0.5) * degree);
                    }
                    /**
                     * 1.按照限象使用不同的三角函数计算所得x,y坐标
                     * 2.子控件根据不同的呃限象旋转位置满足在第0限象的正常显示效果
                     * 3.当整个控件的容器反转之后，为保证显示效果，要做一定的反转
                     */
                    double x = 0l;
                    double y = 0l;
                    if (mPositionState == POSITION_STATE_LEFT) {
                        x = Math.sin(Math.toRadians(newdegree)) * radius;
                        y = mHeight - Math.cos(Math.toRadians(newdegree)) * radius;
                    } else if (mPositionState == POSITION_STATE_RIGHT) {
                        x = mWidth - Math.sin(Math.toRadians(newdegree)) * radius;
                        y = mHeight - Math.cos(Math.toRadians(newdegree)) * radius;
                    }

                    float newleft = (float) (x - mChildHalfSize);
                    float newtop = (float) (y - mChildHalfSize);
                    float newright = (float) (x + mChildHalfSize);
                    float newbottom = (float) (y + mChildHalfSize);

                    if (mMotionX > newleft && mMotionX < newright && mMotionY > newtop && mMotionY < newbottom) {
                        mClickTime1 = System.currentTimeMillis();
                        /**
                         * 找到当前点击的那个item
                         */
                        mTargetItem = ((AngleItemCommon) views.get(index));

                        if (mTargetItem instanceof AngleItemStartUp) {
                            if (mMotionX > newleft && mMotionX < (newleft + mDeleteBtnSize) && mMotionY > newtop &&
                                    mMotionY < (newtop + mDeleteBtnSize) && ((AngleItemStartUp) mTargetItem).getDelBtn().
                                    getVisibility() == View.VISIBLE) {
                                isDelClick = true;
                            }
                        } else if (mTargetItem instanceof AngleItemAddTo) {

                        }
                        handler.postDelayed(mLongRunable, 600);
                        return true;
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                //handler.removeCallbacks(mLongRunable);
                float movenewx = event.getX();
                float movenewy = event.getY();
                if (Math.abs(movenewx - mMotionX) > 5 || Math.abs(movenewy - mMotionY) > 4) {
                    handler.removeCallbacks(mLongRunable);
                }
                break;
            case MotionEvent.ACTION_UP:
                float clicknewx = event.getX();
                float clicknewy = event.getY();
                long clicktime = System.currentTimeMillis();
                if (Math.abs(mMotionX - clicknewx) < 10 && Math.abs(mMotionY - clicknewy) < 10) {
                    long time = Math.abs(mClickTime1 - clicktime);
                    if (time < 300) {
                        if (isDelClick) {
                            //Log.i("Gmw", "单击删除按钮");
                            getData().remove(mTargetItem);
                            refresh();
                            if (mOnClickListener != null) {
                                mOnClickListener.onDeleteClick(mTargetItem);
                            }
                        } else {
                            //Log.i("Gmw", "单击按钮");
                            //mTargetItem.setScaleX(0.5f);
                            shake(mTargetItem);
                            //requestLayout();
                            if (mOnClickListener != null) {
                                mOnClickListener.onClick(mTargetItem);
                            }
                        }
                        handler.removeCallbacks(mLongRunable);

                    }
                }
                isDelClick = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                isDelClick = false;
                break;
        }

        return super.onTouchEvent(event);

    }

    public void setOnAngleChangeListener(OnAngleChangeListener listener) {
        mAngleListener = listener;
    }

    public void setOnEditModeListener(OnEditModeListener listener) {
        mEditModeListener = listener;
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    public void setOnLongClickListener(OnLongClickListener listener) {
        mOnLongClickListener = listener;
    }

    /**
     * 手指按下时的初始角度
     */
    public void downAngle(float x, float y) {
        mDownAngle = Math.toDegrees(Math.atan(x / y));
    }

    /**
     * 手指滑动时
     * 动态的计算一个变化量diff，在根据条件合理的计算
     *
     * @param x
     * @param y
     */
    public void changeAngle(float x, float y) {
        double diffAngle;
        double angle;
        angle = Math.toDegrees(Math.atan(x / y));
        diffAngle = angle - mDownAngle;
        if (diffAngle > 0) {
            ANGLE_STATE = ANGLE_STATE_ALONG;
        } else {
            ANGLE_STATE = ANGLE_STATE_INVERSE;
        }
        if (mPositionState == POSITION_STATE_LEFT) {
            changeAngle(diffAngle);
        } else if (mPositionState == POSITION_STATE_RIGHT) {
            changeAngle(-diffAngle);
        }
    }

    /**
     * 改变mChangeAngle的值
     *
     * @param rotation
     */
    private void changeAngle(double rotation) {
        mChangeAngle = (float) rotation;
        angleChange();
    }

    private void angleChange() {
        /**
         * 转动的时候回传当前限象index
         */
        mAngleListener.onAngleChanged(getViewsIndex((int) (getAngleValues() / DEGREES_90)), ((getAngleValues() % DEGREES_90) / DEGREES_90));
        invalidate();
    }

    /**
     * 松开手指后根据x,y方向上的速度来判定是留在当前限象还是旋转到上一个或者下一个限象
     *
     * @param vx
     * @param vy
     */
    public void fling(float vx, float vy) {
        if (mPositionState == POSITION_STATE_LEFT) {
            if (vy > ALLOW_FLING || vx > ALLOW_FLING) {
                flingForward();
            } else if (vx < -ALLOW_FLING || vy < -ALLOW_FLING) {
                flingCurrnet();
            } else {
                upAngle();
            }
        } else if (mPositionState == POSITION_STATE_RIGHT) {
            if (vy > ALLOW_FLING || vx < -ALLOW_FLING) {
                flingCurrnet();
            } else if (vy < -ALLOW_FLING || vx > ALLOW_FLING) {
                flingForward();
            } else {
                upAngle();
            }
        }

    }

    /**
     * 松手后根据当前已经旋转的角，POSITION，顺逆时针来判定是留在当前限象还是旋转到上一个或者下一个限象
     */
    private void upAngle() {
        if (ANGLE_STATE == ANGLE_STATE_ALONG) {
            forward();
        } else if (ANGLE_STATE == ANGLE_STATE_INVERSE) {
            reverse();
        }
    }

    /**
     * 顺时针旋转
     */
    private void forward() {

        float diff = 0;
        if (mPositionState == POSITION_STATE_LEFT) {
            diff = getAngleValues() % DEGREES_90;
            if (diff > 0 && diff < DEGREES_OFFSET) {
                flingCurrnet();
            } else if (diff > DEGREES_OFFSET && diff < DEGREES_90) {
                /**
                 * 转到下一个
                 */
                flingForward();
            }
        } else if (mPositionState == POSITION_STATE_RIGHT) {
            diff = DEGREES_90 - getAngleValues() % DEGREES_90;
            if (diff > 0 && diff < DEGREES_OFFSET) {
                flingForward();
            } else if (diff > DEGREES_OFFSET && diff < DEGREES_90) {
                /**
                 * 转到下一个
                 */
                flingCurrnet();
            }
        }
    }

    /**
     * 逆时针旋转
     */
    private void reverse() {
        float diff = 0;
        if (mPositionState == POSITION_STATE_LEFT) {
            diff = (DEGREES_1080 - getAngleValues()) % DEGREES_90;
            if (diff > 0 && diff < DEGREES_OFFSET) {
                flingForward();
            } else if (diff > DEGREES_OFFSET && diff < DEGREES_90) {
                /**
                 * 转到下一个
                 */
                flingCurrnet();
            }
        } else if (mPositionState == POSITION_STATE_RIGHT) {
            diff = getAngleValues() % DEGREES_90;
            if (diff > 0 && diff < DEGREES_OFFSET) {
                flingCurrnet();
            } else if (diff > DEGREES_OFFSET && diff < DEGREES_90) {
                flingForward();
            }
        }

    }

    /**
     * 顺时针到下一个90度
     */
    private void flingForward() {
        autoWhirling(getAngleValues(), ((int) (getAngleValues() / DEGREES_90) + 1) * DEGREES_90);
    }

    /**
     * 回到当前的角度
     */
    private void flingCurrnet() {
        autoWhirling(getAngleValues(), ((int) (getAngleValues() / DEGREES_90)) * DEGREES_90);
    }

    /**
     * 逆时针到上一个90度
     */
    private void flingReveser() {
        autoWhirling(getAngleValues(), ((int) (getAngleValues() / DEGREES_90) - 1) * DEGREES_90);
    }

    /**
     * 根据当前的数据组和点击情况来切换AngleIndicator
     *
     * @param cur 当前被点击的指示器索引
     */
    public void setViewsIndex(int cur) {
        if (mPositionState == POSITION_STATE_LEFT) {
            int index = getViewsIndex((int) (getAngleValues() / DEGREES_90));
            if (index == 0) {
                if (cur == 1) {
                    flingReveser();
                } else if (cur == 2) {
                    flingForward();
                }
            } else if (index == 1) {
                if (cur == 0) {
                    flingForward();
                } else if (cur == 2) {
                    flingReveser();
                }
            } else if (index == 2) {
                if (cur == 0) {
                    flingReveser();
                } else if (cur == 1) {
                    flingForward();
                }
            }
        } else if (mPositionState == POSITION_STATE_RIGHT) {
            int index = getViewsIndex2((int) (getAngleValues() / DEGREES_90));
            if (index == 0) {
                if (cur == 1) {
                    flingForward();
                } else if (cur == 2) {
                    flingReveser();
                }
            } else if (index == 1) {
                if (cur == 2) {
                    flingForward();
                } else if (cur == 0) {
                    flingReveser();
                }
            } else if (index == 2) {
                if (cur == 0) {
                    flingForward();
                } else if (cur == 1) {
                    flingReveser();
                }
            }
        }

    }

    /**
     * 自动旋转
     *
     * @param start 起始位置
     * @param end   结束为止
     */
    private void autoWhirling(float start, float end) {
        mChangeAngle = 0;
        mAngleAnimator = ValueAnimator.ofFloat(start, end);
        mAngleAnimator.setDuration(500);
        mAngleAnimator.setInterpolator(new DecelerateInterpolator());
        mAngleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mBaseAngle = value;
                mBaseAngle = mBaseAngle % DEGREES_1080;
                angleChange();
            }
        });
        mAngleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                int mIndex = ((int) ((mBaseAngle) / DEGREES_90));
                if (mPositionState == POSITION_STATE_LEFT) {
                    /**
                     * getQuaIndex()
                     */
                    itemLayout(mIndex);

                } else if (mPositionState == POSITION_STATE_RIGHT) {
                    itemLayout2(mIndex);
                }

                mAngleListener.end(getViewsIndex());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAngleAnimator.start();

    }

    public void setPositionState(int position) {
        mPositionState = position;
    }

    public int getPositionState() {
        return mPositionState;
    }

    /**
     * 转动时候随时可以获取到的的转动角度
     *
     * @return
     */
    public float getAngleValues() {
        float newrotation = (mBaseAngle + mChangeAngle);
        return newrotation < 0 ? DEGREES_1080 + (newrotation) : (newrotation);
    }

    public float getBaseAngle() {
        return mBaseAngle;
    }

    public void setBaseAngle(float angle) {
        mBaseAngle = angle;
        angleChange();
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    /**
     * 根据当前的index获取当前显示限象index
     * 比如11->1,10->2,9->3,8->0
     *
     * @param index 转动结束后根据BaseAngle的值除以90得出的范围0-11
     *              3,4的最小公倍数的是12
     * @return
     */
    public int getQuaIndex(int index) {
        return index == 0 ? 0 : (COUNT_12 - index) % COUNT_4;
    }

    /**
     * 获取真实的限象Index
     *
     * @param index
     * @return
     */
    public int getRealIndex(int index) {
        return index == 0 ? 0 : (COUNT_12 - index);
    }

    /**
     * STATE=RIGHT时
     *
     * @param index
     * @return
     */
    public int getQuaIndex2(int index) {
        return index < 0 ? COUNT_4 + index : index % COUNT_4;
    }

    /**
     * 获取当前index的上一个index
     *
     * @param index 传入的是getIndex()的返回值
     * @return 得到上一个index
     */
    public int getPreQuaIndex(int index) {
        return index == 0 ? COUNT_3 : (index - 1);
    }

    /**
     * 获取当前index的下一个index
     *
     * @param index 传入的是getIndex()的返回值
     * @return 得到下一个index
     */
    private int getNextQuaIndex(int index) {
        return index == COUNT_3 ? 0 : (index + 1);
    }


    /**
     * 根据index获取当先index所需要的数据索引
     * 比如: 11->1,10->2,9->0,8->1,7->2,6->0  像这样一直循环
     *
     * @param index 转动结束后根据BaseAngle的值除以90得出的范围0-11
     *              3,4的最小公倍数的是12
     * @return
     */
    private int getViewsIndex(int index) {
        return (COUNT_12 - index) % COUNT_3;
    }

    /**
     * getViewsIndex(int index)重载方法
     *
     * @return 直接返回当前显示数据的Index索引值
     */
    public int getViewsIndex() {
        if (mPositionState == POSITION_STATE_LEFT) {
            return getViewsIndex(((int) ((mBaseAngle) / DEGREES_90)));
        } else {
            return getViewsIndex2(((int) ((mBaseAngle) / DEGREES_90)));
        }
    }

    /**
     * 根据当前的显示数据的Index来从HashMap中的取出数据
     *
     * @return 当前显示在第0限象位置的数据
     */
    public ArrayList<View> getData() {
        return mMap.get(getViewsIndex());
    }

    /**
     * mPositionState=Right
     *
     * @param index 根据BaseAngle求出的实际限象数
     * @return 转换为右侧的实际Index
     */
    private int getViewsIndex2(int index) {
        return index < 0 ? COUNT_3 + index : index % COUNT_3;
    }

    /**
     * 上一个数据索引
     *
     * @param index 传入的是getViews()的返回值
     * @return
     */
    public int getPreViewsIndex(int index) {
        return index == 0 ? 2 : (index - 1);
    }

    /**
     * 下一个数据索引
     *
     * @param index 传入的是getViews()的返回值
     * @return
     */
    public int getNextViewsIndex(int index) {
        return index == 2 ? 0 : (index + 1);
    }

    /**
     * 进入编辑模式，遍历当前的Views集合，显示删除按钮
     */
    public void startEditMode() {
        int index = getViewsIndex();

        for (int i = 0; i < mMap.get(index).size(); i++) {
            AngleItemCommon item = (AngleItemCommon) mMap.get(index).get(i);
            if (item instanceof AngleItemStartUp) {
                ((AngleItemStartUp) item).showDelBtn();
            }
        }
    }

    /**
     * 退出当前编辑模式，遍历当前的Views集合，隐藏编辑按钮
     */
    public void endEditMode() {
        int index = getViewsIndex();
        for (int i = 0; i < mMap.get(index).size(); i++) {
            AngleItemCommon item = (AngleItemCommon) mMap.get(index).get(i);
            if (item instanceof AngleItemStartUp) {
                ((AngleItemStartUp) item).hideDelBtn();
            }

        }
    }

    /**
     * 抖一下
     *
     * @param view
     */
    public void shake(final View view) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.0f, 0.85f);
        valueAnimator.setRepeatCount(1);
        valueAnimator.setDuration(60);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scale = (float) animation.getAnimatedValue();
                view.setScaleX(scale);
                view.setScaleY(scale);
                requestLayout();
            }
        });
        valueAnimator.start();
    }


    public class LongClickRunable implements Runnable {
        @Override
        public void run() {
            if (mOnLongClickListener != null) {
                if (getAngleValues() % DEGREES_90 == 0) {
                    mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = {0, 35};
                    mVibrator.vibrate(pattern, -1);
                    mOnLongClickListener.onLongClick(mTargetItem);
                    startEditMode();
                }
            } else {
                throw new IllegalArgumentException("AngleView.OnClickListener is null(AngleView的Click监听接口对象为空)");
            }
        }
    }

}
