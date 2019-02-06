package com.gmail.mihirn82.myteleprompter;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

public class TelepromptWidgetProvider extends AppWidgetProvider {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        SharedPreferences preferences = context.getSharedPreferences(EditorActivity.myScript,Context.MODE_PRIVATE);
        String scriptName = preferences.getString(EditorActivity.SCRIPT_NAME,"0");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.teleprompt_widget_provider);
        Intent intent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);


        if (scriptName == "0") {
            views.setViewVisibility(R.id.script_name_tv, View.INVISIBLE);
            views.setOnClickPendingIntent(R.id.empty_view_image,pendingIntent);
//
        } else {
            views.setViewVisibility(R.id.empty_view,View.INVISIBLE);
            views.setTextViewText(R.id.script_name_tv,scriptName);
            views.setOnClickPendingIntent(R.id.script_name_tv,pendingIntent);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
//        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
//        super.onDisabled(context);
    }

}
