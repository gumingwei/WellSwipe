package com.well.swipe.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

/**
 * Created by mingwei on 2/25/16.
 */
public class FanMum extends ViewGroup {

    private Paint mTestPaint;

    private float mCurrentRotation = 0;

    /**
     * 转轴X,Y
     */
    private int mPivotX = 0;

    private int mPivotY = 0;

    static int LEFT = 1;

    static int RIGHT = 2;

    int POSITION = LEFT;
    /**
     * 单位度数
     */
    public static final int UNIT_DEGREES = 90;
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
        for (int i = 0; i < 5; i++) {
            TextView item = new TextView(context);
            item.setText("item=" + i);
            addView(item);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mPivotY = getMeasuredWidth();
        Log.i("Gmw", "onMeasure=" + getMeasuredWidth() + "," + getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        getChildAt(0).layout(0, 0, 100, 100);
        getChildAt(1).layout(-100, 0, 0, 100);
        getChildAt(2).layout(-100, -100, 0, 0);
        getChildAt(3).layout(0, -100, 100, 100);
        getChildAt(4).layout(0, getMeasuredHeight() - 100, 100, getMeasuredHeight());
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
        canvas.rotate(mCurrentRotation, 0, mPivotY);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void test(double ratation) {
//        ValueAnimator animator = ValueAnimator.ofInt(0, 360);
//        animator.setRepeatMode(ValueAnimator.RESTART);
//        animator.setRepeatCount(Integer.MAX_VALUE);
//        animator.setInterpolator(new LinearInterpolator());
//        animator.setDuration(10000);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                int values = (int) animation.getAnimatedValue();
//                mCurrentRotation = values;
//                invalidate();
//            }
//        });
//        animator.start();
        mCurrentRotation = 30;
        invalidate();

    }

    public void refresh(double ratation) {
        if (ratation < 0) {
            mCurrentRotation = (float) (360 + ratation);
        } else {
            mCurrentRotation = (float) ratation;
        }
        invalidate();
    }

    public double getRitation() {
        return mCurrentRotation;
    }
}
