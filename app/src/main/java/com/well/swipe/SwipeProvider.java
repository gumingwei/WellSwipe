package com.well.swipe;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;

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
        return false;
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

    synchronized public void loadDefaultFavoritesIfNecessary(int resId) {
        Log.i("Gmw", "loadDefaultFavoritesIfNecessary");
        mDatabaseHelper.loadFavorites(mDatabaseHelper.getWritableDatabase(), resId);

    }

    public class DatabaseHelper extends SQLiteOpenHelper {
        Context mContext;

        private static final String DATEBASE_NAME = "wellswipe.db";

        private static final int DATABASE_VERSION = 12;


        public DatabaseHelper(Context context) {
            super(context, DATEBASE_NAME, null, DATABASE_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

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
            XmlResourceParser parser = mContext.getResources().getXml(workspaceResId);
            AttributeSet attributeSet = Xml.asAttributeSet(parser);
        }
    }
}
