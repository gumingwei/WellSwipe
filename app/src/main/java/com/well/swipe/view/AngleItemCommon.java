package com.well.swipe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by mingwei on 3/19/16.
 */
public abstract class AngleItemCommon extends RelativeLayout {

    public AngleItemCommon(Context context) {
        this(context, null);
    }

    public AngleItemCommon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AngleItemCommon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
