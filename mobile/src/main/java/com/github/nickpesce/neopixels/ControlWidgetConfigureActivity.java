package com.github.nickpesce.neopixels;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The configuration screen for the {@link ControlWidget ControlWidget} AppWidget.
 */
public class ControlWidgetConfigureActivity extends AppCompatActivity implements CommandEditor.CommandEditorListener{

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private EditText mAppWidgetName;
    private Button bAddWidget;
    private CommandEditor commandEditor;

    private static final String PREFS_NAME = "com.github.nickpesce.neopixels.ControlWidget";
    private static final String PREF_PREFIX_KEY = "controlwidget_";
    private static final String PREF_SUFFIX_NAME = "_name";
    private static final String PREF_SUFFIX_COMMAND = "_command";


    public ControlWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.control_widget_configure);
        mAppWidgetName = (EditText) findViewById(R.id.appwidget_name);
        bAddWidget = (Button) findViewById(R.id.bAddWidget);
        commandEditor = (CommandEditor) getSupportFragmentManager().findFragmentById(R.id.fCommandEditor);

        bAddWidget.setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = ControlWidgetConfigureActivity.this;

            // When the button is clicked, store the settings locally
            saveCommandPref(context, mAppWidgetId, commandEditor.getJSON());

            String widgetName = mAppWidgetName.getText().toString();
            saveTitlePref(context, mAppWidgetId, widgetName);


            HashMap<String, Object> hmArgs = commandEditor.getArgs();
            HashSet<String> args = new HashSet<>();
            for(Map.Entry<String, Object> e : hmArgs.entrySet()) {
                args.add(e.getKey() + ":" + e.getValue().toString());
            }

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ControlWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + PREF_SUFFIX_NAME, text);
        prefs.commit();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveCommandPref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + PREF_SUFFIX_COMMAND, text);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId + PREF_SUFFIX_NAME, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return "Error";
        }
    }

    static String loadCommandPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String commandValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId + PREF_SUFFIX_COMMAND, "Error");
        return commandValue;
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.commit();
    }

    @Override
    public void onEditorReady() {

    }
}

