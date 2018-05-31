package com.example.ahmad.inventory.activities;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ahmad.inventory.adapters.ClientAdapter;
import com.example.ahmad.inventory.R;
import com.example.ahmad.inventory.data.Constants;
import com.example.ahmad.inventory.data.InventoryContract;

import static com.example.ahmad.inventory.data.InventoryContract.CONTENT_URI_CLIENT;
import static com.example.ahmad.inventory.data.InventoryContract.CONTENT_URI_PRODUCT;
import static com.example.ahmad.inventory.data.InventoryContract.CONTENT_URI_TRANSACTION;


public class ClientActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ClientActivity";
    private static final int CLIENT_LOADER_ID = 0;
    private static final int PRODUCT_LOADER_ID = 1;
    private static final int TRANSACTION_LOADER_ID = 2;
    private ListView listView;
    private ClientAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.list_view);
        registerForContextMenu(listView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClientActivity.this,ClientEditActivity.class);
                startActivity(intent);
            }
        });
        getSupportLoaderManager().initLoader(CLIENT_LOADER_ID,null,this);

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.list_view) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            Cursor cursor = (Cursor)adapter.getItem(info.position);
            menu.setHeaderTitle(cursor.getString(cursor.getColumnIndex(InventoryContract.ClientsEntry.COLUMN_CLIENT_NAME)));
            menu.setHeaderIcon(R.mipmap.ic_launcher);
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        Cursor cursor = (Cursor)adapter.getItem(info.position);
        final String id = (cursor.getString(cursor.getColumnIndex(InventoryContract.ClientsEntry._ID)));
        String name = (cursor.getString(cursor.getColumnIndex(InventoryContract.ClientsEntry.COLUMN_CLIENT_NAME)));
        String address = (cursor.getString(cursor.getColumnIndex(InventoryContract.ClientsEntry.COLUMN_CLIENT_ADDRESS)));
        String phone=(cursor.getString(cursor.getColumnIndex(InventoryContract.ClientsEntry.COLUMN_CLIENT_PHONE_NUMBER)));
        String credit =(String.valueOf(cursor.getInt(cursor.getColumnIndex(InventoryContract.ClientsEntry.COLUMN_CLIENT_CREDIT))));
        String debit = (String.valueOf(cursor.getInt(cursor.getColumnIndex(InventoryContract.ClientsEntry.COLUMN_CLIENT_DEBIT))));
        String[] menuItems = getResources().getStringArray(R.array.menu);
        String menuItemName = menuItems[menuItemIndex];
        if (menuItemName.equalsIgnoreCase("edit")){

            Intent intent =new Intent(ClientActivity.this,ClientEditActivity.class);
            intent.putExtra(Constants.CLIENT_ID,id);
            intent.putExtra(Constants.CLIENT_NAME,name);
            intent.putExtra(Constants.CLIENT_ADDRESS,address);
            intent.putExtra(Constants.CLIENT_PHONE_NUMBER,phone);
            intent.putExtra(Constants.CLIENT_CREDIT,credit);
            intent.putExtra(Constants.CLIENT_DEBIT,debit);
            startActivity(intent);
        }
        if (menuItemName.equalsIgnoreCase("delete")){
            AlertDialog dialog  = new AlertDialog.Builder(this).create();
            dialog.setTitle("Alert");
            dialog.setMessage("Are you sure you want to delete this client");
            dialog.setIcon(R.drawable.ic_warning_pink_400_48dp);
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getContentResolver().delete(
                                    ContentUris.withAppendedId(CONTENT_URI_CLIENT, Long.parseLong(id)),
                                    null,
                                    null
                            );
                        }
                    });
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();

        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id){
            case CLIENT_LOADER_ID :
                String[] projection = {InventoryContract.ClientsEntry._ID,
                        InventoryContract.ClientsEntry.COLUMN_CLIENT_NAME,
                        InventoryContract.ClientsEntry.COLUMN_CLIENT_PHONE_NUMBER,
                        InventoryContract.ClientsEntry.COLUMN_CLIENT_ADDRESS,
                        InventoryContract.ClientsEntry.COLUMN_CLIENT_DEBIT,
                        InventoryContract.ClientsEntry.COLUMN_CLIENT_CREDIT,
                };
                return new android.support.v4.content.CursorLoader(
                        ClientActivity.this,
                        CONTENT_URI_CLIENT,
                        projection,
                        null,
                        null,
                        InventoryContract.ClientsEntry.COLUMN_CLIENT_NAME + " COLLATE NOCASE");
            case PRODUCT_LOADER_ID :
                return new android.support.v4.content.CursorLoader(
                        ClientActivity.this,
                        CONTENT_URI_PRODUCT,
                        null,
                        null,
                        null,
                        InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME + " COLLATE NOCASE");
            case TRANSACTION_LOADER_ID:
                return new android.support.v4.content.CursorLoader(
                        ClientActivity.this,
                        CONTENT_URI_TRANSACTION,
                        null,
                        null,
                        null,
                        InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_DATE);
            default:
                throw new IllegalArgumentException("Unknown id " + id);

        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        adapter = new ClientAdapter(this,data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long i) {
                Cursor cursor = (Cursor) adapter.getItem(position);
                String id = (cursor.getString(cursor.getColumnIndex(InventoryContract.ClientsEntry._ID)));
                String name = (cursor.getString(cursor.getColumnIndex(InventoryContract.ClientsEntry.COLUMN_CLIENT_NAME)));
                String address = (cursor.getString(cursor.getColumnIndex(InventoryContract.ClientsEntry.COLUMN_CLIENT_ADDRESS)));
                String phone=(cursor.getString(cursor.getColumnIndex(InventoryContract.ClientsEntry.COLUMN_CLIENT_PHONE_NUMBER)));
                String credit =(String.valueOf(cursor.getInt(cursor.getColumnIndex(InventoryContract.ClientsEntry.COLUMN_CLIENT_CREDIT))));
                String debit = (String.valueOf(cursor.getInt(cursor.getColumnIndex(InventoryContract.ClientsEntry.COLUMN_CLIENT_DEBIT))));
                Intent intent =new Intent(ClientActivity.this,ClientEditActivity.class);
                intent.putExtra(Constants.CLIENT_ID,id);
                intent.putExtra(Constants.CLIENT_NAME,name);
                intent.putExtra(Constants.CLIENT_ADDRESS,address);
                intent.putExtra(Constants.CLIENT_PHONE_NUMBER,phone);
                intent.putExtra(Constants.CLIENT_CREDIT,credit);
                intent.putExtra(Constants.CLIENT_DEBIT,debit);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
