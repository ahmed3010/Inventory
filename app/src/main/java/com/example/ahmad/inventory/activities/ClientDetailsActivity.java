package com.example.ahmad.inventory.activities;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.ahmad.inventory.R;
import com.example.ahmad.inventory.data.InventoryContract;

import static com.example.ahmad.inventory.data.InventoryContract.CONTENT_URI_TRANSACTION;

public class ClientDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_details);
        Bundle bundle = new Bundle();
        bundle.putString("client",getIntent().getStringExtra("client"));
        getSupportLoaderManager().initLoader(1,bundle,this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(
                ClientDetailsActivity.this,
                CONTENT_URI_TRANSACTION,
                null,
                InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_CLIENT_ID,
                new String[]{args.getString("client")},
                InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_DATE);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        ListView listView = findViewById(R.id.transactions_list_view);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
