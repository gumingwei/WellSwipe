package com.well.swipe;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by mingwei on 3/16/16.
 */
public class ItemInfo {

    /**
     * item类型
     */
    int mType;
    /**
     * Item的名字
     */
    CharSequence mTitle;

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
        values.put(SwipeSettings.Favorites.ITEM_TYPE, mType);
        values.put(SwipeSettings.Favorites.ITEM_TITLE, mTitle.toString());
        values.put(SwipeSettings.Favorites.ITEM_INDEX, mIndex);
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
            Log.w("Favorite", "Could not write icon");
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
            values.put(SwipeSettings.Favorites.ITEM_ICON, data);
        }
    }
}
