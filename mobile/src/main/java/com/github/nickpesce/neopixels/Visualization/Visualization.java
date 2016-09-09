package com.github.nickpesce.neopixels.Visualization;

import android.graphics.Color;
import android.media.audiofx.Visualizer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Visualization implements Visualizer.OnDataCaptureListener {

    public static int SAMPLES_PER_SEC = 5;

    private Visualizer visualizer;
    private VisualizationActivity display;
    private PixelSender sender;
    private byte[] pixels = {};
    private byte[] fft;
    private byte[] waveform;
    private boolean running;
    private int sleepTime;
    private VisEffect effect;
    private Thread thread;

    public Visualization(VisualizationActivity display) {
        this.display = display;
        this.sender = new PixelSender(display);
    }

    public void start() {
        effect = new TestEffect();
        if(visualizer == null)
            visualizer = new Visualizer(0);
        visualizer.setDataCaptureListener(this, Visualizer.getMaxCaptureRate(), true, true);
        sleepTime = (1/(Visualizer.getMaxCaptureRate()*1000))*1000;
        visualizer.setCaptureSize(1024);
        visualizer.setEnabled(true);
        running = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(running) {
                    update();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
        this.waveform = waveform;
        //System.out.print("WAVEFORM: ");printByteArray(waveform);
    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
        this.fft = fft;
    }

    private void update() {
        if(fft != null && waveform != null)
        {
            pixels = effect.render(fft, waveform);
            synchronized (pixels) {
                sender.sendPixels(pixels);
                display.update(pixels);
            }
        }
    }


    public void destroy() {
        if(visualizer == null) return;
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        visualizer.setEnabled(false);
        visualizer.release();
        visualizer = null;
        sender.doneSendingPixels();

    }
    public boolean isRunning() {
        return running;
    }

    private void printByteArray(byte[] a) {
        for(byte b : a)
            System.out.print(b + ",");
        System.out.println();
    }

}

interface VisEffect {
    byte[] render(byte[] fft, byte[] waveform);
}