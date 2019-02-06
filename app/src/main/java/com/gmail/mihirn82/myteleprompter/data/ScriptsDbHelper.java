package com.gmail.mihirn82.myteleprompter.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.gmail.mihirn82.myteleprompter.data.ScriptsContract.*;

public class ScriptsDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "scripts.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String TEXT_NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ScriptsEntry.TABLE_NAME + " (" +
                    ScriptsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ScriptsEntry.COLUMN_SCRIPT_NAME + TEXT_TYPE + TEXT_NOT_NULL + COMMA_SEP +
                    ScriptsEntry.COLUMN_SCRIPT_DATA + TEXT_TYPE + " );";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ScriptsEntry.TABLE_NAME;

    public ScriptsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}