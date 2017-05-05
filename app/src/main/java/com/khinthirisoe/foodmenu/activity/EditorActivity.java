package com.khinthirisoe.foodmenu.activity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.khinthirisoe.foodmenu.R;
import com.khinthirisoe.foodmenu.data.FoodContract.FoodEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditorActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Object> {

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
        imgInsert.setOnTouchListener(mTouchListener);

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

    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }


}
