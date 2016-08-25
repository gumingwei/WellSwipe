package com.well.swipe.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import com.well.swipe.R;
import com.well.swipecomm.view.PositionState;
import com.well.swipecomm.view.PositionStateView;

/**
 * Created by mingwei on 3/4/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 * AngleLayout的指示器
 */
public class AngleIndicatorView extends PositionStateView {

    private TextPaint mPaint0 = new TextPaint();

    private TextPaint mPaint1 = new TextPaint();

    private TextPaint mPaint2 = new TextPaint();

    private Paint[] mPaintArray = new Paint[]{mPaint0, mPaint1, mPaint2};

    private String mColors[] = new String[]{"#ffffff", "#eaeaea", "#e1e1e1", "#d6d6d6", "#cac9c9",
            "#bfbebe", "#b6b5b5", "#aeadad", "#a5a4a4", "#9c9c9c"};

    private Paint mArcPaint;

    private Paint mInnerPaint;

    private int mLeftOffset;

    private int mRightOffset;

    private int OFFSET_Y;

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

    private int mIndicatorWidth;

    private int mIndicatorTextSize;

    public int mRect;

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
        mLeftOffset = getResources().getDimensionPixelSize(R.dimen.angleindicator_text_paddingleft);
        mRightOffset = getResources().getDimensionPixelOffset(R.dimen.angleindicator_text_paddingright);
        mRect = getResources().getDimensionPixelSize(R.dimen.angleindicator_arc_rect);
        mIndicatorWidth = getResources().getDimensionPixelSize(R.dimen.angleindicator_arc_width);
        mIndicatorTextSize = getResources().getDimensionPixelSize(R.dimen.angleindicator_arc_textsize);
        OFFSET_Y = getResources().getDimensionPixelSize(R.dimen.angleindicator_arc_textsize_offset_y);
        mPaint0.setColor(Color.parseColor(mColors[9]));
        mPaint0.setTextSize(mIndicatorTextSize);
        mPaint0.setAntiAlias(true);

        mPaint1.setColor(Color.parseColor(mColors[9]));
        mPaint1.setTextSize(mIndicatorTextSize);
        mPaint1.setAntiAlias(true);

        mPaint2.setColor(Color.parseColor(mColors[9]));
        mPaint2.setTextSize(mIndicatorTextSize);
        mPaint2.setAntiAlias(true);

        mArcPaint = new Paint();
        //mArcPaint.setColor(Color.WHITE);
        mArcPaint.setColor(getResources().getColor(R.color.indicator_theme_purple_selector));
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStrokeWidth(mIndicatorWidth);

        mInnerPaint = new Paint();
        mInnerPaint.setColor(getResources().getColor(R.color.indicator_theme_purple));
        mInnerPaint.setStyle(Paint.Style.FILL);
        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setStrokeWidth(5);

        if (mPositionState == PositionState.POSITION_STATE_LEFT) {
            setRotation(-90);
        }
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int degree = DEGREES_90 / 4;
        if (mPositionState == PositionState.POSITION_STATE_LEFT) {
            canvas.save();
            canvas.rotate(degree, 0, 0);
            mPaint0.setTextAlign(Paint.Align.LEFT);
            mPaint1.setTextAlign(Paint.Align.LEFT);
            mPaint2.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(getResources().getString(R.string.recent), mLeftOffset, OFFSET_Y, mPaint0);
            canvas.rotate(degree, 0, 0);
            canvas.drawText(getResources().getString(R.string.toolbox), mLeftOffset, OFFSET_Y, mPaint1);
            canvas.rotate(degree, 0, 0);
            canvas.drawText(getResources().getString(R.string.frequent), mLeftOffset, OFFSET_Y, mPaint2);
            canvas.restore();
        } else if (mPositionState == PositionState.POSITION_STATE_RIGHT) {
            canvas.save();
            canvas.rotate(-degree, mWidth, 0);
            mPaint0.setTextAlign(Paint.Align.RIGHT);
            mPaint1.setTextAlign(Paint.Align.RIGHT);
            mPaint2.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(getResources().getString(R.string.recent), mRightOffset, OFFSET_Y, mPaint0);
            canvas.rotate(-degree, mWidth, 0);
            canvas.drawText(getResources().getString(R.string.toolbox), mRightOffset, OFFSET_Y, mPaint1);
            canvas.rotate(-degree, mWidth, 0);
            canvas.drawText(getResources().getString(R.string.frequent), mRightOffset, OFFSET_Y, mPaint2);
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
        for (int i = 0; i < mPaintArray.length; i++) {
            if (i == current) {
                mPaintArray[i].setColor(Color.parseColor(mColors[0]));
            } else {
                mPaintArray[i].setColor(Color.parseColor(mColors[9]));
            }
        }
        invalidate();
    }

    /**
     * 设置是左还是右
     *
     * @param state
     */
    public void setPositionState(int state) {
        super.setPositionState(state);
        if (state == PositionState.POSITION_STATE_LEFT) {
            setRotation(-DEGREES_90);
        } else if (state == PositionState.POSITION_STATE_RIGHT) {
            setRotation(DEGREES_90);
        }
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
                    if (mPositionState == PositionState.POSITION_STATE_LEFT) {
                        degree = Math.toDegrees(Math.atan(newy / newx));
                    } else if (mPositionState == PositionState.POSITION_STATE_RIGHT) {
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

    public void setCurrentIndex(int index) {
        if (mPositionState == PositionState.POSITION_STATE_LEFT) {
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
        } else if (mPositionState == PositionState.POSITION_STATE_RIGHT) {
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
                if (mPositionState == PositionState.POSITION_STATE_LEFT) {
                    mLeftArcStart = START_ANGLE * start + (end - start) * START_ANGLE * v;
                } else if (mPositionState == PositionState.POSITION_STATE_RIGHT) {
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
        if (mPositionState == PositionState.POSITION_STATE_LEFT) {
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
        } else if (mPositionState == PositionState.POSITION_STATE_RIGHT) {
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
