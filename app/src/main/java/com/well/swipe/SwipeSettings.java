package com.well.swipe;

import android.net.Uri;

/**
 * Created by mingwei on 3/15/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class SwipeSettings {

    public static class BaseColumns {

        public static final String ITEM_INDEX = "item_index";

        public static final String ITEM_TITLE = "item_title";

        public static final String ITEM_URI = "item_uri";

        public static final String ITEM_INTENT = "item_intent";

        public static final String ITEM_TYPE = "item_type";

        public static final int ITEM_TYPE_APPLICATION = 1;

        public static final int ITEM_TYPE_SWITCH = 2;

        public static final String ITEM_ACTION = "item_action";

        public static final String ITEM_ICON = "item_icon";

        public static final String ICON_TYPE = "icon_type";

        public static final String ICON_PACKAGENAME = "icon_package";

        public static final String ICON_RESOURCE = "icon_resource";

        public static final String ICON_BITMAP = "icon_bitmap";

        public static final int ICON_TYPE_RESOURCE = 0;

        public static final int ICON_TYPE_BITMAP = 1;

    }

    public static class Favorites {

        public static final Uri CONTENT_URI = Uri.parse("content://" + SwipefreeProvider.AUTHORITY + "/" + SwipefreeProvider.TABLE_FAVORITES);

        public static final Uri CONTENT_URI_WHITELIST = Uri.parse("content://" + SwipefreeProvider.AUTHORITY + "/" + SwipefreeProvider.TABLE_WHITELIST);

    }
}
