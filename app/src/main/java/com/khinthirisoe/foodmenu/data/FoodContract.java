package com.khinthirisoe.foodmenu.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by khinthirisoe on 5/4/17.
 */

public class FoodContract {

    public FoodContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.khinthirisoe.foodmenu";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FOODS = "foods";

    public static final class FoodEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FOODS);

        public final static String TABLE_NAME = "foods";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_FOOD_NAME ="name";

        public final static String COLUMN_FOOD_TYPE = "type";

        public final static String COLUMN_FOOD_PHOTO = "photo";

        public final static String COLUMN_FOOD_PRICE = "price";

        public static final int MAIN_FOOD_TYPE = 0;
        public static final int DRINK_TYPE = 1;
        public static final int SNACK_TYPE = 2;

        public static boolean isValidFoodType(Integer type) {

            if (type == DRINK_TYPE || type == SNACK_TYPE || type == MAIN_FOOD_TYPE) {
                return true;
            }
            return false;
        }

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FOODS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FOODS;
    }
}
