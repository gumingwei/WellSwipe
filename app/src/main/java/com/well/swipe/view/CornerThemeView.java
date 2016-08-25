package com.well.swipe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.well.swipe.R;
import com.well.swipecomm.view.PositionStateView;

/**
 * Created by mingwei on 4/25/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class CornerThemeView extends PositionStateView {

    private Paint mPaint;

    private int mHeight;

    private int mWidth;

    private int mInnerSize;

    private int mGreen = getResources().getColor(R.color.GreenColor);

    private int mWarningColor = getResources().getColor(R.color.warning_color);

    public CornerThemeView(Context context) {
        this(context, null);
    }

    public CornerThemeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerThemeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInnerSize = context.getResources().getDimensionPixelSize(R.dimen.anglelogo_size);
        mPaint = new Paint();
        mPaint.setColor(mGreen);
        mPaint.setAntiAlias(true);
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
        if (isLeft()) {
            canvas.drawCircle(0, mHeight, mInnerSize, mPaint);
        } else if (isRight()) {
            canvas.drawCircle(mWidth, mHeight, mInnerSize, mPaint);
        }
    }

    public void setWarnColor() {
        mPaint.setColor(mWarningColor);
        invalidate();
    }

    public void setGreenColor() {
        mPaint.setColor(mGreen);
        invalidate();
    }

}
