package com.khinthirisoe.foodmenu.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.khinthirisoe.foodmenu.R;
import com.khinthirisoe.foodmenu.data.FoodContract.FoodEntry;
import com.khinthirisoe.foodmenu.utils.BitmapUtils;

/**
 * Created by khinthirisoe on 5/5/17.
 */

public class FoodCursorAdapter extends CursorAdapter {

    public FoodCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_food, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView imgFood = (ImageView) view.findViewById(R.id.img_food);
        TextView txtFoodName = (TextView) view.findViewById(R.id.tv_food_name);
        TextView txtFoodPrice = (TextView) view.findViewById(R.id.tv_food_price);

        int nameColumnIndex = cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_FOOD_NAME);
        int priceColumnIndex = cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_FOOD_PRICE);
        int photoColumnIndex = cursor.getColumnIndexOrThrow(FoodEntry.COLUMN_FOOD_PHOTO);

        String foodName = cursor.getString(nameColumnIndex);
        String foodPrice = cursor.getString(priceColumnIndex);
        String photo = cursor.getString(photoColumnIndex);

        imgFood.setImageBitmap(BitmapUtils.decodeBase64(photo));
        txtFoodName.setText(foodName);
        txtFoodPrice.setText(foodPrice);
    }
}
