package com.well.swipecomm.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.well.swipecomm.R;
import com.well.swipecomm.view.SwipeToast;

import java.lang.reflect.Field;

/**
 * Created by mingwei on 3/9/16.
 * <p/>
 * <p/>
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
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

    /**
     * 判断程序是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isApkInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            @SuppressWarnings("unused")
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMarketUrl(String packageName) {
        String marketUrl = "market://details?id=" + packageName;
        return marketUrl;
    }

    public static void swipeToast(Context context, String text) {
        SwipeToast toastview = (SwipeToast) LayoutInflater.from(context).inflate(R.layout.swipe_toast, null);
        toastview.setHtmlText(text);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setView(toastview);
        toast.show();
    }

}
