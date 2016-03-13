package com.well.swipe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by mingwei on 3/13/16.
 */
public class TextLayout extends LinearLayout {

    public TextLayout(Context context) {
        this(context, null);
    }

    public TextLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.rotate(45, 0, 100);

        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        //canvas.save();
        //canvas.rotate(45, 0, 100);
        //canvas.restore();
        return super.drawChild(canvas, child, drawingTime);
    }
}
