package com.well.swipe.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/4/16.
 * AngleLayout的指示器
 */
public class AngleIndicatorView extends View {

    private TextPaint mPaint0 = new TextPaint();

    private TextPaint mPaint1 = new TextPaint();

    private TextPaint mPaint2 = new TextPaint();

    private Paint[] mPaintArray = new Paint[]{mPaint0, mPaint1, mPaint2};

    private String mColors[] = new String[]{"#f0f0f0", "#eaeaea", "#e1e1e1", "#d6d6d6", "#cac9c9",
            "#bfbebe", "#b6b5b5", "#aeadad", "#a5a4a4", "#9c9c9c"};

    private Paint mPaint;

    private Paint mInnerPaint;

    private int LEFT_OFFSET_X = 145;

    private int RIGHT_OFFSET_X = 15;

    private int OFFSET_Y = 15;

    private int mPositionState = POSITION_STATE_LEFT;

    public static final int POSITION_STATE_LEFT = 1;

    public static final int POSITION_STATE_RIGHT = 2;

    private int mWidth;

    private int mHeight;

    private float mLastX;

    private float mLastY;

    private int mTouchSlop;

    public static final int DEGREES_90 = 90;

    private int DEGREES_U = DEGREES_90 / 8;

    private float mLeftArcStart = START_ANGLE;

    private float mRightArcStart = DEGREES_90 + START_ANGLE * 5;

    private float mArcSweep = START_ANGLE * 2;

    public static final float START_ANGLE = 11.25f;

    private int mIndicatorWidth = 150;

    public int mRect = 210;

    public int mCurrent = 0;

    //public AngleView mAngleview;

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
        mPaint0.setTextSize(32);
        mPaint0.setAntiAlias(true);

        mPaint1.setColor(Color.parseColor(mColors[9]));
        mPaint1.setTextSize(32);
        mPaint1.setAntiAlias(true);

        mPaint2.setColor(Color.parseColor(mColors[9]));
        mPaint2.setTextSize(32);
        mPaint2.setAntiAlias(true);

        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.indicator_theme_purple));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mIndicatorWidth);

        mInnerPaint = new Paint();
        mInnerPaint.setColor(getResources().getColor(R.color.indicator_theme_purple));
        mInnerPaint.setStyle(Paint.Style.STROKE);
        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setStrokeWidth(5);

        if (mPositionState == POSITION_STATE_LEFT) {
            setRotation(-90);
        }
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int degree = DEGREES_90 / 4;
        if (mPositionState == POSITION_STATE_LEFT) {
            RectF f = new RectF(-mRect, -mRect, mRect, mRect);
            canvas.drawArc(f, mLeftArcStart, mArcSweep, false, mPaint);
            canvas.drawCircle(0, 0, mRect - mIndicatorWidth / 2, mInnerPaint);
            canvas.drawCircle(0, 0, mRect + mIndicatorWidth / 2, mInnerPaint);
            canvas.save();
            canvas.rotate(degree, 0, 0);
            canvas.drawText(getResources().getString(R.string.recent), LEFT_OFFSET_X, OFFSET_Y, mPaint0);
            canvas.rotate(degree, 0, 0);
            canvas.drawText(getResources().getString(R.string.toolbox), LEFT_OFFSET_X, OFFSET_Y, mPaint1);
            canvas.rotate(degree, 0, 0);
            canvas.drawText(getResources().getString(R.string.frequent), LEFT_OFFSET_X, OFFSET_Y, mPaint2);
            canvas.restore();
        } else if (mPositionState == POSITION_STATE_RIGHT) {
            RectF f = new RectF(mWidth - mRect, -mRect, mWidth + mRect, mRect);
            canvas.drawArc(f, mRightArcStart, mArcSweep, false, mPaint);
            canvas.drawCircle(mWidth, 0, mRect - mIndicatorWidth / 2, mInnerPaint);
            canvas.drawCircle(mWidth, 0, mRect + mIndicatorWidth / 2, mInnerPaint);
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
            setRotation(-DEGREES_90);
        } else if (state == POSITION_STATE_RIGHT) {
            setRotation(DEGREES_90);
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
                        //setCurrentIndex(0);
                        mListener.onIndexChanged(0);
                    } else if (degree > DEGREES_U * 3 && degree < DEGREES_U * 5) {
                        //setCurrentIndex(1);
                        mListener.onIndexChanged(1);
                    } else if (degree > DEGREES_U * 5 && degree < DEGREES_U * 8) {
                        //setCurrentIndex(2);
                        mListener.onIndexChanged(2);
                    }
                }
                break;
        }
        return true;
    }

    public void setCurrentIndex(int index) {
        if (mPositionState == POSITION_STATE_LEFT) {
            if (mLeftArcStart == START_ANGLE) {
                if (index == 1) {
                    change(1, 3, 0, 1);
                } else if (index == 2) {
                    change(1, 5, 0, 2);
                }
            } else if (mLeftArcStart == 3 * START_ANGLE) {
                if (index == 0) {
                    change(3, 1, 1, 0);
                } else if (index == 2) {
                    change(3, 5, 1, 2);
                }
            } else if (mLeftArcStart == 5 * START_ANGLE) {
                if (index == 1) {
                    change(5, 3, 2, 1);
                } else if (index == 0) {
                    change(5, 1, 2, 0);
                }
            }
        } else if (mPositionState == POSITION_STATE_RIGHT) {
            if (mRightArcStart == DEGREES_90 + 5 * START_ANGLE) {
                if (index == 1) {
                    change(5, 3, 0, 1);
                } else if (index == 2) {
                    change(5, 1, 0, 2);
                }
            } else if (mRightArcStart == DEGREES_90 + 3 * START_ANGLE) {
                if (index == 0) {
                    change(3, 5, 1, 0);
                } else if (index == 2) {
                    change(3, 1, 1, 2);
                }
            } else if (mRightArcStart == DEGREES_90 + 1 * START_ANGLE) {
                if (index == 1) {
                    change(1, 3, 2, 1);
                } else if (index == 0) {
                    change(1, 5, 2, 0);
                }
            }
        }
    }

    public void change(final int start, final int end, final int paint0, final int paint1) {
        ValueAnimator va = ValueAnimator.ofFloat(0f, 1f);
        va.setDuration(400);
        va.setInterpolator(new DecelerateInterpolator());
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                if (mPositionState == POSITION_STATE_LEFT) {
                    mLeftArcStart = START_ANGLE * start + (end - start) * START_ANGLE * v;
                } else if (mPositionState == POSITION_STATE_RIGHT) {
                    mRightArcStart = DEGREES_90 + START_ANGLE * start - (start - end) * START_ANGLE * v;
                }
                float p = (v / 1.1f);
                int np = (int) (p * 10);
                mPaintArray[paint0].setColor(Color.parseColor(mColors[np]));
                mPaintArray[paint1].setColor(Color.parseColor(mColors[9 - np]));
                invalidate();
            }
        });
        va.start();

    }

    /**
     * 点击时改变指示器的颜色
     *
     * @param cur 当前的限象值index
     * @param pre
     */
    public void onAngleChanged2(int cur, float pre) {
        int index = (int) (pre * 10);
        if (mPositionState == POSITION_STATE_LEFT) {
            if (cur == 0) {
                mLeftArcStart = 5 * START_ANGLE + 28 * START_ANGLE * (1 - pre);
                mPaint0.setColor(Color.parseColor(mColors[index]));
                mPaint2.setColor(Color.parseColor(mColors[9 - index]));
            } else if (cur == 1) {
                mLeftArcStart = START_ANGLE + 2 * START_ANGLE * (1 - pre);
                mPaint1.setColor(Color.parseColor(mColors[index]));
                mPaint0.setColor(Color.parseColor(mColors[9 - index]));
            } else if (cur == 2) {
                mLeftArcStart = 3 * START_ANGLE + 2 * START_ANGLE * (1 - pre);
                mPaint2.setColor(Color.parseColor(mColors[index]));
                mPaint1.setColor(Color.parseColor(mColors[9 - index]));
            }
        } else if (mPositionState == POSITION_STATE_RIGHT) {
            if (cur == 0) {
                mRightArcStart = DEGREES_90 + 3 * START_ANGLE + 2 * START_ANGLE * (1 - pre);
                mPaint0.setColor(Color.parseColor(mColors[index]));
                mPaint1.setColor(Color.parseColor(mColors[9 - index]));
            } else if (cur == 1) {
                if (pre < 0.5) {
                    mRightArcStart = DEGREES_90 - START_ANGLE + 2 * START_ANGLE * (1 - pre) - DEGREES_90 * 2 * pre;
                } else {
                    mRightArcStart = DEGREES_90 + 5 * START_ANGLE + 2 * START_ANGLE * (1 - pre) + DEGREES_90 * 2 * (1 - pre);
                }
                mPaint2.setColor(Color.parseColor(mColors[index]));
                mPaint0.setColor(Color.parseColor(mColors[9 - index]));
            } else if (cur == 2) {
                mRightArcStart = DEGREES_90 + START_ANGLE + 2 * START_ANGLE * (1 - pre);
                mPaint1.setColor(Color.parseColor(mColors[index]));
                mPaint2.setColor(Color.parseColor(mColors[9 - index]));
            }
        }
        invalidate();
    }


}
