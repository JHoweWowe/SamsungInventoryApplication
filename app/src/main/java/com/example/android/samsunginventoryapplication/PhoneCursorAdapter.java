package com.example.android.samsunginventoryapplication;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.samsunginventoryapplication.data.PhoneContract.PhoneEntry;

/**
 * Created by ultrajustin22 on 27/4/2017.
 */

public class PhoneCursorAdapter extends CursorAdapter {

    public PhoneCursorAdapter(Context context, Cursor cursor){
        super(context,cursor,0);
    }
    //This method inflates a new View and returns it
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }
    //This method is used to bind all the data in a given view through list_item XML file
    //Used for creating TextViews and implementing them altogether
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        //Find the fields to populate inflated template
        TextView brandPhoneTextView = (TextView) view.findViewById(R.id.brand_phone);
        TextView modelPhoneTextView = (TextView) view.findViewById(R.id.model_phone);
        TextView pricePhoneTextView = (TextView) view.findViewById(R.id.price_phone);
        TextView inventoryPhoneTextView = (TextView) view.findViewById(R.id.inventory_phone);

        //Find the columns responsible for the fields
        int brandColumnIndex = cursor.getColumnIndex(PhoneEntry.COLUMN_PHONE_BRAND);
        int modelColumnIndex = cursor.getColumnIndex(PhoneEntry.COLUMN_PHONE_MODEL);
        int priceColumnIndex = cursor.getColumnIndex(PhoneEntry.COLUMN_PHONE_PRICE);
        int inventoryColumnIndex = cursor.getColumnIndex(PhoneEntry.COLUMN_PHONE_QUANTITY);

        //Extract the properties
        String brand = cursor.getString(brandColumnIndex);
        String model = cursor.getString(modelColumnIndex);
        int price = cursor.getInt(priceColumnIndex);
        String priceStatement = "$" + String.valueOf(price);
        int inventoryStock = cursor.getInt(inventoryColumnIndex);
        String inventoryStockStatement = String.valueOf(inventoryStock) + " in-stock";
        final int productId = cursor.getInt(cursor.getColumnIndex(PhoneEntry._ID));
        final int quantity = cursor.getInt(cursor.getColumnIndex(PhoneEntry.COLUMN_PHONE_QUANTITY));

        //Set the TextViews
        brandPhoneTextView.setText(brand);
        modelPhoneTextView.setText(model);
        pricePhoneTextView.setText(priceStatement);
        inventoryPhoneTextView.setText(inventoryStockStatement);

        //Reference to the sales Button for the user to buy the phone
        Button buyPhoneButton = (Button) view.findViewById(R.id.buy_button_phone);
        buyPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri itemUri = ContentUris.withAppendedId(PhoneEntry.CONTENT_URI, productId);
                buyProduct(context, itemUri, quantity);
            }
        });
    }

        // Decrease product count by 1
        private void buyProduct(Context context, Uri itemUri, int currentCount) {
            int newCount = (currentCount >= 1) ? currentCount - 1 : 0;
            ContentValues values = new ContentValues();
            values.put(PhoneEntry.COLUMN_PHONE_QUANTITY, newCount);
            int numRowsUpdated = context.getContentResolver().update(itemUri, values, null, null);

            if (numRowsUpdated > 0) {
                Log.i(PhoneCursorAdapter.class.getName(), "Buy product successful");
            } else {
                Log.i(PhoneCursorAdapter.class.getName(), "Could not update buy product");
            }
        }

    }
