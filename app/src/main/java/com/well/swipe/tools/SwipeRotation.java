package com.well.swipe.tools;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.Settings;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/27/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class SwipeRotation {

    public static int getRotationStatus(Context context) {
        int status = 0;
        try {
            status = android.provider.Settings.System.getInt(context.getContentResolver(),
                    android.provider.Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return status;
    }

    /**
     * 设置
     *
     * @param resolver
     * @param status
     */
    public static void setRotationStatus(ContentResolver resolver, int status) {
        //得到uri
        Uri uri = android.provider.Settings.System.getUriFor("accelerometer_rotation");
        //沟通设置status的值改变屏幕旋转设置
        android.provider.Settings.System.putInt(resolver, "accelerometer_rotation", status);
        //通知改变
        resolver.notifyChange(uri, null);
    }

    /**
     * 获取图片
     *
     * @param context
     * @return
     */
    public static BitmapDrawable getDrawableState(Context context) {
        if (getRotationStatus(context) == 0) {
            return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_auto_orientation_off);
        } else {
            return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_auto_orientation_on);
        }
    }
}
