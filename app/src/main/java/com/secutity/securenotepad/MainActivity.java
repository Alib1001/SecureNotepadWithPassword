package com.secutity.securenotepad;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.sqlcipher.database.SQLiteDatabase;

public class MainActivity extends AppCompatActivity {
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Create Password");

        SQLiteDatabase.loadLibs(getApplicationContext());
        InitializeSQLCipher();

        etPassword = findViewById(R.id.password);
        Button fab = findViewById(R.id.btnSubmit);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveData_in_db();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        SQliteHelper helper = new SQliteHelper(getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase("password");

        Cursor cursor = db.rawQuery("select * from user limit 1", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String storedPassword = cursor.getString(cursor.getColumnIndex("password"));
            if (!storedPassword.isEmpty()) {
                startActivity(new Intent(this, LoginActivity.class).putExtra("password", storedPassword));
                finish();
            }

        }
    }

    private void InitializeSQLCipher() {
        try {
            AES.generateYek();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveData_in_db() throws Exception {
        String password = etPassword.getText().toString();

        SQliteHelper helper = new SQliteHelper(getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase("password");

        Cursor cursor = db.rawQuery("select * from user limit 1", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String storedPassword = cursor.getString(cursor.getColumnIndex("password"));
            if (!storedPassword.isEmpty()) {
                startActivity(new Intent(this, LoginActivity.class).putExtra("password", storedPassword));
                finish();
            } else {
                storedPassword = "";
            }



        } else {
            if (password != null) {
                password = AES.encrypt(password);
            } else {
                password = "";
            }

            ContentValues values = new ContentValues();
            values.put("password", password);

            long id = helper.insertUser(password, db);

            startActivity(new Intent(this, TaskListActivity.class).putExtra("password", password));
            finish();
        }
    }

}
