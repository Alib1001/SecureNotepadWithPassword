package com.secutity.securenotepad;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

public class EditNoteActivity extends AppCompatActivity {
    private EditText editTextTitle;
    private EditText editTextContent;
    private Button buttonSave;
    private long noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        buttonSave = findViewById(R.id.buttonSave);

        noteId = getIntent().getLongExtra("noteId", -1);

        if (noteId != -1) {
            loadNoteFromDatabase();
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNoteInDatabase();

                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

    }



    private void loadNoteFromDatabase() {
        SQliteHelper helper = new SQliteHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase("password");

        Cursor cursor = helper.getNoteById(noteId, db);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
            @SuppressLint("Range") String encryptedContent = cursor.getString(cursor.getColumnIndex("content"));

            String content = AES.decrypt(encryptedContent);

            editTextTitle.setText(title);
            editTextContent.setText(content);

            cursor.close();
        }
    }

    private void updateNoteInDatabase() {
        String updatedTitle = editTextTitle.getText().toString();
        String updatedContent = editTextContent.getText().toString();

        SQliteHelper helper = new SQliteHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase("password");

        ContentValues values = new ContentValues();
        values.put("title", updatedTitle);
        values.put("content", AES.encrypt(updatedContent));

        int rowsUpdated = helper.updateNoteById(noteId, values, db);

        if (rowsUpdated > 0) {
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
        }

        finish();

    }
}
