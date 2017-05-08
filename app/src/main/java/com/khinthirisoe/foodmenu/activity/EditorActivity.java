package com.khinthirisoe.foodmenu.activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.khinthirisoe.foodmenu.R;
import com.khinthirisoe.foodmenu.data.FoodContract.FoodEntry;
import com.khinthirisoe.foodmenu.utils.BitmapUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditorActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.edt_food_name)
    EditText edtName;
    @BindView(R.id.spinner_type)
    Spinner spinnerType;
    @BindView(R.id.edt_food_price)
    EditText edtPrice;
    @BindView(R.id.img_insert)
    ImageView imgInsert;

    private static final int EXISTING_FOOD_LOADER = 0;
    private Uri mCurrentPetUri;

    private int mType = FoodEntry.MAIN_FOOD_TYPE;
    private boolean mFoodHasChanged = false;

    private static final int SELECT_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentPetUri = intent.getData();

        if (mCurrentPetUri == null) {
            setTitle(getString(R.string.editor_activity_title_add_food));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_food));
            getLoaderManager().initLoader(EXISTING_FOOD_LOADER, null, this);
        }

        imgInsert.setOnClickListener(this);

        edtName.setOnTouchListener(mTouchListener);
        edtPrice.setOnTouchListener(mTouchListener);

        setupSpinner();
    }

    private void setupSpinner() {

        ArrayAdapter typeSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_type_options, android.R.layout.simple_spinner_item);

        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        spinnerType.setAdapter(typeSpinnerAdapter);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.snack_food))) {
                        mType = FoodEntry.SNACK_TYPE;
                    } else if (selection.equals(getString(R.string.soft_drink))) {
                        mType = FoodEntry.DRINK_TYPE;
                    } else {
                        mType = FoodEntry.MAIN_FOOD_TYPE;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType = FoodEntry.MAIN_FOOD_TYPE;
            }
        });
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mFoodHasChanged = true;
            return false;
        }
    };

    @Override
    public void onClick(View v) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), SELECT_PICTURE);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    imgInsert.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void savePet() {
        String name = edtName.getText().toString().trim();
        String price = edtPrice.getText().toString().trim();
        Bitmap bitmap = ((BitmapDrawable) imgInsert.getDrawable()).getBitmap();
        String photo = BitmapUtils.encodeToBase64(bitmap, Bitmap.CompressFormat.PNG, 100);

        if (mCurrentPetUri == null && TextUtils.isEmpty(name) && TextUtils.isEmpty(price)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(FoodEntry.COLUMN_FOOD_NAME, name);
        values.put(FoodEntry.COLUMN_FOOD_PRICE, price);
        values.put(FoodEntry.COLUMN_FOOD_TYPE, mType);
        values.put(FoodEntry.COLUMN_FOOD_PHOTO, photo);

        if (mCurrentPetUri == null) {

            Uri newUri = getContentResolver().insert(FoodEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_food_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_food_successful), Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentPetUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_food_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_food_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {FoodEntry._ID, FoodEntry.COLUMN_FOOD_NAME, FoodEntry.COLUMN_FOOD_TYPE, FoodEntry.COLUMN_FOOD_PHOTO, FoodEntry.COLUMN_FOOD_PRICE};
        return new CursorLoader(this, mCurrentPetUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nameColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_FOOD_NAME);
            int typeColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_FOOD_TYPE);
            int photoColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_FOOD_PHOTO);
            int priceColumnIndex = cursor.getColumnIndex(FoodEntry.COLUMN_FOOD_PRICE);

            String name = cursor.getString(nameColumnIndex);
            int type = cursor.getInt(typeColumnIndex);
            String price = cursor.getString(priceColumnIndex);

            edtName.setText(name);
            edtPrice.setText(price);
            imgInsert.setImageBitmap(BitmapUtils.decodeBase64(cursor.getString(photoColumnIndex)));

            switch (type) {
                case FoodEntry.DRINK_TYPE:
                    spinnerType.setSelection(1);
                    break;
                case FoodEntry.SNACK_TYPE:
                    spinnerType.setSelection(2);
                    break;
                default:
                    spinnerType.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        edtName.setText("");
        edtPrice.setText("");
        imgInsert.setImageBitmap(null);
        spinnerType.setSelection(0);
    }


    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Changes will be unsaved");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteFood();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteFood() {
        if (mCurrentPetUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentPetUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_food_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_food_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentPetUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                savePet();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mFoodHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (!mFoodHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

}
