package com.example.ahmad.inventory.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ahmad.inventory.R;

import static com.example.ahmad.inventory.data.InventoryContract.ProductEntry.COLUMN_PRODUCT_IMAGE_URI;
import static com.example.ahmad.inventory.data.InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME;
import static com.example.ahmad.inventory.data.InventoryContract.ProductEntry.COLUMN_PRODUCT_QR_ID;
import static com.example.ahmad.inventory.data.InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY;


public class ProductAdapter extends CursorAdapter {
    private Activity activity;

    public ProductAdapter(Activity activity, Cursor c) {
        super(activity, c, 0);
        this.activity = activity;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor c) {
        final TextView name = view.findViewById(R.id.product_item_name_text_view);
        TextView id = view.findViewById(R.id.product_item_id_text_view);
        TextView quantity = view.findViewById(R.id.product_item_quantity_text_view);
        Button buy = view.findViewById(R.id.product_item_buy_button);
        Button sell = view.findViewById(R.id.product_item_sell_button);
        final ImageView image = view.findViewById(R.id.product_item_image);
        name.setText(c.getString(c.getColumnIndex(COLUMN_PRODUCT_NAME)));
        quantity.setText(c.getString(c.getColumnIndex(COLUMN_PRODUCT_QUANTITY)));
        final String imageUri = c.getString(c.getColumnIndex(COLUMN_PRODUCT_IMAGE_URI));
        if (!"0".equalsIgnoreCase(imageUri) && imageUri != null) {
            String path = Uri.parse(imageUri).getPath();
            setThumbnail(path, image);
        }
        id.setText(c.getString(c.getColumnIndex(COLUMN_PRODUCT_QR_ID)));
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO implement this
            }
        });
        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO implement this
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.image_dialog);
                ImageButton close = dialog.findViewById(R.id.btnClose);
                final ImageView dialogImage = dialog.findViewById(R.id.dialog_image);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                setPic(imageUri, dialogImage);
                dialog.show();
            }
        });

    }

    private void setPic(String path, ImageView imageView) {
        // Get the dimensions of the View
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int targetH = imageView.getMaxHeight();
        int targetW = imageView.getMaxWidth();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        imageView.setImageBitmap(bitmap);
    }
    private void setThumbnail(String path, ImageView imageView) {
        // Get the dimensions of the View
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int targetH = displayMetrics.heightPixels / 2;
        int targetW = displayMetrics.widthPixels / 2;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

}
