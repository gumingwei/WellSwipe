package com.well.swipe;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.SweepGradient;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

/**
 * Created by mingwei on 3/14/16.
 */
public class SwipeProvider extends ContentProvider {

    private DatabaseHelper mDatabaseHelper;

    public SwipeProvider() {
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        ((SwipeApplication) getContext()).setProvider(this);
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    public static void checkInsert(DatabaseHelper helper, SQLiteDatabase database, String table,
                                   ContentValues values) {
        database.insert(table, null, values);

    }

    synchronized public void loadDefaultFavoritesIfNecessary(int resId) {
        //mDatabaseHelper.printAllAppList();
        Log.i("Gmw", "loadDefaultFavoritesIfNecessary");
        mDatabaseHelper.loadFavorites(mDatabaseHelper.getWritableDatabase(), resId);

    }

    public class DatabaseHelper extends SQLiteOpenHelper {

        private final String TAG_FAORITES = "favorites";

        private final String TAG_FAORITE = "favorite";

        private final String TAG_QUICKSWITCH = "quicksswitch";

        Context mContext;

        private static final String DATEBASE_NAME = "wellswipe.db";

        private static final int DATABASE_VERSION = 12;


        public DatabaseHelper(Context context) {
            super(context, DATEBASE_NAME, null, DATABASE_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE favorites (_id INTEGER PRIMARY KEY AUTOINCREMENT,item_title VARCHAR," +
                    "item_intent VARCHAR,item_type INTEGER,item_index INTEGER,item_action VARCHAR)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        private void loadFavorites(SQLiteDatabase db, int workspaceResId) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ContentValues values = new ContentValues();
            PackageManager packageManager = mContext.getPackageManager();
            int i = 0;
            try {
                XmlResourceParser parser = mContext.getResources().getXml(workspaceResId);
                AttributeSet attributeSet = Xml.asAttributeSet(parser);
                beginDocument(parser, TAG_FAORITES);
                int depth = parser.getDepth();
                int type = 0;
                /**
                 * 用来给app排序的index
                 */
                int index;
                while (((type = parser.next()) != XmlPullParser.END_TAG ||
                        parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
                    if (type != XmlPullParser.START_TAG) {

                        continue;
                    }

                    TypedArray array = mContext.obtainStyledAttributes(attributeSet, R.styleable.Favorite);

                    values.clear();
                    String tag = parser.getName();
                    if (tag.equals(TAG_FAORITE)) {
                        boolean add = addFavorite(db, values, array, packageManager);
                    } else if (tag.equals(TAG_QUICKSWITCH)) {
                        boolean add = addQuickSwith(db, values, array, packageManager);
                    }
                    array.recycle();

                }


            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void beginDocument(XmlPullParser parser, String firstElement) throws XmlPullParserException,
                IOException {
            int type = 0;
            while ((type = parser.next()) != XmlPullParser.START_TAG && (type != XmlPullParser.END_DOCUMENT)) {
                ;
            }

            if (type != XmlPullParser.START_TAG) {
                throw new XmlPullParserException("not start tag found");
            }

            if (!parser.getName().equals(firstElement)) {
                throw new XmlPullParserException("Unexpected start tag: found " + parser.getName() +
                        ", expected " + firstElement);
            }

        }

        private boolean addFavorite(SQLiteDatabase database, ContentValues values, TypedArray array,
                                    PackageManager packageManager) throws XmlPullParserException, IOException {

            String packageName = array.getString(R.styleable.Favorite_packageName);
            String className = array.getString(R.styleable.Favorite_className);

            if (packageManager == null || className == null) {
                return false;
            }
            boolean hasPackage = true;


            if (!isApkInstalled(mContext, packageName)) {
                Log.i("Gmw", "没安装=" + packageName);
                hasPackage = false;
            }
            if (hasPackage) {
                try {
                    ComponentName cn = new ComponentName(packageName, className);
                    PackageInfo info = packageManager.getPackageInfo(packageName, 0);
                    String item_title = info.applicationInfo.loadLabel(packageManager).toString();
                    String item_index = array.getString(R.styleable.Favorite_item_index);
                    values.put(SwipeSettings.Favorites.ITEM_TITLE, item_title);
                    values.put(SwipeSettings.Favorites.ITEM_INTENT, cn.toString());
                    values.put(SwipeSettings.Favorites.ITEM_INDEX, 1);
                    values.put(SwipeSettings.Favorites.ITEM_TYPE, SwipeSettings.Favorites.ITEM_TYPE_FAVORITE);
                    Log.i("Gmw", "addFavorite-" + item_title);
                    checkInsert(mDatabaseHelper, database, TAG_FAORITES, values);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }

            }
            return true;
        }

        public boolean addQuickSwith(SQLiteDatabase database, ContentValues values, TypedArray array,
                                     PackageManager packageManager) {
            String item_action = array.getString(R.styleable.Favorite_item_action);
            if (item_action == null) {
                return false;
            }
            String item_title = array.getString(R.styleable.Favorite_item_title);
            String item_index = array.getString(R.styleable.Favorite_item_index);
            values.put(SwipeSettings.Favorites.ITEM_TITLE, item_title);
            values.put(SwipeSettings.Favorites.ITEM_INDEX, 1);
            values.put(SwipeSettings.Favorites.ITEM_TYPE, SwipeSettings.Favorites.ITEM_TYPE_SWITCH);
            values.put(SwipeSettings.Favorites.ITEM_ACTION, item_action);
            Log.i("Gmw", "addQuick-" + item_action);
            checkInsert(mDatabaseHelper, database, TAG_FAORITES, values);

            return true;
        }

        private void printAllAppList() {

            List<PackageInfo> packs = mContext.getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < packs.size(); i++) {
                PackageInfo p = packs.get(i);
                if ((p.versionName == null)) {
                    continue;
                }

                Log.i("Gmw", "lable=" + p.applicationInfo.loadLabel(mContext.getPackageManager()));
                Log.i("Gmw", "package=" + p.packageName);
                Log.i("Gmw", "package_class=" + p.activities);


            }
        }

        public boolean isApkInstalled(Context context, String packageName) {
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
    }


}
