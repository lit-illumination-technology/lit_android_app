package com.github.nickpesce.neopixels;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.HashMap;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ControlWidgetConfigureActivity ControlWidgetConfigureActivity}
 */
public class ControlWidget extends AppWidgetProvider {

    private static final String ACTION = "DO ACTION";
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent.getAction().equals(ACTION))
        {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            String command = ControlWidgetConfigureActivity.loadCommandPref(context, appWidgetId);
            CommandSender sender = new CommandSender(context);
            sender.startEffect(command);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            ControlWidgetConfigureActivity.deleteTitlePref(context, appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = ControlWidgetConfigureActivity.loadTitlePref(context, appWidgetId);

        //Create an intent to send a command
        Intent intent = new Intent(context, ControlWidget.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setAction(ACTION);

        //Get the intent ready.
        PendingIntent pending = PendingIntent.getBroadcast(context, appWidgetId, intent, 0);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.control_widget);
        views.setTextViewText(R.id.controlwidget_text, widgetText);

        //Add a listener on the widget view to send the intent when pressed.
        views.setOnClickPendingIntent(R.id.controlwidget_text, pending);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

