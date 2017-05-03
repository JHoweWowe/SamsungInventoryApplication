package com.example.android.samsunginventoryapplication.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This Java class defines the database and how users are able to interact with it using ContentProvider
 */

public final class PhoneContract {

    private PhoneContract() {}

    /*
    Designing/creating the setup structure for the Content URI data to be passed through
    That includes:
    - ContentAuthority (Provider)
    - Path
    - BaseContentURI
    - Appended Content URI with path established
     */

    public static final String CONTENT_AUTHORITY = "com.example.android.samsunginventoryapplication";
    public static final String PATH_PHONE = "samsunginventoryapplication";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);




    //Inner class which defines the database table
    public static class PhoneEntry implements BaseColumns {
        //The Content Uri to access the phone data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PHONE);

        //Declaring MIME types which define the data type
        //This declares the MIME type for a list of phones
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PHONE;
        //This declares the constant for the MIME type for a single phone
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PHONE;



        public static final String TABLE_NAME = "phone";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PHONE_BRAND = "brand";
        public static final String COLUMN_PHONE_MODEL = "model";
        public static final String COLUMN_PHONE_COLOUR = "colour";
        public static final String COLUMN_PHONE_MEMORY = "memory";
        public static final String COLUMN_PHONE_QUANTITY = "quantity";
        public static final String COLUMN_PHONE_PRICE = "price";

        //These following values are used for choosing colour options in the spinner
        public static final int COLOUR_BLACK = 0;
        public static final int COLOUR_GREY = 1;
        public static final int COLOUR_WHITE = 2;
        public static final int COLOUR_UNKNOWN = 3;

        public static boolean isValidColour(int colour) {
            if (colour == COLOUR_BLACK || colour == COLOUR_GREY || colour == COLOUR_WHITE || colour == COLOUR_UNKNOWN) {
                return true;
            }
            return false;
        }
    }

}
