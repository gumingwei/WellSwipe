package com.well.swipe;

import android.content.ContentResolver;
import android.content.Context;

/**
 * Created by mingwei on 3/16/16.
 */
public class ItemSwipeSwitch extends ItemInfo {
    /**
     * 快捷开关的action
     */
    String mAction;

    ItemSwipeSwitch() {
        mType = SwipeSettings.BaseColumns.ITEM_TYPE_SWITCH;
    }

    public ItemSwipeSwitch(ItemSwipeSwitch switchitem) {
        super(switchitem);
        mAction = switchitem.mAction;
    }

    public int delete(Context context) {
        ContentResolver resolver = context.getContentResolver();
        return resolver.delete(SwipeSettings.Favorites.CONTENT_URI, SwipeSettings.BaseColumns.ITEM_ACTION +
                "=?", new String[]{mAction});
    }

}
