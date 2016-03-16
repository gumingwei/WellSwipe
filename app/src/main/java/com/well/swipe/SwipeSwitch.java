package com.well.swipe;

/**
 * Created by mingwei on 3/16/16.
 */
public class SwipeSwitch extends ItemInfo {
    /**
     * 快捷开关的action
     */
    String mAction;

    SwipeSwitch() {
        mType = SwipeSettings.Favorites.ITEM_TYPE_SWITCH;
    }

    public SwipeSwitch(SwipeSwitch switchitem) {
        super(switchitem);
        mAction = switchitem.mAction;
    }

}
