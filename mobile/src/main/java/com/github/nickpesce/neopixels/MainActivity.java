package com.github.nickpesce.neopixels;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener, AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    private String command;
    private Button bSend;
    private EditText tfCommand;
    private Spinner spCommand;
    private SeekBar sbRed, sbGreen, sbBlue, sbSpeed;
    private Switch swColor, swSpeed;
    private SurfaceView svColor;
    private CommandSender sender;
    private HashMap<String, Byte>  commands;
    private static final byte HAS_COLOR = 0b1, HAS_SPEED = 0b10, IS_EACH = 0b100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sender = new CommandSender(this, this);

        bSend = (Button)findViewById(R.id.bSend);

        tfCommand = (EditText)findViewById(R.id.tfCommand);
        tfCommand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                command = tfCommand.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        tfCommand.setOnEditorActionListener(this);

        spCommand = (Spinner)findViewById(R.id.spCommand);
        spCommand.setOnItemSelectedListener(this);
        sender.sendCommand("commands");

        sbRed = (SeekBar) findViewById(R.id.sbRed);
        sbRed.setOnSeekBarChangeListener(this);

        sbGreen = (SeekBar) findViewById(R.id.sbGreen);
        sbGreen.setOnSeekBarChangeListener(this);

        sbBlue = (SeekBar) findViewById(R.id.sbBlue);
        sbBlue.setOnSeekBarChangeListener(this);

        sbSpeed = (SeekBar) findViewById(R.id.sbSpeed);
        sbSpeed.setOnSeekBarChangeListener(this);

        swColor = (Switch)findViewById(R.id.swColor);
        swColor.setOnCheckedChangeListener(this);

        swSpeed = (Switch) findViewById(R.id.swSpeed);
        swSpeed.setOnCheckedChangeListener(this);

        svColor = (SurfaceView) findViewById(R.id.svColor);

        bSend.setOnClickListener(this);
        
    }

    public void setCommands(String raw)
    {
        String[] helpStrings = raw.split("~ ");
        for(int i = 0; i < helpStrings.length; i++)
            helpStrings[i] = helpStrings[i].trim();
        commands = new HashMap<>();
        for(int i = 1; i < helpStrings.length; i++) {
            byte options = (byte)((helpStrings[i].contains("-c") ? HAS_COLOR:0)
                    | (helpStrings[i].contains("-s") ? HAS_SPEED:0));
            if(helpStrings[i].contains("each"))
                continue;
                //options = IS_EACH;
            commands.put(helpStrings[i].split(" ")[0], options);
        }
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, new ArrayList(commands.keySet()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCommand.setAdapter(adapter);
        spCommand.setVisibility(Spinner.VISIBLE);
        spCommand.setSelection(Arrays.asList(commands).indexOf("on"));
        swColor.setVisibility(Switch.VISIBLE);
        swSpeed.setVisibility(Switch.VISIBLE);
    }

    private void updateCommand()
    {
        if(spCommand.getSelectedItem() == null)
        {
            command = "";
            return;
        }
        command = spCommand.getSelectedItem().toString();
        if(swColor.isChecked())
            command += " -c (" + sbRed.getProgress() + "," + sbGreen.getProgress() + "," + sbBlue.getProgress() +")";
        if(swSpeed.isChecked())
            command += " -s " + sbSpeed.getProgress();
        tfCommand.setText(command);
        //sender.sendCommand(command);
    }

    private void updateColor()
    {
        svColor.setBackgroundColor(Color.argb(255, sbRed.getProgress(), sbGreen.getProgress(), sbBlue.getProgress()));
    }

    @Override
    public void onClick(View v) {
        if(v.equals(bSend)) {
            sender.sendCommand(command);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.equals(tfCommand) && actionId == EditorInfo.IME_ACTION_SEND) {
            sender.sendCommand(command);
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
            startActivity(new Intent(this, SettingsActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        updateCommand();
        byte params = commands.get(spCommand.getSelectedItem().toString());
        swColor.setVisibility((params & HAS_COLOR) != 0 ? Switch.VISIBLE : Switch.GONE);
        swSpeed.setVisibility((params & HAS_SPEED) != 0 ? Switch.VISIBLE : Switch.GONE);
        swColor.setChecked(false);
        swSpeed.setChecked(false);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        updateCommand();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateCommand();
        updateColor();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(swColor))
        {
            int visible = isChecked ? SeekBar.VISIBLE : SeekBar.GONE;
            sbRed.setVisibility(visible);
            sbGreen.setVisibility(visible);
            sbBlue.setVisibility(visible);
            svColor.setVisibility(visible);
        }else
            sbSpeed.setVisibility(isChecked ? SeekBar.VISIBLE : SeekBar.GONE);
        updateCommand();
    }
}
