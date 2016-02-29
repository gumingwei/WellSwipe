package com.well.swipe.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

/**
 * Created by mingwei on 2/25/16.
 */
public class FanMum extends ViewGroup {

    private Paint mTestPaint;
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
     * 转轴X,Y
     */
    private int mPivotX = 0;

    private int mPivotY = 0;

    static int LEFT = 1;

    static int RIGHT = 2;

    public int POSITION = RIGHT;

    /**
     * 顺时针/逆时针
     */
    public int WHIRLING_STATE = WHIRLING_STATE_REST;

    public static final int WHIRLING_STATE_REST = 0;

    public static final int WHIRLING_STATE_ALONG = 1;

    public static final int WHIRLING_STATE_INVERSE = 2;

    public static final int DEGREES_360 = 360;
    /**
     * 单位度数
     */
    public static final int DEGREES_90 = 90;
    /**
     * 判定范围
     */
    public static final int OFFSET_DEGREES = 25;


    public FanMum(Context context) {
        this(context, null);
    }

    public FanMum(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FanMum(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTestPaint = new Paint();
        mTestPaint.setColor(Color.BLUE);
        mTestPaint.setAntiAlias(true);
        mTestPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < 4; i++) {
            TextView item = new TextView(context);
            item.setText("item=" + i);
            addView(item);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mPivotX = getMeasuredWidth();
        mPivotY = getMeasuredHeight();

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (POSITION == LEFT) {
            getChildAt(0).layout(200, 200, 300, 300);
            getChildAt(1).layout(200, 800, 300, 900);
            getChildAt(2).layout(-300, 800, -200, 900);
            getChildAt(3).layout(-300, 200, -200, 300);
        } else {
            getChildAt(0).layout(200, 200, 300, 300);
            getChildAt(1).layout(200, 800, 300, 900);
            getChildAt(2).layout(800, 800, 900, 900);
            getChildAt(3).layout(800, 200, 900, 300);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        if (POSITION == LEFT) {
            canvas.rotate(mBaseAngle + mChangeAngle, 0, mPivotY);
        } else {
            canvas.rotate(mBaseAngle + mChangeAngle, mPivotX, mPivotY);
        }
        super.dispatchDraw(canvas);
        canvas.restore();
    }
    
    /**
     * 手指按下时的初始角度
     */
    public void downAngle(float x, float y) {
        if (POSITION == LEFT) {
            mDownAngle = Math.toDegrees(Math.atan(x / y));
        } else {
            mDownAngle = DEGREES_360 - Math.toDegrees(Math.atan(x / y));
        }
        WHIRLING_STATE = WHIRLING_STATE_REST;

    }

    /**
     * 手指滑动时
     * 动态的计算一个变化量diff，在根据条件合理的计算
     *
     * @param x
     * @param y
     */
    public void changeAngle(float x, float y) {
        double diffrotation;
        double rotation;
        if (POSITION == LEFT) {
            rotation = Math.toDegrees(Math.atan(x / y));
        } else {
            rotation = DEGREES_360 - Math.toDegrees(Math.atan(x / y));
        }
        diffrotation = rotation - mDownAngle;
        if (diffrotation > 0) {
            WHIRLING_STATE = FanMum.WHIRLING_STATE_ALONG;
        } else {
            WHIRLING_STATE = FanMum.WHIRLING_STATE_INVERSE;
        }
        changeAngle(diffrotation);

    }

    /**
     * 改变mChangeAngle的值
     *
     * @param rotation
     */
    private void changeAngle(double rotation) {
        mChangeAngle = (float) rotation;
        invalidate();
    }

    public void upAngle() {
        if (POSITION == LEFT) {
            if (WHIRLING_STATE == FanMum.WHIRLING_STATE_ALONG) {
                along();
            } else {
                inverse();
            }
        } else {
            if (WHIRLING_STATE == FanMum.WHIRLING_STATE_ALONG) {
                along();
            } else {
                inverse();
            }
        }

    }

    /**
     * 顺时针旋转
     */
    public void along() {
        if (getAngleValues() % FanMum.DEGREES_90 > 0 && getAngleValues() %
                FanMum.DEGREES_90 < FanMum.OFFSET_DEGREES) {
            autoWhirling(getAngleValues(), ((int) (getAngleValues() / FanMum.DEGREES_90)) * FanMum.DEGREES_90);
        } else {
            autoWhirling(getAngleValues(), ((int) (getAngleValues() / FanMum.DEGREES_90) + 1) * FanMum.DEGREES_90);
        }
    }


    /**
     * 逆时针旋转
     */
    public void inverse() {
        if ((DEGREES_360 - getAngleValues()) % FanMum.DEGREES_90 > 0 && (DEGREES_360 - getAngleValues()) %
                FanMum.DEGREES_90 < FanMum.OFFSET_DEGREES) {
            autoWhirling(getAngleValues(), ((int) (getAngleValues() / FanMum.DEGREES_90) + 1) * FanMum.DEGREES_90);
        } else {
            autoWhirling(getAngleValues(), ((int) (getAngleValues() / FanMum.DEGREES_90)) * FanMum.DEGREES_90);
        }
    }

    /**
     * 顺时针自动旋转
     */
    public void flingAlong() {
        autoWhirling(getAngleValues(), ((int) (getAngleValues() / FanMum.DEGREES_90) + 1) * FanMum.DEGREES_90);
    }

    /**
     * 逆时针自动转
     */
    public void flingInvrse() {
        autoWhirling(getAngleValues(), ((int) (getAngleValues() / FanMum.DEGREES_90)) * FanMum.DEGREES_90);
    }

    public void autoWhirling(float start, float end) {
        mChangeAngle = 0;
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(400);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mBaseAngle = value;
                mBaseAngle = mBaseAngle % DEGREES_360;
                invalidate();
            }
        });
        animator.start();
    }


    public float getAngleValues() {
        float newrotation = (mBaseAngle + mChangeAngle);
        return newrotation < 0 ? DEGREES_360 + (newrotation) : (newrotation);
    }
}
