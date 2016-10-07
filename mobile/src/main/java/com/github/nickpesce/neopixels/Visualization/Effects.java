package com.github.nickpesce.neopixels.Visualization;

import java.util.Random;

class TestEffect implements VisEffect, BeatListener {
    private BeatDetector beatDetector;
    private FrequencyLevelDetector freqDetector;
    private byte[] pixels;
    private Random random;

    public TestEffect() {
        random = new Random();
        pixels = new byte[60 * 3];
        freqDetector = new FrequencyLevelDetector();
        beatDetector = new BeatDetector(freqDetector);
        beatDetector.addBeatListener(this);
    }

    public static byte twos(int unsigned) {
        if (unsigned < 0) {
            return 0;
            //throw new IllegalArgumentException();
        }
        if (unsigned > 255) {
            return -1;
        }
        if (unsigned < 128) return (byte) (unsigned);
        else return (byte) (unsigned - 256);
    }

    @Override
    public byte[] render(byte[] fft, byte[] waveform) {
        freqDetector.updateFFT(fft);
        return pixels;
    }

    @Override
    public void onBeat(int band, double p) {
        switch (band) {
            case FrequencyLevelDetector.BAND_SUB:
                byte r = twos(random.nextInt(255));
                byte g = twos(random.nextInt(255));
                byte b = twos(random.nextInt(255));
                for (int i = 0; i < 60; i++) {
                    pixels[3 * i] = r;
                    pixels[3 * i + 1] = g;
                    pixels[3 * i + 2] = b;
                }
                break;
        }
    }
}

class BandsEffect implements VisEffect, FrequencyLevelListener {
    FrequencyLevelDetector freqDetector;
    byte[] pixels;
    public BandsEffect() {
        pixels = new byte[60*3];
        freqDetector = new FrequencyLevelDetector();
        freqDetector.addFrequencyLevelListener(this);
    }

    public static byte twos(int unsigned) {
        if (unsigned < 0) {
            return 0;
            //throw new IllegalArgumentException();
        }
        if (unsigned > 255) {
            return -1;
        }
        if (unsigned < 128) return (byte) (unsigned);
        else return (byte) (unsigned - 256);
    }

    @Override
    public void onFrequencyLevelsUpdate(double[] levels, double[] prevLevels, double[] variances) {
        for(int i = 0; i < 15; i++) {
            pixels[3*i] = twos((int)levels[0]*5);
            pixels[3*i + 1] = 0;
            pixels[3*i + 2] = 0;
        }
        for(int i = 15; i < 30; i++) {
            pixels[3*i + 1] = twos((int)(levels[1] * 5));
            pixels[3*i] = 0;
            pixels[3*i + 2] = 0;
        }
        for(int i = 30; i < 45; i++) {
            pixels[3*i+ 2] = twos((int)(levels[2] * 17));
            pixels[3*i + 1] = 0;
            pixels[3*i] = 0;
        }
        for(int i = 45; i < 60; i++) {
            pixels[3*i] = twos((int)(levels[3] * 43));
            pixels[3*i + 1] = 0;
            pixels[3*i + 2] = twos((int)(levels[3] * 43));
        }
    }

    @Override
    public byte[] render(byte[] fft, byte[] waveform) {
        freqDetector.updateFFT(fft);
        return pixels;
    }
}

class BeatTestEffect implements VisEffect, BeatListener {

    private BeatDetector beatDetector;
    private FrequencyLevelDetector freqDetector;
    private byte[] pixels;

    public BeatTestEffect() {
        pixels = new byte[60 * 3];
        freqDetector = new FrequencyLevelDetector();
        beatDetector = new BeatDetector(freqDetector);
        beatDetector.addBeatListener(this);
    }

    public static byte twos(int unsigned) {
        if (unsigned < 0) {
            return 0;
            //throw new IllegalArgumentException();
        }
        if (unsigned > 255) {
            return -1;
        }
        if (unsigned < 128) return (byte) (unsigned);
        else return (byte) (unsigned - 256);
    }

    @Override
    public byte[] render(byte[] fft, byte[] waveform) {
        pixels = new byte[60 * 3];
        freqDetector.updateFFT(fft);
        return pixels;
    }

    @Override
    public void onBeat(int band, double p) {
        switch (band) {
            case FrequencyLevelDetector.BAND_SUB:
                for (int i = 0; i < 15; i++) {
                    pixels[3 * i] = twos((int) (128 * p));
                    pixels[3 * i + 1] = 0;
                    pixels[3 * i + 2] = 0;
                }
                break;
            case FrequencyLevelDetector.BAND_LOW:
                //                byte r = (byte)(random.nextInt(255)-128);
                //                byte g = (byte)(random.nextInt(255)-128);
                //                byte b = (byte)(random.nextInt(255)-128);
                //                for(int i = 0; i < 60; i++) {
                //                    pixels[3 * i] = r;
                //                    pixels[3 * i + 1] = g;
                //                    pixels[3 * i + 2] = b;
                //                }
                for (int i = 15; i < 30; i++) {
                    pixels[3 * i] = twos((int) (128 * p));
                    pixels[3 * i + 1] = 0;
                    pixels[3 * i + 2] = twos((int) (128 * p));
                }
                break;
            case FrequencyLevelDetector.BAND_MID:
                for (int i = 30; i < 45; i++) {
                    pixels[3 * i] = 0;
                    pixels[3 * i + 1] = 0;
                    pixels[3 * i + 2] = twos((int) (128 * p));
                }
                break;
            case FrequencyLevelDetector.BAND_HIGH:
                for (int i = 45; i < 60; i++) {
                    pixels[3 * i] = 0;
                    pixels[3 * i + 1] = twos((int) (128 * p));
                    pixels[3 * i + 2] = 0;
                }
                break;
        }
    }
}
