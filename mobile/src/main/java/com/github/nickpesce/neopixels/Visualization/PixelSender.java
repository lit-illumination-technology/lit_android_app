package com.github.nickpesce.neopixels.Visualization;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by nick on 6/8/16.
 */
public class PixelSender {

    private SharedPreferences prefs;

    private DatagramSocket udpSocket;
    private InetAddress host;
    private int port;

    public PixelSender(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Temporary hardcoded port
                    //TODO: get port in response from link start
                    port = 9000;
                    String hostname = prefs.getString("hostname", "host.example.net");
                    if(hostname.contains("://"))
                        hostname = hostname.substring(hostname.indexOf("://")+3);
                    host = InetAddress.getByName(hostname);
                    udpSocket = new DatagramSocket(port);
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    /**
     * Sends pixel color data over UDP (faster).
     * startSendingPixels() must be called prior to first use.
     * doneSendingPixels() must be called when done.
     * Must not be called from main thread
     * @param pixels The byte array of pixel RGB values to send.
     *               FORMAT: [R1, G1, B1, R2, G2, B2, ...]
     */
    public void sendPixels(final byte[] pixels) {
        try {
            DatagramPacket packet = new DatagramPacket(pixels, pixels.length, host, port);
            udpSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Call when no more pixel data will be sent
     * Must not be called form main thread
     */
    public void doneSendingPixels() {
        udpSocket.close();
    }

}
