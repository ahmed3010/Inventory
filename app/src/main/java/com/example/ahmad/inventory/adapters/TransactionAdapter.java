package com.example.ahmad.inventory.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ahmad.inventory.R;
import com.example.ahmad.inventory.data.InventoryContract;


public class TransactionAdapter extends CursorAdapter {
    public TransactionAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.transactions_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView ClientNameTextView = view.findViewById(R.id.transaction_item_client_name);
        TextView dateTextView = view.findViewById(R.id.transaction_item_date);
        ImageView imageView = view.findViewById(R.id.transaction_item_image);
        TextView paidMoneyTextView = view.findViewById(R.id.transaction_item_paid_money);
        TextView productNameTextView = view.findViewById(R.id.transaction_item_product_name);
        TextView quantityTextView = view.findViewById(R.id.transaction_item_quantity);
        TextView remainingMoneyTextView = view.findViewById(R.id.transaction_item_remain_money);
        TextView totalMoneyTextView = view.findViewById(R.id.transaction_item_total_money);

        ClientNameTextView.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_CLIENT_ID)));
        dateTextView.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_DATE)));
        double paid = cursor.getDouble(cursor.getColumnIndex(InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_PAID_MONEY));
        paidMoneyTextView.setText(String.valueOf(paid));
        double remaining = cursor.getDouble(cursor.getColumnIndex(InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_REMAINING_MONEY));
        remainingMoneyTextView.setText(String.valueOf(remaining));
        productNameTextView.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex(InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_REMAINING_MONEY))));
        quantityTextView.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_PRODUCT_QUANTITY))));
        totalMoneyTextView.setText(String.valueOf((remaining + paid)));
        int type = cursor.getInt(cursor.getColumnIndex(InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_TYPE));
        switch (type) {
            case InventoryContract.TransactionEntry.TRANSACTIONS_TYPE_BUY:
                imageView.setImageResource(R.drawable.ic_arrow_downward_green_500_48dp);
                break;
            case InventoryContract.TransactionEntry.TRANSACTIONS_TYPE_SELL:
                imageView.setImageResource(R.drawable.ic_arrow_upward_red_500_48dp);
                break;
        }
    }
}
