package com.example.ahmad.inventory.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ahmad.inventory.R;
import com.example.ahmad.inventory.data.Constants;
import com.example.ahmad.inventory.data.InventoryContract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.ahmad.inventory.data.Constants.PRODUCT_ID;
import static com.example.ahmad.inventory.data.Constants.PRODUCT_IMAGE_URI;
import static com.example.ahmad.inventory.data.Constants.PRODUCT_NAME;
import static com.example.ahmad.inventory.data.Constants.PRODUCT_QR_ID;
import static com.example.ahmad.inventory.data.Constants.PRODUCT_QUANTITY;
import static com.example.ahmad.inventory.data.Constants.PRODUCT_SELL_PRICE;
import static com.example.ahmad.inventory.data.Constants.PRODUCT_WEIGHT;
import static com.example.ahmad.inventory.data.Constants.PRODUCT_WEIGHT_UNIT;
import static com.example.ahmad.inventory.data.InventoryContract.CONTENT_URI_PRODUCT;

public class ProductEditActivity extends AppCompatActivity {
    private static final int CAMERA_INTENT_ID = 101;
    private static final int GALLERY_INTENT_ID = 104;
    private static final int QR_INTENT_ID = 103;
    private static final int CAMERA_PERMISSION = 100;
    private static final String EDIT_KEY = "edit";
    private static final int WRITE_EXTERNAL_PERMISSION = 106;
    private boolean editing = false;
    private EditText nameEditText;
    private EditText priceEditText;
    private EditText quantityEditText;
    private EditText qrEditText;
    private EditText weightEditText;
    private ImageView imageView;
    private Spinner spinner;
    private String mCurrentPhotoPath = "";
    public static final String FORMATS = "SCAN_FORMATS";
    private static final String TAG = "ProductEditActivity";

    private Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);
        nameEditText = findViewById(R.id.edit_product_name_edit_text);
        priceEditText = findViewById(R.id.edit_product_sell_price_edit_text);
        qrEditText = findViewById(R.id.edit_product_qr_edit_text);
        quantityEditText = findViewById(R.id.edit_product_quantity_edit_text);
        imageView = findViewById(R.id.edit_product_image);
        weightEditText = findViewById(R.id.edit_product_weight_edit_text);
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Intent intent = getIntent();

        if (intent.getStringExtra(PRODUCT_ID) != null) {
            setTitle(getString(R.string.edit_product));
            editing = true;
            if (intent.getStringExtra(PRODUCT_NAME) != null) {
                String name = intent.getStringExtra(PRODUCT_NAME);
                nameEditText.setText(name);
            }
            if (intent.getStringExtra(PRODUCT_SELL_PRICE) != null)
                priceEditText.setText(intent.getStringExtra(PRODUCT_SELL_PRICE));
            if (intent.getStringExtra(PRODUCT_QUANTITY) != null)
                quantityEditText.setText(intent.getStringExtra(PRODUCT_QUANTITY));
            if (intent.getStringExtra(PRODUCT_QR_ID) != null)
                qrEditText.setText(intent.getStringExtra(PRODUCT_QR_ID));
            if (intent.getStringExtra(PRODUCT_WEIGHT) != null)
                weightEditText.setText(intent.getStringExtra(PRODUCT_WEIGHT));
            if (intent.getStringExtra(PRODUCT_ID) != null) {
                id = Long.parseLong(intent.getStringExtra(PRODUCT_ID));
            }
            if (intent.getStringExtra(PRODUCT_IMAGE_URI) != null) {
                mCurrentPhotoPath = intent.getStringExtra(PRODUCT_IMAGE_URI);
            }
            if (intent.getIntExtra(PRODUCT_WEIGHT_UNIT, 0) != 0) {
                spinner.setSelection(intent.getIntExtra(PRODUCT_WEIGHT_UNIT, 0));
            }
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        } else {
            setTitle(getString(R.string.edit_product));
        }
    }

    public void save(View view) {
        String name = nameEditText.getText().toString();
        String price = priceEditText.getText().toString();
        String qrCode = qrEditText.getText().toString();
        String quantity = quantityEditText.getText().toString();
        String weight = weightEditText.getText().toString();
        resize();
        ContentValues values = new ContentValues();
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME, name);
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_IMAGE_URI, mCurrentPhotoPath);
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_WEIGHT, weight);
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_SELL_PRICE, price);
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_QR_ID, qrCode);
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_WEIGHT_UNIT, spinner.getSelectedItemPosition());

        if (editing) {
            getContentResolver().update(
                    ContentUris.withAppendedId(CONTENT_URI_PRODUCT, id),
                    values,
                    null,
                    null);
        } else {
            getContentResolver().insert(CONTENT_URI_PRODUCT, values);
        }
        finish();
    }

    private void saveImage(Bitmap bitmap) {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fos;
        try {
            if (image != null) {
                fos = new FileOutputStream(image);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                fos.close();
            }

        } catch (FileNotFoundException e) {
            Log.e(TAG, "saveImage: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (image != null) {
            mCurrentPhotoPath = image.getAbsolutePath();
        }
    }

    public void cancel(View view) {
        if (isDataChanged()) {
            showDialog();
        } else {
            finish();
        }
    }

    private boolean isDataChanged() {
        if (editing) {
            String currentName = nameEditText.getText().toString();
            String currentPrice = priceEditText.getText().toString();
            String currentQrCode = qrEditText.getText().toString();
            String currentQuantity = quantityEditText.getText().toString();
            String currentWeight = weightEditText.getText().toString();
            String currentImage = mCurrentPhotoPath;

            String name = "";
            String price = "";
            String qrCode = "";
            String quantity = "";
            String weight = "";
            String image = "";

            Intent intent = getIntent();
            if (intent.getStringExtra(PRODUCT_NAME) != null) {
                name = intent.getStringExtra(PRODUCT_NAME);
            }
            if (intent.getStringExtra(PRODUCT_SELL_PRICE) != null)
                price = intent.getStringExtra(PRODUCT_SELL_PRICE);
            if (intent.getStringExtra(PRODUCT_QUANTITY) != null)
                quantity = intent.getStringExtra(PRODUCT_QUANTITY);
            if (intent.getStringExtra(PRODUCT_QR_ID) != null)
                qrCode = intent.getStringExtra(PRODUCT_QR_ID);
            if (intent.getStringExtra(PRODUCT_WEIGHT) != null)
                weight = intent.getStringExtra(PRODUCT_WEIGHT);
            if (intent.getStringExtra(PRODUCT_IMAGE_URI) != null) {
                image = intent.getStringExtra(PRODUCT_IMAGE_URI);
            }
            return
                    !(currentName.equalsIgnoreCase(name) &&
                            currentPrice.equalsIgnoreCase(price) &&
                            currentImage.equalsIgnoreCase(image) &&
                            currentQrCode.equalsIgnoreCase(qrCode) &&
                            currentQuantity.equalsIgnoreCase(quantity) &&
                            currentWeight.equalsIgnoreCase(weight));


        } else {
            return
                    !(TextUtils.isEmpty(nameEditText.getText().toString()) &&
                            TextUtils.isEmpty(qrEditText.getText().toString()) &&
                            TextUtils.isEmpty(quantityEditText.getText().toString()) &&
                            TextUtils.isEmpty(priceEditText.getText().toString()) &&
                            TextUtils.isEmpty(weightEditText.getText().toString()));
        }
    }

    @Override
    public void onBackPressed() {
        if (isDataChanged()) {
            showDialog();
        } else {
            finish();
        }
    }

    private void showDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle(getString(R.string.discard_dialog_title));
        dialog.setMessage(getString(R.string.discard_dialog));
        dialog.setIcon(R.drawable.ic_warning_pink_400_48dp);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (!mCurrentPhotoPath.equalsIgnoreCase(getIntent().getStringExtra(PRODUCT_IMAGE_URI))) {
                            File file = new File(mCurrentPhotoPath);
                            if (file.exists()) {
                                boolean delete = file.delete();
                                Log.d(TAG, "Dialog Cancel button onClick: delete " + delete);
                            }
                        }
                        finish();
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

    public void chooseImageFromCamera(View view) {
        boolean permissionNotGranted =
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission
                                (this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED;
        // Here, thisActivity is the current activity
        if (permissionNotGranted) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        CAMERA_PERMISSION);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        CAMERA_PERMISSION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            captureCameraImage();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) &&
                        (grantResults.length > 2 && grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                    captureCameraImage();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            case WRITE_EXTERNAL_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImage();
                }
        }
    }

    public void chooseImageFromGallery(View view) {
        boolean permissionNotGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
        // Here, thisActivity is the current activity
        if (permissionNotGranted) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_PERMISSION);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_PERMISSION);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            chooseImage();
        }

    }

    private void chooseImage() {

        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_INTENT_ID);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void captureCameraImage() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File

        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider",
                    photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        }
        startActivityForResult(cameraIntent, CAMERA_INTENT_ID);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_INTENT_ID:
                if (resultCode == Activity.RESULT_OK) {
                    resize();
                } else {
                    File file = new File(mCurrentPhotoPath);
                    if (file.exists()) {
                        boolean delete = file.delete();
                        Log.d(TAG, "onActivityResult: Camera result not found temp file deleted " + delete);
                    }
                    if (editing) {
                        mCurrentPhotoPath = getIntent().getStringExtra(PRODUCT_IMAGE_URI);
                    }
                }
                break;
            case QR_INTENT_ID:
                if (resultCode == Activity.RESULT_OK) {
                    String contents = data.getStringExtra("SCAN_RESULT");
                    qrEditText.setText(contents);
                }
                break;
            case GALLERY_INTENT_ID:
                if (resultCode == Activity.RESULT_OK) {
                    if (null != data && null != data.getData()) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            mCurrentPhotoPath = cursor.getString(columnIndex);
                            cursor.close();
                        }
                    }
                    setPic();
                }
                break;
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "SuspiciousNameCombination"})
    private void resize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels / 2;
        int width = displayMetrics.widthPixels / 2;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / width, photoH / height);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        if (bitmap != null) {
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, height, height, false);
            File file = new File(mCurrentPhotoPath);
            file.delete();
            saveImage(scaled);
            imageView.setImageBitmap(scaled);
        }
    }

    public void getQRCode(View view) {
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra(FORMATS, "EAN_13,EAN_8,QR_CODE,CODE_128,DATA_MATRIX"); // "PRODUCT_MODE for bar codes
            startActivityForResult(intent, QR_INTENT_ID);
        } catch (Exception e) {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);
        }
    }

    private void setPic() {
        // Get the dimensions of the View

        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.PRODUCT_IMAGE_URI, mCurrentPhotoPath);
        outState.putBoolean(EDIT_KEY, editing);
        if (id != null) {
            outState.putLong(Constants.PRODUCT_ID, id);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mCurrentPhotoPath = savedInstanceState.getString(PRODUCT_IMAGE_URI);
        editing = savedInstanceState.getBoolean(EDIT_KEY);
        id = savedInstanceState.getLong(PRODUCT_ID);
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        if (!TextUtils.isEmpty(savedInstanceState.getCharSequence(PRODUCT_IMAGE_URI))) {
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    setPic();
                    ViewTreeObserver obs = imageView.getViewTreeObserver();
                    obs.removeOnGlobalLayoutListener(this);
                }
            });
        }
    }
}
