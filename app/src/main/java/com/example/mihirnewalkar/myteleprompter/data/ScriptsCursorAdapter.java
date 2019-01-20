package com.example.mihirnewalkar.myteleprompter.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


import com.example.mihirnewalkar.myteleprompter.R;

import static com.example.mihirnewalkar.myteleprompter.data.ScriptsContract.*;

public class ScriptsCursorAdapter extends CursorAdapter {


    public ScriptsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView scriptNameTextView = (TextView) view.findViewById(R.id.script_name_tv);

        int nameColumnIndex = cursor.getColumnIndex(ScriptsEntry.COLUMN_SCRIPT_NAME);
        int dataColumnIndex = cursor.getColumnIndex(ScriptsEntry.COLUMN_SCRIPT_DATA);

        String scriptName = cursor.getString(cursor.getColumnIndex(ScriptsEntry.COLUMN_SCRIPT_NAME));
        String scriptData = cursor.getString(cursor.getColumnIndex(ScriptsEntry.COLUMN_SCRIPT_DATA));

        scriptNameTextView.setText(scriptName);
    }
}
