package com.github.nickpesce.neopixels;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.github.nickpesce.neopixels.Visualization.VisualizationActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, CommandEditor.CommandEditorListener {

    public static final int NUM_LIGHTS = 60;

    private Button bSend;
    private CommandSender sender;
    private CommandEditor commandEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        commandEditor = (CommandEditor) getSupportFragmentManager().findFragmentById(R.id.fCommandEditor);
        sender = new CommandSender(this);

        bSend = (Button)findViewById(R.id.bSend);

        bSend.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.equals(bSend)) {
            sender.startEffect(commandEditor.getEffect(), commandEditor.getArgs());
        }
    }

    @Override
    public void onEditorReady() {
        bSend.setVisibility(Button.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } if(id == R.id.action_visualization) {
            startActivity(new Intent(this, VisualizationActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
