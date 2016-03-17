package com.well.swipe;

import android.net.Uri;

/**
 * Created by mingwei on 3/15/16.
 */
public class SwipeSettings {

    static class BaseColumns {

        static final String ITEM_INDEX = "item_index";

        static final String ITEM_TITLE = "item_title";

        static final String ITEM_URI = "item_uri";

        static final String ITEM_INTENT = "item_intent";

        static final String ITEM_TYPE = "item_type";

        static final int ITEM_TYPE_APPLICATION = 1;

        static final int ITEM_TYPE_SWITCH = 2;

        static final String ITEM_ACTION = "item_action";

        static final String ITEM_ICON = "item_icon";

        static final String ICON_TYPE = "icon_type";

        static final String ICON_PACKAGENAME = "icon_package";

        static final String ICON_RESOURCE = "icon_resource";

        static final String ICON_BITMAP = "icon_bitmap";

        static final int ICON_TYPE_RESOURCE = 0;

        static final int ICON_TYPE_BITMAP = 1;


    }

    static class Favorites {

        static final Uri CONTENT_URI = Uri.parse("content://" + SwipeProvider.AUTHORITY + "/" + SwipeProvider.TABLE_FAVORITES);

    }
}
