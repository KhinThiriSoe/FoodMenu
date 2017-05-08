package com.khinthirisoe.foodmenu.activity;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.khinthirisoe.foodmenu.R;
import com.khinthirisoe.foodmenu.adapter.FoodCursorAdapter;
import com.khinthirisoe.foodmenu.data.FoodContract.FoodEntry;
import com.khinthirisoe.foodmenu.utils.BitmapUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    private static final int FOOD_LOADER = 0;
    FoodCursorAdapter mCursorAdapter;

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.list)
    ListView foodListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        ButterKnife.bind(this);

        View emptyView = findViewById(R.id.rl_empty);
        foodListView.setEmptyView(emptyView);

        mCursorAdapter = new FoodCursorAdapter(this, null);
        foodListView.setAdapter(mCursorAdapter);

        fab.setOnClickListener(this);
        foodListView.setOnItemClickListener(this);

        //initiate loader
        getLoaderManager().initLoader(FOOD_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {FoodEntry._ID, FoodEntry.COLUMN_FOOD_NAME, FoodEntry.COLUMN_FOOD_PHOTO,
                FoodEntry.COLUMN_FOOD_PRICE, FoodEntry.COLUMN_FOOD_TYPE};

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String filterBy = sharedPrefs.getString(
                getString(R.string.settings_filter_by_key),
                getString(R.string.settings_filter_by_default)
        );

        //default is all (3)
        if (filterBy.contains("3")) {
            return new CursorLoader(this, FoodEntry.CONTENT_URI, projection, null, null, null);
        }
        else {
            String[] selectionArgs = new String[]{filterBy};

            return new CursorLoader(this, FoodEntry.CONTENT_URI, projection, FoodEntry.COLUMN_FOOD_TYPE + "=?", selectionArgs, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertFood();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllFoods();
                return true;
            case R.id.action_setting:
                startActivity(new Intent(CatalogActivity.this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllFoods() {
        int rowsDeleted = getContentResolver().delete(FoodEntry.CONTENT_URI, null, null);
        Log.v(LOG_TAG, rowsDeleted + " rows deleted from restaurant database");
    }

    private void insertFood() {
        // insert dummy data

        ContentValues values = new ContentValues();
        values.put(FoodEntry.COLUMN_FOOD_NAME, "Fried chicken");
        values.put(FoodEntry.COLUMN_FOOD_TYPE, FoodEntry.MAIN_FOOD_TYPE);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_no_photo);
        String photo = BitmapUtils.encodeToBase64(bitmap, Bitmap.CompressFormat.PNG, 100);
        values.put(FoodEntry.COLUMN_FOOD_PHOTO, photo);
        values.put(FoodEntry.COLUMN_FOOD_PRICE, "1500 Ks");

        getContentResolver().insert(FoodEntry.CONTENT_URI, values);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(CatalogActivity.this, EditorActivity.class));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Uri currentPetUri = ContentUris.withAppendedId(FoodEntry.CONTENT_URI, id);

        startActivity(new Intent(CatalogActivity.this, EditorActivity.class)
                .setData(currentPetUri));
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //restartLoader for settings
        getLoaderManager().restartLoader(FOOD_LOADER, null, this);

    }
}
