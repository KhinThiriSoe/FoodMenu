package com.khinthirisoe.foodmenu.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.khinthirisoe.foodmenu.data.FoodContract.FoodEntry;

/**
 * Created by khinthirisoe on 5/4/17.
 */

public class FoodDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = FoodDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "restaurant.db";

    private static final int DATABASE_VERSION = 1;

    public FoodDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_FOODS_TABLE = "CREATE TABLE " + FoodEntry.TABLE_NAME + " ("
                + FoodEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FoodEntry.COLUMN_FOOD_NAME + " TEXT NOT NULL, "
                + FoodEntry.COLUMN_FOOD_TYPE + " INTEGER NOT NULL DEFAULT 0, "
                + FoodEntry.COLUMN_FOOD_PHOTO + " TEXT, "
                + FoodEntry.COLUMN_FOOD_PRICE + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_FOODS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
