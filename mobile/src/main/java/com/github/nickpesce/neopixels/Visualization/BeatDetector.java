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
    private Queue<Long> previousDs[];
    private long lastTime[];
    private int historySize = 20;
    private int averageD[];
    private long averageDSum[];

    public BeatDetector(FrequencyLevelDetector detector){
        listeners = new ArrayList<BeatListener>();
        detector.addFrequencyLevelListener(this);
        previousDs = new LinkedList[4];
        lastTime = new long[4];
        averageD = new int[4];
        averageDSum = new long[4];
        for(int i = 0; i < 4; i++){
            lastTime[i] = System.currentTimeMillis();
            averageD[i] = 500;
            previousDs[i] = new LinkedList<Long>();
        }
    }

    public void addBeatListener(BeatListener listener) {
        listeners.add(listener);
    }

    boolean[] inBeat = new boolean[4];
    double[] thresholds = new double[4];

    @Override
    public void onFrequencyLevelsUpdate(double[] levels, double[] prevLevels, double[] variances) {
        for(int i = 0; i < levels.length; i++) {
            long d = System.currentTimeMillis() - lastTime[i];
            int distance = (int)Math.max(averageD[i] - d, 0);
            thresholds[i] = prevLevels[i] * variances[i] + ((distance/averageD[i]));
            if(levels[i] > thresholds[i]) {
                if(inBeat[i])
                    continue;
                inBeat[i] = true;
                    for (BeatListener bl : listeners) {
                        bl.onBeat(i, levels[i]);
                    }

                if(previousDs[i].size() >= historySize)
                    averageDSum[i] -= previousDs[i].remove();
                previousDs[i].add(d);
                averageDSum[i] += d;
                averageD[i] = (int)(averageDSum[i] / previousDs[i].size());
                lastTime[i] = System.currentTimeMillis();
            }else
                inBeat[i] = false;
        }
        VisualizationActivity.lightView.updateGraph(levels, prevLevels, thresholds);
    }
}


