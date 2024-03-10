package com.secutity.securenotepad;



import android.content.ContentValues;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import net.sqlcipher.database.SQLiteDatabase;
public class AddNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        final EditText editTextTitle = findViewById(R.id.editTextTitle);
        final EditText editTextContent = findViewById(R.id.editTextContent);
        Button buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString();
                String content = editTextContent.getText().toString();

                String encryptedContent = AES.encrypt(content);

                SQliteHelper helper = new SQliteHelper(AddNoteActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase("password");

                ContentValues values = new ContentValues();
                values.put("title", title);
                values.put("content", encryptedContent);

                long id = helper.insertNote(values, db);

                if (id != -1) {
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

