package com.appme.story.engine.app.folders.file;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class Database extends SQLiteOpenHelper implements Serializable {
    private static final String DATABASE_NAME = "db_manager";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_FILE_TAB = "tbl_file_history";
    private static final String KEY_FILE_PATH = "path";


    public static final String CREATE_TABLE_FILE_HISTORY =
    "create table " + TABLE_FILE_TAB +
    "(" +
    KEY_FILE_PATH + " TEXT PRIMARY KEY" +
    ")";

    private String TAG = Database.class.getName();

    public Database(@NonNull Context context,
                    @NonNull String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public Database(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FILE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILE_TAB);
        onCreate(db);
    }

    public ArrayList<File> getListFile() {
        ArrayList<File> files = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_FILE_TAB;
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    String result = cursor.getString(cursor.getColumnIndex(KEY_FILE_PATH));
                    File file = new File(result);
                    if (file.isFile())
                        files.add(file);
                    else
                        removeFile(result);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception ignored) {

        }
        return files;
    }

    public long addNewFile(File file) {
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_FILE_PATH, file.getPath());
            return sqLiteDatabase.insert(TABLE_FILE_TAB, null, contentValues);
        } catch (Exception e) {
            return -1;
        }
    }

    public boolean removeFile(String path) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_FILE_TAB, KEY_FILE_PATH + "=?", new String[]{path}) > 0;
    }

    public long addNewFile(String file) {
        return addNewFile(new File(file));
    }

}

