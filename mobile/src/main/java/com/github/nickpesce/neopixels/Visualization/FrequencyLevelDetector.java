package com.github.nickpesce.neopixels.Visualization;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by nick on 8/28/16.
 */
public class FrequencyLevelDetector {
    public static final int BAND_SUB = 0,//SUB BASS AND BASS (bottom .1%) | Bass beat
            BAND_LOW = 1,//BASS, UPPER BASS AND LOW-MID (next 3.8%) | Lower vocal and lower drums
            BAND_MID = 2,//UPPER-MID AND MID(next 21%) | Higher vocals and instruments
            BAND_HIGH = 3;//HIGH FREQ AND ULTRA HIGH FREQ (remaining 75%) | Cymbals and high sounds

    public static final int[] bandWidths = {1, 20, 107, 384};
    public static final int LONG_HISTORY_SIZE = 25;//50
    public static final int SHORT_HISTORY_SIZE = 5;//15

    private ArrayList<FrequencyLevelListener> listeners;

    private Queue<Double>[] longEnergyHistories;
    private double[] longEnergySums;

    private Queue<Double>[] shortEnergyHistories;
    private double[] shortEnergySums;

    private Queue<Double>[] varianceHistories;
    private double[] varianceSums;


    public FrequencyLevelDetector() {
        System.out.println();
        longEnergyHistories = new LinkedList[4];
        for(int i = 0; i < longEnergyHistories.length; i++) {
            longEnergyHistories[i] = new LinkedList<>();
        }
        longEnergySums = new double[4];

        shortEnergyHistories = new LinkedList[4];
        for(int i = 0; i < shortEnergyHistories.length; i++) {
            shortEnergyHistories[i] = new LinkedList<>();
        }
        shortEnergySums = new double[4];

        varianceHistories = new LinkedList[4];
        for(int i = 0; i < varianceHistories.length; i++) {
            varianceHistories[i] = new LinkedList<>();
        }
        varianceSums = new double[4];

        listeners = new ArrayList<FrequencyLevelListener>();
    }

    public void updateFFT(byte[] fft) {
        double[] currents = new double[bandWidths.length];
        double[] longs = new double[bandWidths.length];
        double[] shorts = new double[bandWidths.length];

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


            //add the energy levels to long term energyHistory
            longEnergyHistories[band].add(avg);
            longEnergySums[band] += avg;
            if (longEnergyHistories[band].size() > LONG_HISTORY_SIZE)
                longEnergySums[band] -= longEnergyHistories[band].poll();

            //add the energy levels to short term energyHistory
            shortEnergyHistories[band].add(avg);
            shortEnergySums[band] += avg;
            if (shortEnergyHistories[band].size() > SHORT_HISTORY_SIZE)
                shortEnergySums[band] -= shortEnergyHistories[band].poll();


            //Find the variance of the long term
            double variance = Math.pow((avg - (longEnergySums[band] / LONG_HISTORY_SIZE)), 2);
            varianceHistories[band].add(variance);
            varianceSums[band] += variance;
            if (varianceHistories[band].size() > LONG_HISTORY_SIZE)
                varianceSums[band] -= varianceHistories[band].poll();

            //Find a the constant value that determines a spike
            double C = -.0025714 * (varianceSums[band] / LONG_HISTORY_SIZE) + 1.5142857;
            currents[band] = avg;
            longs[band] = longEnergySums[band] / LONG_HISTORY_SIZE;
            shorts[band] = shortEnergySums[band] / SHORT_HISTORY_SIZE;

            Cs[band] = C;
        }
        for(FrequencyLevelListener l : listeners) {
            if(l!=null)
                l.onFrequencyLevelsUpdate(shorts, longs, Cs);
        }
    }

    public void addFrequencyLevelListener(FrequencyLevelListener listener) {
        listeners.add(listener);
    }
}
