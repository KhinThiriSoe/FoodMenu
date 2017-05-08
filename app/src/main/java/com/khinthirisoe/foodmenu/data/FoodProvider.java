package com.khinthirisoe.foodmenu.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.khinthirisoe.foodmenu.data.FoodContract.FoodEntry;

/**
 * Created by khinthirisoe on 5/4/17.
 */

public class FoodProvider extends ContentProvider {

    public static final String LOG_TAG = FoodProvider.class.getSimpleName();
    private FoodDbHelper mDbHelper;

    private static final int FOODS = 100;

    private static final int FOOD_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(FoodContract.CONTENT_AUTHORITY, FoodContract.PATH_FOODS, FOODS);
        sUriMatcher.addURI(FoodContract.CONTENT_AUTHORITY, FoodContract.PATH_FOODS + "/#", FOOD_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new FoodDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case FOODS:
                cursor = database.query(FoodEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

                break;
            case FOOD_ID:
                selection = FoodEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(FoodEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FOODS:
                return insertFood(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertFood(Uri uri, ContentValues values) {

        String name = values.getAsString(FoodEntry.COLUMN_FOOD_NAME);
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Food requires a name");
        }

        Integer type = values.getAsInteger(FoodEntry.COLUMN_FOOD_TYPE);
        if (type == null || !FoodEntry.isValidFoodType(type)) {
            throw new IllegalArgumentException("Food requires valid type");
        }

        String price = values.getAsString(FoodEntry.COLUMN_FOOD_PRICE);
        if (TextUtils.isEmpty(price)) {
            throw new IllegalArgumentException("Food requires price");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(FoodEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FOODS:
                return updatePet(uri, values, selection, selectionArgs);
            case FOOD_ID:
                selection = FoodEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(FoodEntry.COLUMN_FOOD_NAME)) {
            String name = values.getAsString(FoodEntry.COLUMN_FOOD_NAME);
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Food requires a name");
            }
        }

        if (values.containsKey(FoodEntry.COLUMN_FOOD_TYPE)) {
            Integer type = values.getAsInteger(FoodEntry.COLUMN_FOOD_TYPE);
            if (type == null || !FoodEntry.isValidFoodType(type)) {
                throw new IllegalArgumentException("Food requires valid type");
            }
        }

        if (values.containsKey(FoodEntry.COLUMN_FOOD_PRICE)) {
            String price = values.getAsString(FoodEntry.COLUMN_FOOD_PRICE);
            if (TextUtils.isEmpty(price)) {
                throw new IllegalArgumentException("Food requires price");
            }
        }

        if (values.containsKey(FoodEntry.COLUMN_FOOD_PHOTO)) {
            String photo = values.getAsString(FoodEntry.COLUMN_FOOD_PRICE);

        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(FoodEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case FOODS:
                rowsDeleted = database.delete(FoodEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FOOD_ID:
                selection = FoodEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(FoodEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FOODS:
                return FoodEntry.CONTENT_LIST_TYPE;
            case FOOD_ID:
                return FoodEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }    }
}
