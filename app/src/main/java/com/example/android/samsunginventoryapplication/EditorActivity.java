package com.example.android.samsunginventoryapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.samsunginventoryapplication.data.PhoneContract.PhoneEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PHONE_LOADER = 0;

    private EditText mBrandEditText;
    private EditText mModelEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;

    //ContentURI for existing Phone (null if new Phone)
    private Uri mCurrentPhoneUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentPhoneUri = intent.getData();

        //If the URI is empty, then that means there is no ContentURI passed through
        //and a new Phone inventory stock entry must be created
        if (mCurrentPhoneUri == null) {
            setTitle("Add a Phone");
            invalidateOptionsMenu();
        }
        else {
            setTitle("Edit a Phone");
            getLoaderManager().initLoader(PHONE_LOADER,null,null);
        }

        mBrandEditText = (EditText) findViewById(R.id.name_phone_series);
        mModelEditText = (EditText) findViewById(R.id.series_phone_series);
        mQuantityEditText = (EditText) findViewById(R.id.quantity_editText);
        mPriceEditText = (EditText) findViewById(R.id.phone_price_editText);
    }

    //Within the EditorActivity, inflate the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    /**
     * Get user input from editor and save phone into database.
     * NEEDS EDITING!!!
     */
    private void savePhone() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String brandString = mBrandEditText.getText().toString().trim();
        String modelString = mModelEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();

        // Check if this is supposed to be a new pet
        // and check if all the fields in the editor are blank
        if (mCurrentPhoneUri == null &&
                TextUtils.isEmpty(brandString) && TextUtils.isEmpty(modelString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(priceString)) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and Phone attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(PhoneEntry.COLUMN_PHONE_BRAND, brandString);
        values.put(PhoneEntry.COLUMN_PHONE_MODEL, modelString);
        // If the price is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(PhoneEntry.COLUMN_PHONE_PRICE, price);
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(PhoneEntry.COLUMN_PHONE_QUANTITY, quantity);

        // Determine if this is a new or existing pet by checking if mCurrentPhoneUri is null or not
        if (mCurrentPhoneUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(PhoneEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_phone_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_phone_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentPhoneUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_phone_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_phone_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    //Whenever one of the menu options is clicked, ensure options can be chosen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.save_phone_entry):
                savePhone();
                return true;
            case (R.id.delete_phone_entry):
                //Delete phone data entry...to be implemented after creating the PhoneProvider class
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {PhoneEntry._ID,PhoneEntry.COLUMN_PHONE_BRAND,PhoneEntry.COLUMN_PHONE_MODEL};
        return new CursorLoader(this,mCurrentPhoneUri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            //Find the columns responsible for the fields
            int brandColumnIndex = cursor.getColumnIndex(PhoneEntry.COLUMN_PHONE_BRAND);
            int modelColumnIndex = cursor.getColumnIndex(PhoneEntry.COLUMN_PHONE_MODEL);
            int priceColumnIndex = cursor.getColumnIndex(PhoneEntry.COLUMN_PHONE_PRICE);
            int inventoryColumnIndex = cursor.getColumnIndex(PhoneEntry.COLUMN_PHONE_QUANTITY);

            //Extract the properties
            String brand = cursor.getString(brandColumnIndex);
            String model = cursor.getString(modelColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int inventorystock = cursor.getInt(inventoryColumnIndex);

            mBrandEditText.setText(brand);
            mModelEditText.setText(model);
            mPriceEditText.setText(price);
            mQuantityEditText.setText(inventorystock);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //If the data is invalidated, then set everything to null or empty
        mBrandEditText.setText("");
        mModelEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
    }
}
