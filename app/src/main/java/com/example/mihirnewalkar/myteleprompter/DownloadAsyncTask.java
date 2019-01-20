package com.example.mihirnewalkar.myteleprompter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;


public class DownloadAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private Context mContext;
    String name;
    String data;

    public DownloadAsyncTask(Context context,String name, String data) {
        mContext = context;
        this.name = name;
        this.data = data;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        String filename =  name + ".txt";
        String content = data;
        FileOutputStream outputStream;
        File file;

        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
            outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes());
            outputStream.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        String status;
        if (aBoolean) {
            status = mContext.getString(R.string.download_success);
        } else {
            status = mContext.getString(R.string.download_fail);
        }
        Toast.makeText(mContext, status, Toast.LENGTH_SHORT).show();
    }
}
