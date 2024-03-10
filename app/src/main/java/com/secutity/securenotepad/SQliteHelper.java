package com.secutity.securenotepad;

import android.content.ContentValues;
import android.content.Context;
import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
public class SQliteHelper extends SQLiteOpenHelper {
    public static final String dbName = "notepaddb";
    public static final int version = 6;

    public SQliteHelper(Context context) {
        super(context, dbName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String userTableQuery = "create table if not exists user(_id integer primary key autoincrement, password text, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(userTableQuery);

        String notesTableQuery = "create table if not exists notes(_id integer primary key autoincrement, title text, content text, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(notesTableQuery);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            String notesTableQuery = "create table if not exists notes(_id integer primary key autoincrement, title text, content text, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";
            db.execSQL(notesTableQuery);
        }
    }


    public long insertUser(String password, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("password", password);
        return db.insert("user", null, values);
    }

    public long insertNote(ContentValues values, SQLiteDatabase db) {
        return db.insert("notes", null, values);
    }

    public Cursor getAllNotes(SQLiteDatabase db) {
        return db.rawQuery("select * from notes", null);
    }

    public String retrieveStoredPassword() {
        SQLiteDatabase db = this.getReadableDatabase("password");
        Cursor cursor = db.rawQuery("SELECT password FROM user LIMIT 1", null);

        String storedPassword = "";

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            storedPassword = cursor.getString(cursor.getColumnIndex("password"));
            cursor.close();
        }

        return storedPassword;
    }

    public boolean updatePassword(String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase("password");

        ContentValues values = new ContentValues();
        values.put("password", AES.encrypt(newPassword));
        int rowsAffected = db.update("user", values, null, null);

        return rowsAffected > 0;
    }

    public int deleteNoteById(long noteId, SQLiteDatabase db) {
        return db.delete("notes", "_id=?", new String[]{String.valueOf(noteId)});
    }

    public Cursor getNoteById(long noteId, SQLiteDatabase db) {
        return db.rawQuery("SELECT * FROM notes WHERE _id = ?", new String[]{String.valueOf(noteId)});
    }

    public int updateNoteById(long noteId, ContentValues values, SQLiteDatabase db) {
        return db.update("notes", values, "_id=?", new String[]{String.valueOf(noteId)});
    }


}
