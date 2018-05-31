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
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ahmad.inventory.R;
import com.example.ahmad.inventory.adapters.ProductAdapter;
import com.example.ahmad.inventory.data.Constants;
import com.example.ahmad.inventory.data.InventoryContract;

import java.io.File;

import static com.example.ahmad.inventory.data.InventoryContract.CONTENT_URI_PRODUCT;


public class ProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ProductActivity";
    private static final int Product_LOADER_ID = 1;

    private ListView listView;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.list_view);
        registerForContextMenu(listView);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductActivity.this, ProductEditActivity.class);
                startActivity(intent);
            }
        });
        getSupportLoaderManager().initLoader(Product_LOADER_ID, null, this);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list_view) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Cursor cursor = (Cursor) adapter.getItem(info.position);
            menu.setHeaderTitle(cursor.getString(cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME)));
            menu.setHeaderIcon(R.mipmap.ic_launcher);
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);

            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        Cursor cursor = (Cursor) adapter.getItem(info.position);
        final String id = (cursor.getString(cursor.getColumnIndex(InventoryContract.ProductEntry._ID)));
        String name = (cursor.getString(cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME)));
        String quantity = (cursor.getString(cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)));
        String qr_id = (cursor.getString(cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_QR_ID)));
        final String image = ((cursor.getString(cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_IMAGE_URI))));
        String sellPrice = (String.valueOf(cursor.getInt(cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_SELL_PRICE))));
        String weight = (String.valueOf(cursor.getInt(cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_WEIGHT))));
        int unit = (cursor.getInt(cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_WEIGHT_UNIT)));
        String[] menuItems = getResources().getStringArray(R.array.menu);
        String menuItemName = menuItems[menuItemIndex];
        if (menuItemName.equalsIgnoreCase(getString(R.string.edit))) {

            Intent intent = new Intent(ProductActivity.this, ProductEditActivity.class);
            intent.putExtra(Constants.PRODUCT_ID, id);
            intent.putExtra(Constants.PRODUCT_NAME, name);
            intent.putExtra(Constants.PRODUCT_IMAGE_URI, image);
            intent.putExtra(Constants.PRODUCT_QR_ID, qr_id);
            intent.putExtra(Constants.PRODUCT_QUANTITY, quantity);
            intent.putExtra(Constants.PRODUCT_WEIGHT, weight);
            intent.putExtra(Constants.PRODUCT_WEIGHT_UNIT, unit);
            intent.putExtra(Constants.PRODUCT_SELL_PRICE, sellPrice);
            startActivity(intent);
        }
        if (menuItemName.equalsIgnoreCase(getString(R.string.delete))) {
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setTitle(getString(R.string.discard_dialog_title));
            dialog.setMessage(getString(R.string.dialog_delete_warning));
            dialog.setIcon(R.drawable.ic_warning_pink_400_48dp);
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            File file = new File(image);
                            if (file.exists()) {
                                boolean delete = file.delete();
                                Log.i(TAG, "onClick: image deleted" + delete);
                            }
                            getContentResolver().delete(
                                    ContentUris.withAppendedId(CONTENT_URI_PRODUCT, Long.parseLong(id)),
                                    null,
                                    null
                            );
                        }
                    });
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no),
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
        return new android.support.v4.content.CursorLoader(
                ProductActivity.this,
                CONTENT_URI_PRODUCT,
                null,
                null,
                null,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME + " COLLATE NOCASE");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (adapter==null) {
            adapter = new ProductAdapter(this, data);
        }else {
            adapter.swapCursor(data);
        }
        listView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (adapter != null) {
            adapter.swapCursor(null);
        }
    }
}
