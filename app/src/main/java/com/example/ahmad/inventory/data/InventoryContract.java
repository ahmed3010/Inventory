package com.example.ahmad.inventory.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {
    private InventoryContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.ahmad.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CLIENTS = ClientsEntry.TABLE_NAME;
    public static final String PATH_PRODUCT = ProductEntry.TABLE_NAME;
    public static final String PATH_TRANSACTIONS = TransactionEntry.TABLE_NAME;
    public static final Uri CONTENT_URI_CLIENT = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CLIENTS);
    public static final Uri CONTENT_URI_PRODUCT = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);
    public static final Uri CONTENT_URI_TRANSACTION = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TRANSACTIONS);

    public static final class ClientsEntry implements BaseColumns {


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLIENTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLIENTS;
        public final static String TABLE_NAME = "clients";
        public final static String _ID = BaseColumns._ID;
        public static final String COLUMN_CLIENT_NAME = "name";
        public static final String COLUMN_CLIENT_ADDRESS = "address";
        public static final String COLUMN_CLIENT_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_CLIENT_DEBIT = "debit";
        public static final String COLUMN_CLIENT_CREDIT = "credit";

    }

    public static final class ProductEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;
        public final static String TABLE_NAME = "PRODUCT";
        public final static String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_WEIGHT = "weight";
        public static final String COLUMN_PRODUCT_WEIGHT_UNIT = "weight_unit";
        public static final String COLUMN_PRODUCT_IMAGE_URI = "image";
        public static final String COLUMN_PRODUCT_QR_ID = "qr_id";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_BUY_PRICE = "buy_price";
        public static final String COLUMN_PRODUCT_SELL_PRICE = "sell_price";

    }

    public static final class TransactionEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSACTIONS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSACTIONS;
        public final static String TABLE_NAME = "TRANSACTIONS";
        public final static String _ID = BaseColumns._ID;
        public static final String COLUMN_TRANSACTIONS_DATE = "date";
        public static final String COLUMN_TRANSACTIONS_TYPE = "name";
        public static final String COLUMN_TRANSACTIONS_CLIENT_ID = "client_name";
        public static final String COLUMN_TRANSACTIONS_PRODUCT_ID = "product_name";
        public static final String COLUMN_TRANSACTIONS_PRODUCT_QUANTITY = "product_quantity";
        public static final String COLUMN_TRANSACTIONS_PAID_MONEY = "paid_money";
        public static final String COLUMN_TRANSACTIONS_REMAINING_MONEY = "remaining_money";

        public static final int TRANSACTIONS_TYPE_BUY = 0;
        public static final int TRANSACTIONS_TYPE_SELL = 1;

        public static Uri getTransactionByProduct(Long productID) {
            Uri uri = CONTENT_URI_TRANSACTION.buildUpon().appendPath(ProductEntry.TABLE_NAME).build();
            return ContentUris.withAppendedId(uri, productID);
        }

        public static Uri getTransactionByClient(Long clientId) {
            Uri uri = CONTENT_URI_TRANSACTION.buildUpon().appendPath(ClientsEntry.TABLE_NAME).build();
            return ContentUris.withAppendedId(uri, clientId);
        }

    }

}
