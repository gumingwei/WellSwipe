package com.well.swipe;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by mingwei on 3/16/16.
 */
public class ItemSwipeTools extends ItemInfo {
    /**
     * 快捷开关的action
     */
    public String mAction;

    public Bitmap mDefaultIcon;

    public boolean isChecked;

    ItemSwipeTools() {
        mType = SwipeSettings.BaseColumns.ITEM_TYPE_SWITCH;
    }

    public ItemSwipeTools(ItemSwipeTools switchitem) {
        super(switchitem);
        mAction = switchitem.mAction;
        mDefaultIcon = switchitem.mDefaultIcon;
    }

    public int delete(Context context) {
        ContentResolver resolver = context.getContentResolver();
        return resolver.delete(SwipeSettings.Favorites.CONTENT_URI, SwipeSettings.BaseColumns.ITEM_ACTION +
                "=?", new String[]{mAction});
    }

    public void insert(Context context, int index) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(SwipeSettings.BaseColumns.ITEM_TITLE, mTitle.toString());
        values.put(SwipeSettings.BaseColumns.ITEM_INDEX, index);
        values.put(SwipeSettings.BaseColumns.ITEM_TYPE, SwipeSettings.BaseColumns.ITEM_TYPE_SWITCH);
        values.put(SwipeSettings.BaseColumns.ITEM_ACTION, mAction);
        resolver.insert(SwipeSettings.Favorites.CONTENT_URI, values);
    }


}
