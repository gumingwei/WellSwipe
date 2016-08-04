package com.well.swipe;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by mingwei on 3/16/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class ItemInfo {

    /**
     * item类型
     */
    int mType;
    /**
     * Item的名字
     */
    public CharSequence mTitle;

    /**
     * Item的顺序索引
     */
    int mIndex;


    ItemInfo() {

    }

    ItemInfo(ItemInfo info) {
        mType = info.mType;
        mTitle = info.mTitle;
        mIndex = info.mIndex;
    }

    static String getPackageName(Intent intent) {
        if (intent != null) {
            String packageName = intent.getPackage();
            if (packageName == null && intent.getComponent() != null) {
                return intent.getComponent().getPackageName();
            }
            if (packageName != null) {
                return packageName;
            }
        }
        return "";
    }

    void onAddToDatabase(ContentValues values) {
        values.put(SwipeSettings.BaseColumns.ITEM_TYPE, mType);
        values.put(SwipeSettings.BaseColumns.ITEM_TITLE, mTitle.toString());
        values.put(SwipeSettings.BaseColumns.ITEM_INDEX, mIndex);
    }

    /**
     * 把图片保存到sqlite
     *
     * @param bitmap
     * @return
     */
    static byte[] flattenBitmap(Bitmap bitmap) {
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * bitmap转换成二进制然后放在数据库
     *
     * @param values
     * @param bitmap
     */
    static void writeBitmap(ContentValues values, Bitmap bitmap) {
        if (bitmap != null) {
            byte[] data = flattenBitmap(bitmap);
            values.put(SwipeSettings.BaseColumns.ITEM_ICON, data);
        }
    }
}
