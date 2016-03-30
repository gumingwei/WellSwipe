package com.well.swipe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/29/16.
 */
public class AngleIndicatorViewTheme extends PositionStateView {

    private Paint mPaint;

    private int mColor;

    private int mWidth;

    private int mHeight;

    private int mDistance;

    private int mIndicatorInnerSize;

    public AngleIndicatorViewTheme(Context context) {
        this(context, null);
    }

    public AngleIndicatorViewTheme(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AngleIndicatorViewTheme(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDistance = getResources().getDimensionPixelSize(R.dimen.angleview_indicatorview_distance);
        mIndicatorInnerSize = getResources().getDimensionPixelSize(R.dimen.angleindicator_arc_inner_size);
        mColor = getResources().getColor(R.color.indicator_arc_background);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPositionState == PositionState.POSITION_STATE_LEFT) {
            mPaint.setColor(mColor);
            canvas.drawCircle(0, mHeight, mHeight, mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
            mPaint.setColor(Color.TRANSPARENT);
            canvas.drawCircle(0, mHeight, mIndicatorInnerSize, mPaint);
        } else if (mPositionState == PositionState.POSITION_STATE_RIGHT) {
            mPaint.setColor(mColor);
            canvas.drawCircle(mWidth, mHeight, mHeight, mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
            mPaint.setColor(Color.TRANSPARENT);
            canvas.drawCircle(mWidth, mHeight, mIndicatorInnerSize, mPaint);
        }
        mPaint.setXfermode(null);
    }

    @Override
    public void setPositionState(int state) {
        super.setPositionState(state);
    }
}
