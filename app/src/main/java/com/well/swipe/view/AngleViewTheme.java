package com.well.swipe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;

import com.well.swipe.R;
import com.well.swipecomm.view.PositionStateView;

/**
 * Created by mingwei on 3/29/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
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
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColor);
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
        if (isLeft()) {
            canvas.drawCircle(0, mHeight, mHeight, mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));

            canvas.drawCircle(0, mHeight, mInnerSize + mDistance, mPaint);
            canvas.drawCircle(0, mHeight, mInnerSize, mPaint);
            mPaint.setXfermode(null);
        } else if (isRight()) {
            canvas.drawCircle(mWidth, mHeight, mHeight, mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));

            canvas.drawCircle(mWidth, mHeight, mInnerSize + mDistance, mPaint);
            canvas.drawCircle(mWidth, mHeight, mInnerSize, mPaint);
            mPaint.setXfermode(null);
        }

    }
}
