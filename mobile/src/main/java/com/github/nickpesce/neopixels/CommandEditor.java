package com.github.nickpesce.neopixels;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class CommandEditor extends Fragment implements AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener{

    private static final byte HAS_COLOR = 0b1, HAS_SPEED = 0b10, IS_EACH = 0b100;

    private CommandEditorListener listener;

    private CommandSender sender;
    private TextView tvConnecting;
    private String effect;
    private Spinner spEffect;
    private SeekBar sbRed, sbGreen, sbBlue, sbSpeed;
    private Switch swColor, swSpeed;
    private LinearLayout llRanges;
    private SurfaceView svColor;
    private List<String> ranges;
    private HashMap<String, Byte> effects;
    private HashMap<String, Object> args;

    public CommandEditor() {
        // Required empty public constructor
    }


    public HashMap<String, Object> getArgs() {
        return args;
    }

    public String getEffect() {
        return effect;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sender = new CommandSender(getContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (CommandEditorListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CommandEditor.CommandEditorListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_command_editor, container, false);
        args = new HashMap();
        ranges = new ArrayList<>();

        tvConnecting = (TextView) v.findViewById(R.id.tvConnecting);

        llRanges = (LinearLayout) v.findViewById(R.id.llRanges);
        sender.getRanges(this);

        spEffect = (Spinner) v.findViewById(R.id.spCommand);
        spEffect.setOnItemSelectedListener(this);
        sender.getEffects(this);

        sbRed = (SeekBar) v.findViewById(R.id.sbRed);
        sbRed.setOnSeekBarChangeListener(this);

        sbGreen = (SeekBar) v.findViewById(R.id.sbGreen);
        sbGreen.setOnSeekBarChangeListener(this);

        sbBlue = (SeekBar) v.findViewById(R.id.sbBlue);
        sbBlue.setOnSeekBarChangeListener(this);

        sbSpeed = (SeekBar) v.findViewById(R.id.sbSpeed);
        sbSpeed.setOnSeekBarChangeListener(this);

        swColor = (Switch) v.findViewById(R.id.swColor);
        swColor.setOnCheckedChangeListener(this);

        swSpeed = (Switch) v.findViewById(R.id.swSpeed);
        swSpeed.setOnCheckedChangeListener(this);

        svColor = (SurfaceView) v.findViewById(R.id.svColor);
        return v;
    }

    private void updateCommand()
    {
        if(spEffect.getSelectedItem() == null)
        {
            effect = "";
            return;
        }
        if(ranges.isEmpty()) {
            effect = "off";
            return;
        }
        effect = spEffect.getSelectedItem().toString();
        args.clear();
        if(swColor.isChecked())
            args.put("color", new int[]{sbRed.getProgress(), sbGreen.getProgress(), sbBlue.getProgress()});
        if(swSpeed.isChecked()) {
            args.put("speed", sbSpeed.getProgress());
        }
        args.put("ranges", ranges);
    }

    private void updateColor()
    {
        svColor.setBackgroundColor(Color.argb(255, sbRed.getProgress(), sbGreen.getProgress(), sbBlue.getProgress()));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        updateCommand();
        byte params = effects.get(spEffect.getSelectedItem().toString());
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
        }else if(buttonView.equals(swSpeed)) {
            sbSpeed.setVisibility(isChecked ? SeekBar.VISIBLE : SeekBar.GONE);
        } else {
            if(isChecked)
                ranges.add(buttonView.getText().toString());
            else
                ranges.remove(buttonView.getText().toString());
        }
        updateCommand();
    }

    public void callBackRanges(List<String> ranges) {
        boolean defaultRange = true;
        for(String s : ranges) {
            Switch sw = new Switch(this.getContext());
            sw.setText(s);
            sw.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            sw.setOnCheckedChangeListener(this);
            sw.setChecked(defaultRange);
            defaultRange = false;
            llRanges.addView(sw);
        }
        updateLayout();
    }
    /**
     * Callback from CommandSender.getEffects(CommandEditor).
     * updates the ui with the effects received and notifies the listener if everything is loaded(onEditorReady())
     * @param effects (Name, Modifiers) of effects received
     */
    public void callBackEffects(HashMap<String, Byte> effects) {
        this.effects = effects;
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_spinner_item, new ArrayList(effects.keySet()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEffect.setAdapter(adapter);
        updateLayout();
    }

    public void updateLayout() {
        if(ranges == null || effects == null) return;
        spEffect.setVisibility(Spinner.VISIBLE);
        swColor.setVisibility(Switch.VISIBLE);
        swSpeed.setVisibility(Switch.VISIBLE);
        llRanges.setVisibility(LinearLayout.VISIBLE);
        tvConnecting.setVisibility(TextView.INVISIBLE);
        listener.onEditorReady();
    }

    public void callBackError(String errorMessage) {
        tvConnecting.setText(errorMessage);
    }

    public String getJSON() {
        JSONObject command = new JSONObject();
        try {
            command.put("effect", effect);
            if (args != null && !args.isEmpty())
                command.put("args", new JSONObject(args));
            return command.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public interface CommandEditorListener {
        void onEditorReady();
    }
}
