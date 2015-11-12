package com.github.nickpesce.neopixels;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private Button bSend;
    private EditText tfCommand;
    private CommandSender sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bSend = (Button)findViewById(R.id.bSend);
        tfCommand = (EditText)findViewById(R.id.tfCommand);
        tfCommand.setOnEditorActionListener(this);
        bSend.setOnClickListener(this);
        sender = new CommandSender(this, "nickspi.student.umd.edu", 42297);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(bSend)) {
            sender.sendCommand(tfCommand.getText().toString());
            tfCommand.setText("");
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.equals(tfCommand) && actionId == EditorInfo.IME_ACTION_SEND) {
            sender.sendCommand(tfCommand.getText().toString());
            tfCommand.setText("");
            return true;
        }
        return false;
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
