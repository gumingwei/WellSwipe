package com.well.swipe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/4/16.
 * AngleLayout的指示器
 */
public class IndicatorView extends View {

    private Paint mPaint0 = new Paint();

    private Paint mPaint1 = new Paint();

    private Paint mPaint2 = new Paint();

    private String mColors[] = new String[]{"#ffff00", "#ffcc00", "#ff9900", "#ff6600", "#ff3300",
            "#ff0000", "#cc3300", "#cc0000", "#993300", "#990000"};

    private int LEFT_OFFSET_X = 125;

    private int RIGHT_OFFSET_X = 25;

    private int OFFSET_Y = 10;

    private int STATE = STATE_LEFT;

    public static final int STATE_LEFT = 1;

    public static final int STATE_RIGHT = 2;

    private int mWidth;


    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint0.setColor(Color.parseColor(mColors[9]));
        mPaint0.setTextSize(30);
        mPaint0.setAntiAlias(true);

        mPaint1.setColor(Color.parseColor(mColors[9]));
        mPaint1.setTextSize(30);
        mPaint1.setAntiAlias(true);

        mPaint2.setColor(Color.parseColor(mColors[9]));
        mPaint2.setTextSize(30);
        mPaint2.setAntiAlias(true);

        if (STATE == STATE_LEFT) {
            setRotation(-90);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int degree = 90 / 3;
        if (STATE == STATE_LEFT) {
            canvas.save();
            canvas.rotate(degree * 0.5f, 0, 0);
            canvas.drawText(getResources().getString(R.string.recent), LEFT_OFFSET_X, OFFSET_Y, mPaint0);
            canvas.rotate(degree, 0, 0);
            canvas.drawText(getResources().getString(R.string.toolbox), LEFT_OFFSET_X, OFFSET_Y, mPaint1);
            canvas.rotate(degree, 0, 0);
            canvas.drawText(getResources().getString(R.string.frequent), LEFT_OFFSET_X, OFFSET_Y, mPaint2);
            canvas.restore();
        } else if (STATE == STATE_RIGHT) {
            canvas.save();
            canvas.rotate(-degree * 0.5f, mWidth, 0);
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

    }

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


    public void setSTATE(int STATE) {
        this.STATE = STATE;
        if (STATE == STATE_RIGHT) {
            setRotationY(180);
            setRotation(90);
        }
        invalidate();
    }

    /**
     * 顺时针时刷新颜色
     *
     * @param cur
     * @param pre
     */
    public void onForward(int cur, float pre) {
        int index = (int) (pre * 10);
        if (cur == 0) {
            mPaint0.setColor(Color.parseColor(mColors[index]));
            mPaint2.setColor(Color.parseColor(mColors[9 - index]));
        } else if (cur == 1) {
            mPaint1.setColor(Color.parseColor(mColors[index]));
            mPaint0.setColor(Color.parseColor(mColors[9 - index]));
        } else if (cur == 2) {
            mPaint2.setColor(Color.parseColor(mColors[index]));
            mPaint1.setColor(Color.parseColor(mColors[9 - index]));
        }
        invalidate();
    }

    /**
     * 逆时针时刷新颜色
     *
     * @param cur
     * @param pre
     */
    public void onReverse(int cur, float pre) {
        int index = Math.abs((int) (pre * 10));
        if (cur == 0) {
            mPaint0.setColor(Color.parseColor(mColors[index]));
            mPaint1.setColor(Color.parseColor(mColors[9 - index]));
        } else if (cur == 1) {
            mPaint1.setColor(Color.parseColor(mColors[index]));
            mPaint2.setColor(Color.parseColor(mColors[9 - index]));
        } else if (cur == 2) {
            mPaint2.setColor(Color.parseColor(mColors[index]));
            mPaint0.setColor(Color.parseColor(mColors[9 - index]));
        }
        invalidate();
    }

}
