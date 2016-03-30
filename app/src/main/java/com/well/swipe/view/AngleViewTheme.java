package com.well.swipe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/29/16.
 */
public class AngleViewTheme extends PositionStateView {

    private Paint mPaint;

    private int mColor;

    private int mWidth;

    private int mHeight;
    /**
     * 背景环的内部值，为了跟Indicator接近，直接去IndicatorView的变长
     */
    private int mInnerSize;

    private int mDistance;

    private static final Xfermode[] sModes = {new PorterDuffXfermode(PorterDuff.Mode.CLEAR),
            new PorterDuffXfermode(PorterDuff.Mode.SRC), new PorterDuffXfermode(PorterDuff.Mode.DST),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER), new PorterDuffXfermode(PorterDuff.Mode.DST_OVER),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_IN), new PorterDuffXfermode(PorterDuff.Mode.DST_IN),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT), new PorterDuffXfermode(PorterDuff.Mode.DST_OUT),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP), new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP),
            new PorterDuffXfermode(PorterDuff.Mode.XOR), new PorterDuffXfermode(PorterDuff.Mode.DARKEN),
            new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN), new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY),
            new PorterDuffXfermode(PorterDuff.Mode.SCREEN)};

    public AngleViewTheme(Context context) {
        this(context, null);
    }

    public AngleViewTheme(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AngleViewTheme(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInnerSize = getResources().getDimensionPixelSize(R.dimen.angleindicator_size);
        mDistance = getResources().getDimensionPixelSize(R.dimen.angleview_indicatorview_distance);
        mColor = getResources().getColor(R.color.angleview_arc_background);
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
        mPaint.setColor(mColor);
        if (mPositionState == PositionState.POSITION_STATE_LEFT) {
            canvas.drawCircle(0, mHeight, mHeight, mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
            mPaint.setColor(mColor);
            canvas.drawCircle(0, mHeight, mInnerSize + mDistance, mPaint);
        } else if (mPositionState == PositionState.POSITION_STATE_RIGHT) {
            canvas.drawCircle(mWidth, mHeight, mHeight, mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
            mPaint.setColor(mColor);
            canvas.drawCircle(mWidth, mHeight, mInnerSize + mDistance, mPaint);
        }
        mPaint.setXfermode(null);
    }
}
