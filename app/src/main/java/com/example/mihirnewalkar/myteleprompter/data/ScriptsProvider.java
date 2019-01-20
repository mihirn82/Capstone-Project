package com.example.mihirnewalkar.myteleprompter.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.example.mihirnewalkar.myteleprompter.data.ScriptsContract.*;

public class ScriptsProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = ScriptsProvider.class.getSimpleName();

    private ScriptsDbHelper mDbHelper;

    /** URI matcher code for the content URI for the scripts table */
    private static final int SCRIPTS = 100;

    /** URI matcher code for the content URI for a single product in the scripts table */
    private static final int SCRIPTS_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(ScriptsContract.CONTENT_AUTHORITY,ScriptsContract.PATH_SCRIPTS,SCRIPTS);
        sUriMatcher.addURI(ScriptsContract.CONTENT_AUTHORITY,ScriptsContract.PATH_SCRIPTS + "/#",SCRIPTS_ID);
    }



    @Override
    public boolean onCreate() {

        mDbHelper = new ScriptsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case SCRIPTS:
                cursor = database.query(ScriptsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case SCRIPTS_ID:

                selection = ScriptsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(ScriptsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SCRIPTS:
                return ScriptsEntry.CONTENT_LIST_TYPE;
            case SCRIPTS_ID:
                return ScriptsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {

        if (values.containsKey(ScriptsEntry.COLUMN_SCRIPT_NAME)) {
            String name = values.getAsString(ScriptsEntry.COLUMN_SCRIPT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Script requires name");
            }
        }

        if (values.containsKey(ScriptsEntry.COLUMN_SCRIPT_DATA)) {
            String data = values.getAsString(ScriptsEntry.COLUMN_SCRIPT_DATA);
            if (data == null) {
                throw new IllegalArgumentException("Script requires data");
            }
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(ScriptsEntry.TABLE_NAME, null, values);


        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int rowsDeleted;

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SCRIPTS:

                rowsDeleted = database.delete(ScriptsEntry.TABLE_NAME, selection, selectionArgs);

                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsDeleted;

            case SCRIPTS_ID:

                selection = ScriptsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = database.delete(ScriptsEntry.TABLE_NAME, selection, selectionArgs);

                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsDeleted;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SCRIPTS:
                return updateScript(uri, contentValues, selection, selectionArgs);

            case SCRIPTS_ID:

                selection = ScriptsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateScript(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateScript(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ScriptsEntry.COLUMN_SCRIPT_NAME)) {
            String name = values.getAsString(ScriptsEntry.COLUMN_SCRIPT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Script requires name");
            }
        }

        if (values.containsKey(ScriptsEntry.COLUMN_SCRIPT_DATA)) {
            String data = values.getAsString(ScriptsEntry.COLUMN_SCRIPT_DATA);
            if (data == null) {
                throw new IllegalArgumentException("Script requires data");
            }
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ScriptsEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

}
