package com.gmail.mihirn82.myteleprompter;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gmail.mihirn82.myteleprompter.data.ScriptsCursorAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import static com.gmail.mihirn82.myteleprompter.data.ScriptsContract.*;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int SCRIPT_LOADER = 0;
    private InterstitialAd mInterstitialAd;
    ScriptsCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find a reference to the {@link ListView} in the layout
        ListView scriptsListView = (ListView) findViewById(R.id.list_view_scripts);

        View emptyView = findViewById(R.id.empty_view);
        scriptsListView.setEmptyView(emptyView);

        mCursorAdapter = new ScriptsCursorAdapter(this,null);
        scriptsListView.setAdapter(mCursorAdapter);

        scriptsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this,EditorActivity.class);

                Uri currentScriptUri = ContentUris.withAppendedId (ScriptsEntry.CONTENT_URI,id);
                Log.i("MainActivity","uri = " + currentScriptUri);
                intent.setData(currentScriptUri);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(SCRIPT_LOADER,null,this);

//        MobileAds.initialize(this,"ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        requestNewInterstitial();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
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

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this, ScriptsEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


//    private void insertScript() {
//
//        ContentValues values = new ContentValues();
//        values.put(ScriptsEntry.COLUMN_SCRIPT_NAME, "Lorem");
//        values.put(ScriptsEntry.COLUMN_SCRIPT_DATA, "Lorem ipsum dolor sit amet, consectetur adipiscing elit");
//
//        Uri newUri = getContentResolver().insert(ScriptsEntry.CONTENT_URI, values);
//    }
//
//    private void deleteAllScripts() {
//        int rowsDeleted = getContentResolver().delete(ScriptsEntry.CONTENT_URI, null, null);
//        Log.v("CatalogActivity", rowsDeleted + " scripts deleted from scripts database");
//    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("MY-DEVICE")
                .build();
        mInterstitialAd.loadAd(adRequest);
    }
}
