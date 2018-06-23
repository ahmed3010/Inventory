package com.example.ahmad.inventory.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.ahmad.inventory.R;
import com.example.ahmad.inventory.adapters.TransactionAdapter;
import com.example.ahmad.inventory.data.InventoryContract;

public class TransactionsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "TransactionsActivity";
    TransactionAdapter adapter;
    private boolean fromActivity = false;
    private Uri queryUri;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        listView = findViewById(R.id.list_view);
        Intent intent = getIntent();
        queryUri = intent.getData();
        if (queryUri != null) {
            fromActivity = true;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (fromActivity) {
            return new CursorLoader(
                    this,
                    queryUri,
                    null,
                    null,
                    null,
                    InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_DATE);
        } else {
            return new CursorLoader(
                    this,
                    InventoryContract.CONTENT_URI_TRANSACTION,
                    null,
                    null,
                    null,
                    InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_DATE);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (adapter == null) {
            adapter = new TransactionAdapter(this, data);
            listView.setAdapter(adapter);
        } else {
            adapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (adapter != null) {
            adapter.swapCursor(null);
        }
    }
}
