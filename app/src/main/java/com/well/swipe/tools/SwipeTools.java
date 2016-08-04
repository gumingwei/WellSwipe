package com.well.swipe.tools;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by mingwei on 3/28/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
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
