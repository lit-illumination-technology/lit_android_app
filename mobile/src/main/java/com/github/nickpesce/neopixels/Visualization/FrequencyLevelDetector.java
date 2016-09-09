package com.github.nickpesce.neopixels.Visualization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by nick on 8/28/16.
 */
public class FrequencyLevelDetector {
    public static final int BAND_SUB = 0,
            BAND_LOW = 1,
            BAND_MID = 2,
            BAND_HIGH = 3;

    public static final int[] bandWidths = {1, 62, 96, 352};
    private ArrayList<FrequencyLevelListener> listeners;

    private Queue<Double>[] energyHistories;
    private double[] energySums;

    private Queue<Double>[] varianceHistories;
    private double[] varianceSums;


    public FrequencyLevelDetector() {
        energyHistories = new LinkedList[4];
        for(int i = 0; i < energyHistories.length; i++) {
            energyHistories[i] = new LinkedList<>();
        }
        energySums = new double[4];

        varianceHistories = new LinkedList[4];
        for(int i = 0; i < varianceHistories.length; i++) {
            varianceHistories[i] = new LinkedList<>();
        }
        varianceSums = new double[4];

        listeners = new ArrayList<FrequencyLevelListener>();
    }

    public void updateFFT(byte[] fft) {
        double[] avgs = new double[bandWidths.length];
        double[] prevs = new double[bandWidths.length];
        double[] Cs = new double[bandWidths.length];


        for(int band = 0; band < 4; band++) {
            int bandWidth = bandWidths[band];
            int bandStart = 0;
            for (int i = 0; i < band; i++) {
                bandStart += bandWidths[i];
            }
            //Find the energy levels
            double[] energy = new double[bandWidth];
            for (int i = 0; i < bandWidth; i++) {
                int ffti = i + bandStart;
                energy[i] = Math.sqrt(fft[2 * ffti] * fft[2 * ffti] + fft[2 * ffti + 1] * fft[2 * ffti + 1]);
            }

            //Find the Average energy of these 1024 samples
            double tmp = 0;
            double avg;
            for (int i = 0; i < energy.length; i++) {
                tmp += energy[i];
            }
            avg = tmp / energy.length;


            //add the energy levels to energyHistory
            energyHistories[band].add(avg);
            energySums[band] += avg;
            if (energyHistories[band].size() > Visualization.SAMPLES_PER_SEC)
                energySums[band] -= energyHistories[band].poll();

            //Find the variance of the last second
            double variance = Math.pow((avg - (energySums[band] / Visualization.SAMPLES_PER_SEC)), 2);
            varianceHistories[band].add(variance);
            varianceSums[band] += variance;
            if (varianceHistories[band].size() > Visualization.SAMPLES_PER_SEC)
                varianceSums[band] -= varianceHistories[band].poll();

            //Find a the constant value that determines a spike
            double C = -.0025714 * varianceSums[band] / Visualization.SAMPLES_PER_SEC + 1.5142857;
            avgs[band] = avg;
            prevs[band] = energySums[band] / Visualization.SAMPLES_PER_SEC;
            Cs[band] = C;
        }
        for(FrequencyLevelListener l : listeners) {
            l.onFrequencyLevelsUpdate(avgs, prevs, Cs);
        }
    }

    public void addFrequencyLevelListener(FrequencyLevelListener listener) {
        listeners.add(listener);
    }
}
