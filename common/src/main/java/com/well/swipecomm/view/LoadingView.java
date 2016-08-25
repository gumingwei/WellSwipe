package com.well.swipecomm.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.well.swipecomm.R;

/**
 * Created by mingwei on 5/31/16.
 * <p/>
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 */
public class LoadingView extends View {

    private final String TAG = "LoadingView";
    private Paint mTestPaint;
    private Paint mOuterPaint;
    private RectF mOuterRectF;
    private Paint mInnerPaint;
    private RectF mInnerRectF;
    /**
     * mStart 起始值 弧度 mSweep 终点值 弧度
     */
    int mStart = 0;
    int mSweep = 90;

    int mWidth, mHeight;
    int mArcLenght = 60;
    /**
     * outer width
     */
    int mOuterWidth;
    /**
     * inner width
     */
    int mInnerWidth;
    /**
     * outer color
     */
    int mOuterColor;
    /**
     * inner color
     */
    int mInnerColor;
    /**
     * inner rotating speed
     */
    int mInnerRotatingSpeed = 1;
    /**
     * Circle degree
     */
    int mCircle = 0;
    /**
     * Circle speed,only mode==circle
     */
    int mCircleSpeed = 1;
    /**
     * custom degree,only mode==custom
     */
    int mCustomDegree = 0;

    private boolean isStop;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);

    }

    private void setBounds() {
        mOuterRectF = new RectF(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight()
                - getPaddingBottom());
        mInnerRectF = new RectF(mOuterRectF);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setBounds();
        initPaint();
    }

    private void initPaint() {
        mOuterPaint = new Paint();
        mOuterPaint.setAntiAlias(true);
        mOuterPaint.setColor(mOuterColor);
        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setStrokeWidth(mOuterWidth);
        //
        mInnerPaint = new Paint();
        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setColor(mInnerColor);
        mInnerPaint.setStyle(Paint.Style.STROKE);
        mInnerPaint.setStrokeWidth(mInnerWidth);
        //
        mTestPaint = new Paint();
        mTestPaint.setAntiAlias(true);
        mTestPaint.setColor(Color.BLACK);
        mTestPaint.setStyle(Paint.Style.STROKE);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Loading);
        mOuterWidth = array.getDimensionPixelOffset(R.styleable.Loading_outer_width, 0);
        mOuterColor = array.getColor(R.styleable.Loading_outer_color, Color.GRAY);
        mInnerWidth = array.getDimensionPixelOffset(R.styleable.Loading_inner_width, R.styleable.Loading_inner_width);
        mInnerColor = array.getColor(R.styleable.Loading_inner_color, Color.BLACK);
        mInnerRotatingSpeed = array.getInt(R.styleable.Loading_inner_rotating_speed, 1);
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // canvas.drawRect(mOuterRectF, mTestPaint);
        canvas.drawArc(mOuterRectF, 360, 360, false, mOuterPaint);
        canvas.drawArc(mInnerRectF, mStart, mSweep + 2, false, mInnerPaint);
        // int d = mRandom.nextInt(8);
        mStart += mInnerRotatingSpeed;
        if (mStart > 360) {
            mStart -= 360;
        }
        if (!isStop) {
            invalidate();
        }

    }

    public void stop() {
        isStop = true;
        setVisibility(GONE);
    }

}
