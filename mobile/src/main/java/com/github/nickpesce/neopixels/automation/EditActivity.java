package com.github.nickpesce.neopixels.automation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nickpesce.neopixels.CommandEditor;
import com.github.nickpesce.neopixels.R;

/**
 * Created by nick on 4/3/16.
 */
public final class EditActivity extends AppCompatActivity implements View.OnClickListener, com.github.nickpesce.neopixels.CommandEditor.CommandEditorListener {

    Button bOk;
    CommandEditor commandEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_automation);
        bOk = (Button)findViewById(R.id.bEditOk);
        bOk.setOnClickListener(this);
        commandEditor = (CommandEditor) getSupportFragmentManager().findFragmentById(R.id.fCommandEditor);
    }

    @Override
    public void finish() {
        final Intent resultIntent = new Intent();
        final Bundle resultBundle = new Bundle();
        String command = commandEditor.getJSON();
        resultBundle.putString(FireReceiver.BUNDLE_COMMAND, command);
        resultIntent.putExtra(FireReceiver.BUNDLE_BLURB, command);
        if ( TaskerPlugin.Setting.hostSupportsOnFireVariableReplacement( this ) )
            TaskerPlugin.Setting.setVariableReplaceKeys( resultBundle, new String [] { FireReceiver.BUNDLE_COMMAND } );
        resultIntent.putExtra("com.twofortyfouram.locale.intent.extra.BUNDLE", resultBundle);
        setResult(RESULT_OK, resultIntent);
        super.finish();
    }

    @Override
    public void onClick(View v) {
        if(v.equals(bOk)) {
            finish();
        }
    }

    @Override
    public void onEditorReady() {
        bOk.setVisibility(Button.VISIBLE);
    }
}
