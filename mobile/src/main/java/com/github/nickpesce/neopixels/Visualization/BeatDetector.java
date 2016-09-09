package com.github.nickpesce.neopixels.Visualization;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Created by nick on 6/20/16.
 */
public class BeatDetector implements FrequencyLevelListener{
    private ArrayList<BeatListener> listeners;

    public BeatDetector(FrequencyLevelDetector detector){
        listeners = new ArrayList<BeatListener>();
        detector.addFrequencyLevelListener(this);
    }

    public void addBeatListener(BeatListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onFrequencyLevelsUpdate(double[] levels, double[] prevLevels, double[] variances) {
        for(int i = 0; i < levels.length; i++) {
            if(levels[i] > prevLevels[i] * variances[i]) {
                for(BeatListener bl : listeners)
                    bl.onBeat(i, levels[i]);
            }
        }
    }
}


