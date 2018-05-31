package com.example.ahmad.inventory.activities;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.ahmad.inventory.R;
import com.example.ahmad.inventory.data.Constants;
import com.example.ahmad.inventory.data.InventoryContract;

import static com.example.ahmad.inventory.data.Constants.CLIENT_ADDRESS;
import static com.example.ahmad.inventory.data.Constants.CLIENT_ID;
import static com.example.ahmad.inventory.data.Constants.CLIENT_NAME;
import static com.example.ahmad.inventory.data.Constants.CLIENT_PHONE_NUMBER;
import static com.example.ahmad.inventory.data.InventoryContract.CONTENT_URI_CLIENT;

public class ClientEditActivity extends AppCompatActivity {
    private boolean editing = false;
    private EditText nameEditText;
    private EditText addressEditText;
    private EditText phoneEditText;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_edit);
        nameEditText = findViewById(R.id.edit_client_name_edit_text);
        addressEditText = findViewById(R.id.edit_client_address_edit_text);
        phoneEditText = findViewById(R.id.edit_client_phone_edit_text);
        Intent intent = getIntent();
        if (intent.getStringExtra(Constants.CLIENT_ID) != null) {
            setTitle("Edit Client");
            editing = true;
            if (intent.getStringExtra(CLIENT_NAME) != null)
                nameEditText.setText(intent.getStringExtra(CLIENT_NAME));
            if (intent.getStringExtra(CLIENT_ADDRESS) != null)
                addressEditText.setText(intent.getStringExtra(CLIENT_ADDRESS));
            if (intent.getStringExtra(CLIENT_PHONE_NUMBER) != null)
                phoneEditText.setText(intent.getStringExtra(CLIENT_PHONE_NUMBER));
            if (intent.getStringExtra(CLIENT_ID) != null) {
                id = Integer.parseInt(intent.getStringExtra(CLIENT_ID));
            }
        }else {
            setTitle("Add Client");
        }
    }

    public void save(View view) {
        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String address = addressEditText.getText().toString();

        ContentValues values = new ContentValues();
        values.put(InventoryContract.ClientsEntry.COLUMN_CLIENT_NAME, name);
        values.put(InventoryContract.ClientsEntry.COLUMN_CLIENT_ADDRESS, address);
        values.put(InventoryContract.ClientsEntry.COLUMN_CLIENT_PHONE_NUMBER, phone);
        if (editing) {
            getContentResolver().update(
                    ContentUris.withAppendedId(CONTENT_URI_CLIENT, id),
                    values,
                    null,
                    null);
        } else {
            getContentResolver().insert(CONTENT_URI_CLIENT, values);
        }
        onBackPressed();
    }

    public void cancel(View view) {

        AlertDialog dialog  = new AlertDialog.Builder(this).create();
        dialog.setTitle("Alert");
        dialog.setMessage("Are you sure you want to discard changes");
        dialog.setIcon(R.drawable.ic_warning_pink_400_48dp);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onBackPressed();
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
}
