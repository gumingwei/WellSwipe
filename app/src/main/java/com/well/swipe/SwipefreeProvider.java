package com.well.swipe;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Xml;

import com.well.swipecomm.utils.Utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mingwei on 3/14/16.
 * <p/>
 * <p/>
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 */
public class SwipefreeProvider extends ContentProvider {

    private DatabaseHelper mDatabaseHelper;

    static final String TABLE_FAVORITES = "favorites";

    static final String TABLE_WHITELIST = "whitelist";

    static final String PARAMETER_NOTIFY = "notify";

    static final String AUTHORITY = "com.well.swipe.favorites";

    private static UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final String PREFERENCE_NAME = "loadfavorite";

    private static final String PREFERENCE_KEY = "isFirstLoad";

    static {
        mUriMatcher.addURI("com.well.swipe.favorites", "favorites", 1);

        mUriMatcher.addURI("com.well.swipe.favorites", "whitelist", 2);
    }

    public SwipefreeProvider() {

    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        ((SwipefreeApplication) getContext()).setProvider(this);
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        SqlArguments args = new SqlArguments(uri, null, null);
        if (TextUtils.isEmpty(args.where)) {
            return "vnd.android.cursor.dir/" + args.table;
        } else {
            return "vnd.android.cursor.item/" + args.table;
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (mUriMatcher.match(uri)) {
            case 1:
                break;
        }
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(args.table);

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        Cursor result = qb.query(db, projection, args.where, args.args, null, null, sortOrder);
        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private static long dbInsertAndCheck(DatabaseHelper helper, SQLiteDatabase db, String table,
                                         String nullColumnHack, ContentValues values) {
        //判断存App，Tools数据时使用，在判断白名单是没用了
        //if (!values.containsKey(SwipeSettings.BaseColumns.ITEM_TYPE)) {
        //throw new RuntimeException("Error: attempting to add item without specifying an id");
        //}
        return db.insert(table, nullColumnHack, values);
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SqlArguments args = new SqlArguments(uri);

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final long rowId = dbInsertAndCheck(mDatabaseHelper, db, args.table, null, values);
        if (rowId <= 0) return null;

        uri = ContentUris.withAppendedId(uri, rowId);
        sendNotify(uri);

        return uri;
    }

    private void sendNotify(Uri uri) {
        String notify = uri.getQueryParameter(PARAMETER_NOTIFY);
        if (notify == null || "true".equals(notify)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int count = db.delete(args.table, args.where, args.args);
        if (count > 0) sendNotify(uri);

        return count;
    }

    /**
     * 批量插入
     *
     * @param uri
     * @param values
     * @return
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SqlArguments args = new SqlArguments(uri, null, null);
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            int count = values.length;
            for (int i = 0; i < count; i++) {
                if (db.insert(args.table, null, values[i]) < 0) {
                    return 0;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return values.length;
    }

    /**
     * 读取配置的app个开关数据
     *
     * @param resId
     */
    synchronized public void loadDefaultFavoritesIfNecessary(int resId) {

        SharedPreferences preferences = getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (!preferences.getBoolean(PREFERENCE_KEY, false)) {
            mDatabaseHelper.loadFavorites(mDatabaseHelper.getWritableDatabase(), resId);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(PREFERENCE_KEY, true);
            editor.commit();
        }

    }

    public class DatabaseHelper extends SQLiteOpenHelper {

        private Context mContext;

        private final String TAG_FAORITES = "favorites";

        private final String TAG_FAORITE = "favorite";

        private final String TAG_QUICKSWITCH = "quicksswitch";

        private static final String DATEBASE_NAME = "wellswipe.db";

        private static final int DATABASE_VERSION = 12;

        private int LENGTH = 9;

        public DatabaseHelper(Context context) {
            super(context, DATEBASE_NAME, null, DATABASE_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE favorites (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "item_title VARCHAR," +
                    "item_intent VARCHAR," +
                    "item_type INTEGER," +
                    "item_index INTEGER," +
                    "item_action VARCHAR," +
                    "icon_type INTEGER," +
                    "icon_package VARCHAR," +
                    "icon_resource VARCHAR," +
                    "icon_bitmap BLOB)");

            db.execSQL("CREATE TABLE whitelist(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "item_intent VARCHAR)");
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
                while (((type = parser.next()) != XmlPullParser.END_TAG ||
                        parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
                    if (type != XmlPullParser.START_TAG) {

                        continue;
                    }

                    TypedArray array = mContext.obtainStyledAttributes(attributeSet, R.styleable.Favorite);

                    values.clear();
                    String tag = parser.getName();
                    if (tag.equals(TAG_FAORITE)) {
                        boolean add = addFavorite(db, values, array, packageManager, intent);
                    } else if (tag.equals(TAG_QUICKSWITCH)) {
                        boolean add = addQuickSwith(db, values, array, packageManager);
                    }
                    array.recycle();
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                db.close();
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

        /**
         * 从default_workspace中读取配置数据，然后存入数据库
         *
         * @param database
         * @param values
         * @param array
         * @param packageManager
         * @param intent
         * @return
         * @throws XmlPullParserException
         * @throws IOException
         */
        private boolean addFavorite(SQLiteDatabase database, ContentValues values, TypedArray array,
                                    PackageManager packageManager, Intent intent) throws XmlPullParserException, IOException {
            String packageName = array.getString(R.styleable.Favorite_packageName);
            String className = array.getString(R.styleable.Favorite_className);

            if (packageManager == null || className == null) {
                return false;
            }
            boolean hasPackage = true;
            /**
             * 判断是否安装
             */
            if (!Utils.isApkInstalled(mContext, packageName)) {
                hasPackage = false;
            }
            if (hasPackage) {
                try {
                    ApplicationInfo appinfo = packageManager.getApplicationInfo(packageName, 0);
                    Drawable drawable = appinfo.loadIcon(packageManager);
                    BitmapDrawable bd = (BitmapDrawable) drawable;
                    //
                    ComponentName cn = new ComponentName(packageName, className);
                    PackageInfo info = packageManager.getPackageInfo(packageName, 0);
                    String item_title = info.applicationInfo.loadLabel(packageManager).toString();
                    intent.setComponent(cn);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    int item_index = array.getInt(R.styleable.Favorite_item_index, 0);
                    values.put(SwipeSettings.BaseColumns.ITEM_TITLE, item_title);
                    values.put(SwipeSettings.BaseColumns.ITEM_INTENT, intent.toUri(0));
                    values.put(SwipeSettings.BaseColumns.ITEM_INDEX, item_index);
                    values.put(SwipeSettings.BaseColumns.ITEM_TYPE, SwipeSettings.BaseColumns.ITEM_TYPE_APPLICATION);
                    values.put(SwipeSettings.BaseColumns.ICON_TYPE, SwipeSettings.BaseColumns.ICON_TYPE_BITMAP);
                    //把bitmap存入数据库
                    values.put(SwipeSettings.BaseColumns.ICON_BITMAP, flattenBitmap(bd.getBitmap()));

                    /**
                     * 如果表里已经包含了存在的index，就不在插入了
                     */
                    if (!hasIndex(database, item_index, SwipeSettings.BaseColumns.ITEM_TYPE_APPLICATION)) {
                        checkInsert(database, TAG_FAORITES, values);
                    }
                    //database.close();
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
            int item_index = array.getInt(R.styleable.Favorite_item_index, 0);
            values.put(SwipeSettings.BaseColumns.ITEM_TITLE, item_title);
            values.put(SwipeSettings.BaseColumns.ITEM_INDEX, item_index);
            values.put(SwipeSettings.BaseColumns.ITEM_TYPE, SwipeSettings.BaseColumns.ITEM_TYPE_SWITCH);
            values.put(SwipeSettings.BaseColumns.ITEM_ACTION, item_action);

            if (!hasIndex(database, item_index, SwipeSettings.BaseColumns.ITEM_TYPE_SWITCH)) {
                checkInsert(database, TAG_FAORITES, values);
            }
            //database.close();
            return true;
        }

        /**
         * 存表之前每次检查一边表中是否已经存在可当前读取到的index的app，如果有就跳过读下一个
         * 没有才回存表
         *
         * @param database
         * @param target
         * @param type
         * @return
         */
        private boolean hasIndex(SQLiteDatabase database, int target, int type) {
            Cursor cursor = database.rawQuery("select item_index from favorites where item_type=" + type, null);
            ArrayList<Integer> index = new ArrayList<>();
            if (cursor.getCount() < 9) {
                if (cursor.getCount() > 0) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        index.add(cursor.getInt(cursor.getColumnIndexOrThrow(SwipeSettings.BaseColumns.ITEM_INDEX)));
                    }
                } else {
                    return false;
                }
                for (int j = 0; j < index.size(); j++) {
                    if (index.get(j) == target) {
                        return true;
                    }
                }
                return false;
            } else {
                return true;
            }
        }

        public void checkInsert(SQLiteDatabase database, String table, ContentValues values) {
            database.insert(table, null, values);
        }

        byte[] flattenBitmap(Bitmap bitmap) {
            int size = bitmap.getWidth() * bitmap.getHeight() * 4;
            ByteArrayOutputStream out = new ByteArrayOutputStream(size);
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                return out.toByteArray();
            } catch (IOException e) {
                return null;
            }
        }
    }

    /**
     * Sql参数拼接
     */
    static class SqlArguments {

        public final String table;
        public final String where;
        public final String[] args;

        SqlArguments(Uri url, String where, String[] args) {
            if (url.getPathSegments().size() == 1) {
                this.table = url.getPathSegments().get(0);
                this.where = where;
                this.args = args;
            } else if (url.getPathSegments().size() != 2) {
                throw new IllegalArgumentException("Invalid URI: " + url);
            } else if (!TextUtils.isEmpty(where)) {
                throw new UnsupportedOperationException("WHERE clause not supported: " + url);
            } else {
                this.table = url.getPathSegments().get(0);
                this.where = "_id=" + ContentUris.parseId(url);
                this.args = null;
            }
        }

        SqlArguments(Uri url) {
            if (url.getPathSegments().size() == 1) {
                table = url.getPathSegments().get(0);
                where = null;
                args = null;
            } else {
                throw new IllegalArgumentException("Invalid URI: " + url);
            }
        }
    }

}