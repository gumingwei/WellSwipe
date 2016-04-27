package com.well.swipe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.well.swipe.R;

/**
 * Created by mingwei on 4/25/16.
 */
public class CornerThemeView extends PositionStateView {

    private Paint mPaint;

    private int mHeight;

    private int mWidth;

    private int mInnerSize;

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
        mPaint.setColor(getResources().getColor(R.color.GreenColor));
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
}
