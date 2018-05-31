package com.example.ahmad.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventoryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;


    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_CLIENT_TABLE ="CREATE TABLE "+InventoryContract.ClientsEntry.TABLE_NAME+ " ("
                + InventoryContract.ClientsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.ClientsEntry.COLUMN_CLIENT_NAME + " TEXT NOT NULL, "
                + InventoryContract.ClientsEntry.COLUMN_CLIENT_ADDRESS + " TEXT, "
                + InventoryContract.ClientsEntry.COLUMN_CLIENT_PHONE_NUMBER + " TEXT , "
                + InventoryContract.ClientsEntry.COLUMN_CLIENT_CREDIT + " INTEGER NOT NULL DEFAULT 0 , "
                + InventoryContract.ClientsEntry.COLUMN_CLIENT_DEBIT + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_CLIENT_TABLE);

        String SQL_CREATE_PRODUCT_TABLE ="CREATE TABLE "+InventoryContract.ProductEntry.TABLE_NAME+ " ("
                + InventoryContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryContract.ProductEntry.COLUMN_PRODUCT_WEIGHT + " REAL  NOT NULL, "
                + InventoryContract.ProductEntry.COLUMN_PRODUCT_WEIGHT_UNIT + " INTEGER NOT NULL, "
                + InventoryContract.ProductEntry.COLUMN_PRODUCT_QR_ID + " TEXT NOT NULL, "
                + InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryContract.ProductEntry.COLUMN_PRODUCT_IMAGE_URI + " TEXT Default \"0\", "
                + InventoryContract.ProductEntry.COLUMN_PRODUCT_BUY_PRICE + " INTEGER NOT NULL DEFAULT 0 , "
                + InventoryContract.ProductEntry.COLUMN_PRODUCT_SELL_PRICE + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_PRODUCT_TABLE);

        String SQL_CREATE_TRANSACTIONS_TABLE ="CREATE TABLE "+InventoryContract.TransactionEntry.TABLE_NAME+ " ("
                + InventoryContract.TransactionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_DATE + " TEXT NOT NULL, "
                + InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_TYPE + " INTEGER NOT NULL, "
                + InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_CLIENT_ID + " INTEGER  NOT NULL DEFAULT 0, "
                + InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_PRODUCT_ID + " INTEGR NOT NULL, "
                + InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_PAID_MONEY + " INTEGER NOT NULL, "
                + InventoryContract.TransactionEntry.COLUMN_TRANSACTIONS_REMAINING_MONEY + " INTEGER NOT NULL);";
        db.execSQL(SQL_CREATE_TRANSACTIONS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
