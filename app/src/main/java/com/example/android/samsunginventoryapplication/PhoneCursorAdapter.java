package com.example.android.samsunginventoryapplication;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public void bindView(View view, Context context, Cursor cursor) {
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

        //Set the TextViews
        brandPhoneTextView.setText(brand);
        modelPhoneTextView.setText(model);
        pricePhoneTextView.setText(priceStatement);
        inventoryPhoneTextView.setText(inventoryStockStatement);
    }
}
