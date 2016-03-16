package com.well.swipe.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.util.DisplayMetrics;


import com.well.swipe.R;

import java.lang.reflect.Field;

/**
 * Created by mingwei on 2/26/16.
 */
public class Utils {
    

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 状态栏的高度
     *
     * @param c
     * @return
     */
    public static int getStatusBarHeight(Context c) {
        int h = 0;
        try {
            Class<?> z = Class.forName("com.android.internal.R$dimen");
            Object o = z.newInstance();
            Field f = z.getField("status_bar_height");
            int x = (Integer) f.get(o);
            h = c.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return h;
    }

}
