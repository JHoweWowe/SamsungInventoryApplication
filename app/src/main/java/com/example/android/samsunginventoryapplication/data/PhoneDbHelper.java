package com.example.android.samsunginventoryapplication.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.samsunginventoryapplication.data.PhoneContract.PhoneEntry;

/**
 * Created by ultrajustin22 on 25/4/2017.
 */

public class PhoneDbHelper extends SQLiteOpenHelper {

    //If the database schema (aka: PhoneContract Java class) is changed...the database version must be incremented
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "phone.db";

    //Helper values to create/upgrade SQLiteDatabase
    public static final String SQL_CREATE_PHONE_ENTRIES =
            "CREATE TABLE " + PhoneEntry.TABLE_NAME
            + " (" + PhoneEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + PhoneEntry.COLUMN_PHONE_BRAND + " TEXT,"
            + PhoneEntry.COLUMN_PHONE_MODEL + " TEXT,"
            + PhoneEntry.COLUMN_PHONE_COLOUR + " INTEGER,"
            + PhoneEntry.COLUMN_PHONE_MEMORY + " INTEGER DEFAULT 0,"
            + PhoneEntry.COLUMN_PHONE_QUANTITY + " INTEGER DEFAULT 0,"
            + PhoneEntry.COLUMN_PHONE_PRICE + " INTEGER)";

    public PhoneDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PHONE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This application is still in its beta stage
    }
}
