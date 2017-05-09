package com.example.android.samsunginventoryapplication.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.samsunginventoryapplication.data.PhoneContract.PhoneEntry;

/**
 * Created by ultrajustin22 on 25/4/2017.
 */

public class PhoneProvider extends ContentProvider {

    //Create a UriMatcher object which corresponds to an integer code
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //Matching Uri for muliple rows for PhoneEntry data table
    public static final int PHONE = 100;

    //Matching Uri for a single row (Phone) for PhoneEntry data table
    public static final int PHONE_ID = 101;

    //LOG_TAG
    public static final String LOG_TAG = "PhoneProvider.class";

    //Private variable which allows the database to be called upon
    private PhoneDbHelper mDbHelper;

    //Instantiated initially b/c UriMatcher wants to match incoming content Uri to integer code in database
    static {
        //Calls to addUri() are instantiated here..the first line of code shows multiple rows of the database table
        sUriMatcher.addURI(PhoneContract.CONTENT_AUTHORITY, PhoneContract.PATH_PHONE, PHONE);
        //Calls to addUri() are instantiated here..the second line of code shows a single row (Phone) of the database table
        //especially if we only want a single Phone inventory report
        sUriMatcher.addURI(PhoneContract.CONTENT_AUTHORITY,PhoneContract.PATH_PHONE + "/#", PHONE_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new PhoneDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Get readable database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        //Create a Cursor instance, in case if the Cursor has nothing to return from reading all of the data
        Cursor cursor;
        //Match the Uri to its corresponding code using switch-case statements
        int match = sUriMatcher.match(uri);
        switch (match) {
            case (PHONE):
                //Cursor should have query method loaded with the parameters
                cursor = db.query(PhoneEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case (PHONE_ID):
                //Modifying the selection and selectionArgs[] String values
                //Extracting the ID_ from the URI
                selection = PhoneEntry._ID + "=?";
                //For every "=?" selection created, we only need to have an element in each selection
                //Since we have 1 question mark in each selection, we have 1 String in selection arguments' String array
                //This line of code literally returns a String representation of the uri parsed in
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = db.query(PhoneEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                //Throw exception with inability to read the code (IllegalArgumentException)
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }
        // Set notification URI on the Cursor, so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    //Returns the MIME (data) type
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case (PHONE):
                return PhoneEntry.CONTENT_LIST_TYPE;
            case (PHONE_ID):
                return PhoneEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + "with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case (PHONE):
                return insertPhone(uri,values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    //Helper method which inserts the phone entry with the given ContentValues
    private Uri insertPhone(Uri uri, ContentValues contentValues) {
        //Check if all of the qualities are NOT null...using data validation
        String brand = contentValues.getAsString(PhoneEntry.COLUMN_PHONE_BRAND);
        if (brand == null) {
            throw new IllegalArgumentException("How do you not know what the phone's brand is??");
        }
        String model = contentValues.getAsString(PhoneEntry.COLUMN_PHONE_MODEL);
        if (model == null) {
            throw new IllegalArgumentException("How do you NOT know what your phone's model is??");
        }
        Integer colour = contentValues.getAsInteger(PhoneEntry.COLUMN_PHONE_COLOUR);
        if (colour == null || !PhoneEntry.isValidColour(colour)) {
            throw new IllegalArgumentException("Dude, you should know the colour of your phone");
        }
        Integer memory = contentValues.getAsInteger(PhoneEntry.COLUMN_PHONE_MEMORY);
        if (memory == null || memory < 0) {
            throw new IllegalArgumentException("Dude, you should know the memory size of your phone");
        }
        Integer quantity = contentValues.getAsInteger(PhoneEntry.COLUMN_PHONE_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Dude, you should know how big/small the inventory size of phones are");
        }
        Integer price = contentValues.getAsInteger(PhoneEntry.COLUMN_PHONE_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Dude, you should know the price of the given model/brand of your phone");
        }

        //Allow access for the database to be written
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(PhoneEntry.TABLE_NAME,null,contentValues);
        if (id == -1) {
            Log.e(LOG_TAG,"Failed to register a new row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Track the number of rows in the database that will/is being deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PHONE:
                // Delete all rows that match the selection and selection args
                rowsDeleted = db.delete(PhoneEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PHONE_ID:
                // Delete a single row given by the ID in the URI
                selection = PhoneEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = db.delete(PhoneEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }

        // Return the number of rows deleted
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case (PHONE):
                return updatePhone(uri, values, selection, selectionArgs);
            case (PHONE_ID):
                //Extract ID and modify selection and selectionArgs[]
                selection = PhoneEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updatePhone(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Error updating for URI " + uri);
        }
    }

    //Helper method which helps to update the phones
    private int updatePhone(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO: Update the selected pets in the pets database table with the given ContentValues
        //Checking if there is a key-value pair exists for each section
        if (values.containsKey(PhoneEntry.COLUMN_PHONE_BRAND)) {
            String brand = values.getAsString(PhoneEntry.COLUMN_PHONE_BRAND);
            if (brand == null) {
                throw new IllegalArgumentException("How do you not know what the phone's brand is??");
            }
        }
        if (values.containsKey(PhoneEntry.COLUMN_PHONE_MODEL)) {
            String model = values.getAsString(PhoneEntry.COLUMN_PHONE_MEMORY);
            if (model == null) {
                throw new IllegalArgumentException("How do you not know what the phone's model is??");
            }
        }
        if (values.containsKey(PhoneEntry.COLUMN_PHONE_COLOUR)) {
            Integer colour = values.getAsInteger(PhoneEntry.COLUMN_PHONE_COLOUR);
            if (colour == null || !PhoneEntry.isValidColour(colour)) {
                throw new IllegalArgumentException("Dude, you should know the colour of your phone");
            }
        }
        if (values.containsKey(PhoneEntry.COLUMN_PHONE_MEMORY)) {
            Integer memory = values.getAsInteger(PhoneEntry.COLUMN_PHONE_MEMORY);
            if (memory == null || memory < 0) {
                throw new IllegalArgumentException("Dude, you should know the memory size of your phone");
            }
        }
        if (values.containsKey(PhoneEntry.COLUMN_PHONE_QUANTITY)) {
            Integer quantity = values.getAsInteger(PhoneEntry.COLUMN_PHONE_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Dude, you should know what the phone quantity stock is");
            }
        }
        if (values.containsKey(PhoneEntry.COLUMN_PHONE_PRICE)) {
            Integer price = values.getAsInteger(PhoneEntry.COLUMN_PHONE_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Dude, you should know how much each phone should cost");
            }
        }
        //If the ContentValues size is 0, don't even try to update
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated = db.update(PhoneEntry.TABLE_NAME,values,selection,selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        // TODO: Return the number of rows that were affected
        return rowsUpdated;
    }
}
