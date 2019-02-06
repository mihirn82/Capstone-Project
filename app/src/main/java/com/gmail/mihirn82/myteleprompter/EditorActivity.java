package com.gmail.mihirn82.myteleprompter;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import static com.gmail.mihirn82.myteleprompter.data.ScriptsContract.*;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    public static final String EXTRA_JUSTSTART = "JUSTSTART";
    public static final String EXTRA_TELETEXT = "TELETEXT";

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private EditText mNameEditText;
    private EditText mDataEditText;

    private Uri mCurrentProductUri;

    private String name;
    private String data;

    private Tracker mTracker;

    public static final String myScript = "myScript";
    public static final String SCRIPT_NAME = "scriptName";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mCurrentProductUri = getIntent().getData();

        Log.i ("EditorActivity","uri = " + mCurrentProductUri);

        if (mCurrentProductUri == null) {
            Log.i("EditorActivity.this","uri is null");
        }
        else {
            getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER,null,this);
        }

        mNameEditText = (EditText) findViewById(R.id.script_name);
        mDataEditText = (EditText) findViewById(R.id.script_data);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_teleprompt:
                Intent intent = new Intent(this,TelepromptActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("data", data);
                startActivity(intent);

            case R.id.action_save:
                saveScript();
                finish();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case R.id.action_download:
                DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(this,name,data);
                downloadAsyncTask.execute();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection = {
                ScriptsEntry._ID,
                ScriptsEntry.COLUMN_SCRIPT_NAME,
                ScriptsEntry.COLUMN_SCRIPT_DATA};


        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        Log.i("EditorActivity","cursor = " + cursor);

        if (cursor == null || cursor.getCount() < 1) {

            SharedPreferences.Editor editor = getSharedPreferences(myScript,Context.MODE_PRIVATE).edit();
            editor.putString(SCRIPT_NAME,null);
            editor.apply();

            AppWidgetManager widgetManager = AppWidgetManager.getInstance(getApplicationContext());
            int[] appWidgetIds = widgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), TelepromptWidgetProvider.class));
            Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, getApplicationContext(), TelepromptWidgetProvider.class);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            sendBroadcast(updateIntent);

            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ScriptsEntry.COLUMN_SCRIPT_NAME);
            int dataColumnIndex = cursor.getColumnIndex(ScriptsEntry.COLUMN_SCRIPT_DATA);

            name = cursor.getString(nameColumnIndex);
            data = cursor.getString(dataColumnIndex);

            mNameEditText.setText(name);
            mDataEditText.setText(data);

            SharedPreferences.Editor editor = getSharedPreferences(myScript,Context.MODE_PRIVATE).edit();
            editor.putString(SCRIPT_NAME,mNameEditText.getText().toString().trim());
            editor.apply();

            AppWidgetManager widgetManager = AppWidgetManager.getInstance(getApplicationContext());
            int[] appWidgetIds = widgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), TelepromptWidgetProvider.class));
            Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, getApplicationContext(), TelepromptWidgetProvider.class);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            sendBroadcast(updateIntent);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        mNameEditText.setText("");
        mDataEditText.setText("");
    }


    private void saveScript() {

        String nameString = mNameEditText.getText().toString().trim();
        String dataString = mDataEditText.getText().toString().trim();

        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) || TextUtils.isEmpty(dataString)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ScriptsEntry.COLUMN_SCRIPT_NAME, nameString);
        values.put(ScriptsEntry.COLUMN_SCRIPT_DATA, dataString);

        if (mCurrentProductUri == null) {

            Uri newUri = getContentResolver().insert(ScriptsEntry.CONTENT_URI,values);
            if (newUri == null) {
                Toast.makeText(this,"Failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,"Successful", Toast.LENGTH_SHORT).show();
            }

        } else {

            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this,"Failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,"Successful", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentProductUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_script_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_script_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.i(TAG, "Setting screen name: " + name);
        mTracker.setScreenName(getResources().getString(R.string.type_activity));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Share")
                .build());
    }
}
