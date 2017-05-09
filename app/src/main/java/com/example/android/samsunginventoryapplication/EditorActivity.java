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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.samsunginventoryapplication.data.PhoneContract.PhoneEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PHONE_LOADER = 0;

    private EditText mBrandEditText;
    private EditText mModelEditText;
    private EditText mStorageSizeEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private Spinner mColourSpinner;

    //Default colour of the Phone should be Unknown, in case the user fails to put the colour of the phone
    private int mColour = PhoneEntry.COLOUR_UNKNOWN;

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
            getSupportLoaderManager().initLoader(PHONE_LOADER,null,this);
        }

        mBrandEditText = (EditText) findViewById(R.id.name_phone_series);
        mModelEditText = (EditText) findViewById(R.id.series_phone_series);
        mStorageSizeEditText = (EditText) findViewById(R.id.phone_storage_size);
        mQuantityEditText = (EditText) findViewById(R.id.quantity_editText);
        mPriceEditText = (EditText) findViewById(R.id.phone_price_editText);
        
        mColourSpinner = (Spinner) findViewById(R.id.spinner_colour);
        setupSpinner();

        //Implementing the increase and decrease of quantity of the phone inventory stock
        Button buttonLessQuantity = (Button) findViewById(R.id.phone_minus_quantity);
        buttonLessQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtractOneToQuantity();
            }
        });
        Button buttonMoreQuantity = (Button) findViewById(R.id.phone_plus_quantity);
        buttonMoreQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseOneToQuantity();
            }
        });
    }

    //Creates the setup for the Spinner
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter colourSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.phone_colour_array, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        colourSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mColourSpinner.setAdapter(colourSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mColourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.phone_colour_black))) {
                        mColour = PhoneEntry.COLOUR_BLACK;
                    } else if (selection.equals(getString(R.string.phone_colour_white))) {
                        mColour = PhoneEntry.COLOUR_WHITE;
                    } else if (selection.equals(getString(R.string.phone_colour_grey))){
                        mColour = PhoneEntry.COLOUR_GREY;
                    }
                    else {
                        mColour = PhoneEntry.COLOUR_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mColour = PhoneEntry.COLOUR_UNKNOWN;
            }
        });

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
        String storageSizeString = mStorageSizeEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();

        // Check if this is supposed to be a new pet
        // and check if all the fields in the editor are blank
        if (mCurrentPhoneUri == null &&
                TextUtils.isEmpty(brandString) && TextUtils.isEmpty(modelString) && TextUtils.isEmpty(storageSizeString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(priceString) && mColour == PhoneEntry.COLOUR_UNKNOWN) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and Phone attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(PhoneEntry.COLUMN_PHONE_BRAND, brandString);
        values.put(PhoneEntry.COLUMN_PHONE_MODEL, modelString);
        values.put(PhoneEntry.COLUMN_PHONE_COLOUR, mColour);
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

        int storageSize = 0;
        if (!TextUtils.isEmpty(storageSizeString)) {
            storageSize = Integer.parseInt(storageSizeString);
        }
        values.put(PhoneEntry.COLUMN_PHONE_MEMORY, storageSize);


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

    //Performs the action of deleting a phone entry from the database
    private void deletePhone() {
        if (mCurrentPhoneUri != null) {
            int rowsDeleted = getContentResolver().delete(PhoneEntry.CONTENT_URI,null,null);
            //Show if deleting the phone entry was successful or failed
            if (rowsDeleted == 0) {
                Toast.makeText(this,"Error with deleting the phone entry",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this,"Deleting the phone entry was successful",Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
    //Decreases the quantity of the phone inventory stock by one for every click
    private void subtractOneToQuantity() {
        String previousValueString = mQuantityEditText.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty() || previousValueString.equals("0")) {
            return;
        }
        else {
            previousValue = Integer.parseInt(previousValueString);
            mQuantityEditText.setText(String.valueOf(previousValue - 1));
        }
    }
    //Increases the quantity of the phone inventory stock by one for every click
    private void increaseOneToQuantity() {
        String previousValueString = mQuantityEditText.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty() || previousValueString.equals("0")) {
            return;
        }
        else {
            previousValue = Integer.parseInt(previousValueString);
            mQuantityEditText.setText(String.valueOf(previousValue + 1));
        }
    }


    //Whenever one of the menu options is clicked, ensure options can be chosen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.save_phone_entry):
                savePhone();
                //Exit out of the activity
                finish();
                return true;
            case (R.id.delete_phone_entry):
                deletePhone();
                //Exit out of activity
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {PhoneEntry._ID,PhoneEntry.COLUMN_PHONE_BRAND,PhoneEntry.COLUMN_PHONE_MODEL,
                PhoneEntry.COLUMN_PHONE_PRICE, PhoneEntry.COLUMN_PHONE_COLOUR,PhoneEntry.COLUMN_PHONE_MEMORY,PhoneEntry.COLUMN_PHONE_QUANTITY};
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
            int colourColumnIndex = cursor.getColumnIndex(PhoneEntry.COLUMN_PHONE_COLOUR);
            int storageSizeColumnIndex = cursor.getColumnIndex(PhoneEntry.COLUMN_PHONE_MEMORY);
            int priceColumnIndex = cursor.getColumnIndex(PhoneEntry.COLUMN_PHONE_PRICE);
            int inventoryColumnIndex = cursor.getColumnIndex(PhoneEntry.COLUMN_PHONE_QUANTITY);

            //Extract the properties
            String brand = cursor.getString(brandColumnIndex);
            String model = cursor.getString(modelColumnIndex);
            int colour = cursor.getInt(colourColumnIndex);
            int storageSize = cursor.getInt(storageSizeColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int inventorystock = cursor.getInt(inventoryColumnIndex);

            mBrandEditText.setText(brand);
            mModelEditText.setText(model);
            mPriceEditText.setText(Integer.toString(price));
            mStorageSizeEditText.setText(Integer.toString(storageSize));
            mQuantityEditText.setText(Integer.toString(inventorystock));

            // Colour is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Black, 2 is White, 3 is Grey).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (colour) {
                case PhoneEntry.COLOUR_BLACK:
                    mColourSpinner.setSelection(1);
                    break;
                case PhoneEntry.COLOUR_WHITE:
                    mColourSpinner.setSelection(2);
                    break;
                case PhoneEntry.COLOUR_GREY:
                    mColourSpinner.setSelection(3);
                default:
                    mColourSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //If the data is invalidated, then set everything to null or empty
        mBrandEditText.setText("");
        mModelEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mColourSpinner.setSelection(0);
    }
}
