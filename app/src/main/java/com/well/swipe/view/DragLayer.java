package com.well.swipe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
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
     * fan的旋转角度
     */
    private int mFanCurRotation = 0;
    /**
     * 按下时相对底部的角度
     */
    private double mLastRotation;
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

    /**
     * 顺时针/逆时针
     */
    private int mWhirling = ALONG;

    private static final int ALONG = 0;

    private static final int INVERSE = 1;

    private float mDownMotionX;

    private float mLastMotionX;

    private float mLastMotionY;

    private int mActivePointId;
    /**
     * 最小移动距离
     */
    private int mTouchSlop;

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
                mLastRotation = Math.toDegrees(Math.atan((mLastMotionX) / ((mHeight - mLastMotionY))));
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
                    double rotation = Math.toDegrees(Math.atan(newX / (mHeight - newY)));
                    double diffrotation = rotation - mLastRotation;
                    Log.i("Gmw", "diffrotation=" + diffrotation);
                    if (diffrotation > 0) {
                        mWhirling = ALONG;
                    } else {
                        mWhirling = INVERSE;
                    }
                    mFanMum.refresh(diffrotation);
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                if (mWhirling == ALONG) {
                    if (mFanMum.getRitation() % FanMum.UNIT_DEGREES > 0 && mFanMum.getRitation() %
                            FanMum.UNIT_DEGREES < FanMum.OFFSET_DEGREES) {
                        Log.i("Gmw", "顺时针_返回当前");
                    } else {
                        Log.i("Gmw", "顺时针_下一个");
                    }
                } else {
                    if (mFanMum.getRitation() % FanMum.UNIT_DEGREES > (FanMum.UNIT_DEGREES - FanMum.OFFSET_DEGREES)) {
                        Log.i("Gmw", "逆时针_返回当前");
                    } else {
                        Log.i("Gmw", "逆时针_上一个");
                    }
                }
                if (mFanMum.getRitation() % FanMum.UNIT_DEGREES > FanMum.OFFSET_DEGREES) {
                    Log.i("Gmw", "touch_ACTION_UP=" + mFanMum.getRitation() % FanMum.UNIT_DEGREES);
                }
                mTouchState = TOUCH_STATE_REST;

                break;
        }

        return true;
    }
}
