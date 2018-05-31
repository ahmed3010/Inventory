/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.ahmad.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.ahmad.inventory.data.InventoryContract.ClientsEntry;
import com.example.ahmad.inventory.data.InventoryContract.ProductEntry;
import com.example.ahmad.inventory.data.InventoryContract.TransactionEntry;

import static com.example.ahmad.inventory.data.InventoryContract.CONTENT_AUTHORITY;
import static com.example.ahmad.inventory.data.InventoryContract.PATH_CLIENTS;
import static com.example.ahmad.inventory.data.InventoryContract.PATH_PRODUCT;
import static com.example.ahmad.inventory.data.InventoryContract.PATH_TRANSACTIONS;

/**
 * {@link ContentProvider} for Pets app.
 */
public class InventoryProvider extends ContentProvider {

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private static final int CLIENT = 100;
    private static final int CLIENT_ID = 101;

    private static final int PRODUCT = 200;
    private static final int PRODUCT_ID = 201;

    private static final int TRANSACTIONS = 300;
    private static final int TRANSACTIONS_ID = 301;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {

        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_CLIENTS, CLIENT);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_CLIENTS + "/#", CLIENT_ID);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PRODUCT, PRODUCT);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PRODUCT + "/#", PRODUCT_ID);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_TRANSACTIONS, TRANSACTIONS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_TRANSACTIONS + "/#", TRANSACTIONS_ID);

    }

    /**
     * Database helper object
     */
    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIENT:
                cursor = database.query(ClientsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CLIENT_ID:
                selection = ClientsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ClientsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case PRODUCT:
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case TRANSACTIONS:
                cursor = database.query(TransactionEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TRANSACTIONS_ID:
                selection = TransactionEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TransactionEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIENT:
                return insertClient(uri, contentValues);
            case PRODUCT:
                return insertProduct(uri, contentValues);
            case TRANSACTIONS:
                return insertTransaction(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        Integer weight = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_WEIGHT);
        if (weight == null) {
//            throw new IllegalArgumentException("Pet requires valid gender");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(ProductEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertClient(Uri uri, ContentValues values) {

        String name = values.getAsString(ClientsEntry.COLUMN_CLIENT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(ClientsEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertTransaction(Uri uri, ContentValues values) {
        String date = values.getAsString(TransactionEntry.COLUMN_TRANSACTIONS_DATE);
        if (date == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }
        String clientName = values.getAsString(TransactionEntry.COLUMN_TRANSACTIONS_CLIENT_ID);
        if (clientName == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        String ProductName = values.getAsString(TransactionEntry.COLUMN_TRANSACTIONS_PRODUCT_ID);
        if (ProductName == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        Integer transactionType = values.getAsInteger(TransactionEntry.COLUMN_TRANSACTIONS_TYPE);
        if (transactionType == null || (transactionType != TransactionEntry.TRANSACTIONS_TYPE_BUY && transactionType != TransactionEntry.TRANSACTIONS_TYPE_SELL)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }

        Integer quantity = values.getAsInteger(TransactionEntry.COLUMN_TRANSACTIONS_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }
        Integer paidMoney = values.getAsInteger(TransactionEntry.COLUMN_TRANSACTIONS_PAID_MONEY);
        if (paidMoney != null && paidMoney < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }
        Integer remainingMoney = values.getAsInteger(TransactionEntry.COLUMN_TRANSACTIONS_REMAINING_MONEY);
        if (remainingMoney != null && remainingMoney < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(ClientsEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIENT:
                return updateClient(uri, contentValues, selection, selectionArgs);
            case CLIENT_ID:
                selection = ClientsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateClient(uri, contentValues, selection, selectionArgs);
            case PRODUCT:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ClientsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case TRANSACTIONS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case TRANSACTIONS_ID:
                selection = ClientsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateTransaction(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updateTransaction(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(TransactionEntry.COLUMN_TRANSACTIONS_DATE)) {
            String date = values.getAsString(TransactionEntry.COLUMN_TRANSACTIONS_DATE);
            if (date == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        if (values.containsKey(TransactionEntry.COLUMN_TRANSACTIONS_CLIENT_ID)) {
            String clientName = values.getAsString(TransactionEntry.COLUMN_TRANSACTIONS_CLIENT_ID);
            if (clientName == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        if (values.containsKey(TransactionEntry.COLUMN_TRANSACTIONS_PRODUCT_ID)) {

            String ProductName = values.getAsString(TransactionEntry.COLUMN_TRANSACTIONS_PRODUCT_ID);
            if (ProductName == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        if (values.containsKey(TransactionEntry.COLUMN_TRANSACTIONS_TYPE)) {
            Integer transactionType = values.getAsInteger(TransactionEntry.COLUMN_TRANSACTIONS_TYPE);
            if (transactionType == null || (transactionType != TransactionEntry.TRANSACTIONS_TYPE_BUY && transactionType != TransactionEntry.TRANSACTIONS_TYPE_SELL)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }
        if (values.containsKey(TransactionEntry.COLUMN_TRANSACTIONS_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(TransactionEntry.COLUMN_TRANSACTIONS_PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }
        if (values.containsKey(TransactionEntry.COLUMN_TRANSACTIONS_PAID_MONEY)) {
            Integer paidMoney = values.getAsInteger(TransactionEntry.COLUMN_TRANSACTIONS_PAID_MONEY);
            if (paidMoney != null && paidMoney < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }
        if (values.containsKey(TransactionEntry.COLUMN_TRANSACTIONS_REMAINING_MONEY)) {
            Integer remainingMoney = values.getAsInteger(TransactionEntry.COLUMN_TRANSACTIONS_REMAINING_MONEY);
            if (remainingMoney != null && remainingMoney < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ClientsEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_WEIGHT)) {
            Double weight = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_WEIGHT);
            if (weight == null) {
                throw new IllegalArgumentException("Product must has weight");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    private int updateClient(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(ClientsEntry.COLUMN_CLIENT_NAME)) {
            String name = values.getAsString(ClientsEntry.COLUMN_CLIENT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ClientsEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIENT:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ClientsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CLIENT_ID:
                // Delete a single row given by the ID in the URI
                selection = ClientsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ClientsEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case PRODUCT:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = TransactionEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case TRANSACTIONS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(TransactionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRANSACTIONS_ID:
                // Delete a single row given by the ID in the URI
                selection = TransactionEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TransactionEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CLIENT:
                return ClientsEntry.CONTENT_LIST_TYPE;
            case CLIENT_ID:
                return ClientsEntry.CONTENT_ITEM_TYPE;
            case PRODUCT:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            case TRANSACTIONS:
                return TransactionEntry.CONTENT_LIST_TYPE;
            case TRANSACTIONS_ID:
                return TransactionEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
