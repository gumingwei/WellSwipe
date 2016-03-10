package com.well.swipe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/4/16.
 * AngleLayout的指示器
 */
public class AngleIndicatorView extends View {

    private Paint mPaint0 = new Paint();

    private Paint mPaint1 = new Paint();

    private Paint mPaint2 = new Paint();

    private String mColors[] = new String[]{"#ffff00", "#ffcc00", "#ff9900", "#ff6600", "#ff3300",
            "#ff0000", "#cc3300", "#cc0000", "#993300", "#990000"};

    private int LEFT_OFFSET_X = 125;

    private int RIGHT_OFFSET_X = 25;

    private int OFFSET_Y = 10;

    private int mPositionState = POSITION_STATE_LEFT;

    public static final int POSITION_STATE_LEFT = 1;

    public static final int POSITION_STATE_RIGHT = 2;

    private int mWidth;

    private int mHeight;

    private float mLastX;

    private float mLastY;

    private int mTouchSlop;

    public static final int DEGREE_90 = 90;

    private int DEGREES_U = DEGREE_90 / 8;

    private OnIndexChangedLitener mListener;

    public interface OnIndexChangedLitener {
        /**
         * 状态选中时
         *
         * @param index
         */
        void onIndexChanged(int index);
    }

    public AngleIndicatorView(Context context) {
        this(context, null);
    }

    public AngleIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AngleIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint0.setColor(Color.parseColor(mColors[9]));
        mPaint0.setTextSize(30);
        mPaint0.setAntiAlias(true);

        mPaint1.setColor(Color.parseColor(mColors[9]));
        mPaint1.setTextSize(30);
        mPaint1.setAntiAlias(true);

        mPaint2.setColor(Color.parseColor(mColors[9]));
        mPaint2.setTextSize(30);
        mPaint2.setAntiAlias(true);

        if (mPositionState == POSITION_STATE_LEFT) {
            setRotation(-90);
        }

        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int degree = DEGREE_90 / 4;
        if (mPositionState == POSITION_STATE_LEFT) {
            canvas.save();
            canvas.rotate(degree, 0, 0);
            canvas.drawText(getResources().getString(R.string.recent), LEFT_OFFSET_X, OFFSET_Y, mPaint0);
            canvas.rotate(degree, 0, 0);
            canvas.drawText(getResources().getString(R.string.toolbox), LEFT_OFFSET_X, OFFSET_Y, mPaint1);
            canvas.rotate(degree, 0, 0);
            canvas.drawText(getResources().getString(R.string.frequent), LEFT_OFFSET_X, OFFSET_Y, mPaint2);
            canvas.restore();
        } else if (mPositionState == POSITION_STATE_RIGHT) {
            canvas.save();
            canvas.rotate(-degree, mWidth, 0);
            canvas.drawText(getResources().getString(R.string.recent), RIGHT_OFFSET_X, OFFSET_Y, mPaint0);
            canvas.rotate(-degree, mWidth, 0);
            canvas.drawText(getResources().getString(R.string.toolbox), RIGHT_OFFSET_X, OFFSET_Y, mPaint1);
            canvas.rotate(-degree, mWidth, 0);
            canvas.drawText(getResources().getString(R.string.frequent), RIGHT_OFFSET_X, OFFSET_Y, mPaint2);
            canvas.restore();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

    }

    /**
     * 设置当前索引的变化监听
     *
     * @param listener
     */
    public void setOnChangeListener(OnIndexChangedLitener listener) {
        mListener = listener;
    }

    /**
     * 初始化当前选中的画笔的颜色
     *
     * @param current 当前高亮的画笔颜色
     */
    public void setCurrent(int current) {
        if (current == 0) {
            mPaint0.setColor(Color.parseColor(mColors[0]));
        } else if (current == 1) {
            mPaint0.setColor(Color.parseColor(mColors[0]));
        } else if (current == 2) {
            mPaint0.setColor(Color.parseColor(mColors[0]));
        }
        invalidate();
    }

    /**
     * 设置是左还是右
     *
     * @param state
     */
    public void setPositionState(int state) {
        this.mPositionState = state;
        if (state == POSITION_STATE_LEFT) {
            setRotation(-90);
            setRotationY(0);
        } else if (state == POSITION_STATE_RIGHT) {
            //setRotationY(DEGREE_90 * 2);
            setRotation(DEGREE_90);
        }
        invalidate();
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
                break;
            case MotionEvent.ACTION_UP:
                float newx = event.getX();
                float newy = event.getY();
                if (Math.abs(newx - mLastX) < mTouchSlop || Math.abs(newy - mLastY) < mTouchSlop) {
                    double degree = 0;
                    if (mPositionState == POSITION_STATE_LEFT) {
                        degree = Math.toDegrees(Math.atan(newy / newx));
                    } else if (mPositionState == POSITION_STATE_RIGHT) {
                        degree = Math.toDegrees(Math.atan(newy / (mWidth - newx)));
                    }
                    if (degree > 0 && degree < DEGREES_U * 3) {
                        mListener.onIndexChanged(0);
                    } else if (degree > DEGREES_U * 3 && degree < DEGREES_U * 5) {
                        mListener.onIndexChanged(1);
                    } else if (degree > DEGREES_U * 5 && degree < DEGREES_U * 8) {
                        mListener.onIndexChanged(2);
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 顺时针时刷新颜色
     *
     * @param cur
     * @param pre
     */
    public void onAngleChanged(int cur, float pre) {
        int index = (int) (pre * 10);
        if (mPositionState == POSITION_STATE_LEFT) {
            if (cur == 0) {
                mPaint0.setColor(Color.parseColor(mColors[index]));
                mPaint2.setColor(Color.parseColor(mColors[9 - index]));
            } else if (cur == 1) {
                mPaint1.setColor(Color.parseColor(mColors[index]));
                mPaint0.setColor(Color.parseColor(mColors[9 - index]));
            } else if (cur == 2) {
                mPaint2.setColor(Color.parseColor(mColors[index]));
                mPaint1.setColor(Color.parseColor(mColors[9 - index]));
            }
        } else if (mPositionState == POSITION_STATE_RIGHT) {
            if (cur == 0) {
                mPaint0.setColor(Color.parseColor(mColors[index]));
                mPaint1.setColor(Color.parseColor(mColors[9 - index]));
            } else if (cur == 1) {
                mPaint2.setColor(Color.parseColor(mColors[index]));
                mPaint0.setColor(Color.parseColor(mColors[9 - index]));
            } else if (cur == 2) {
                mPaint1.setColor(Color.parseColor(mColors[index]));
                mPaint2.setColor(Color.parseColor(mColors[9 - index]));
            }
        }

        invalidate();
    }


}
