package com.well.swipe.tools;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by mingwei on 3/28/16.
 */
public abstract class SwipeTools {
    /**
     * 根据状态获取icon
     *
     * @param context
     * @return
     */
    public abstract BitmapDrawable getDrawableState(Context context);

    /**
     * 根据状态获取title
     *
     * @param context
     * @return
     */
    public abstract String getTitleState(Context context);
}
