package com.example.ahmad.inventory.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ahmad.inventory.R;
import com.example.ahmad.inventory.data.InventoryContract;


public class ClientAdapter extends CursorAdapter {
    public ClientAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.client_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.client_name_text_view);
        TextView phoneTextView = view.findViewById(R.id.client_phone_text_view);
        TextView total = view.findViewById(R.id.client_total_text_view);
        TextView debitTextView = view.findViewById(R.id.client_debit_text_view);
        TextView creditTextView = view.findViewById(R.id.client_credit_text_view);

        nameTextView.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.ClientsEntry.COLUMN_CLIENT_NAME)));
        phoneTextView.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.ClientsEntry.COLUMN_CLIENT_PHONE_NUMBER)));
        creditTextView.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(InventoryContract.ClientsEntry.COLUMN_CLIENT_CREDIT))));
        debitTextView.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(InventoryContract.ClientsEntry.COLUMN_CLIENT_DEBIT))));
        String sTotal = String.valueOf(Integer.parseInt(creditTextView.getText().toString()) + Integer.parseInt(debitTextView.getText().toString()));
        total.setText(sTotal);

    }
}
