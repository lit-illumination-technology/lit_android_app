package com.github.nickpesce.neopixels;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Nick on 11/11/2015.
 */
public class CommandSender{

    private String hostName;
    private int port;
    private Context context;
    public CommandSender(final Context context, final String hostName, int port)
    {
        this.context = context;
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * Sends a command to the host .
     * @param command The string command  to send
     * @throws IOException If the packet could not be sent.
     */
    public void sendCommand(final String command)
    {
        new Task().execute(command);
    }

    class Task extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... s)
        {
            String command = s[0];
            InetAddress host;
            try {
                host = InetAddress.getByName(CommandSender.this.hostName);
            }catch(UnknownHostException e)
            {
                return "Could not find host!";
            }

            try {
                byte[] buf = command.getBytes();
                DatagramSocket socket = new DatagramSocket(port);
                DatagramPacket packet = new DatagramPacket(buf, buf.length, host, port);
                socket.send(packet);
                socket.close();
                return "Command " + command + " sent!";
            }catch(IOException e)
            {
                return "Could not connect!";
            }
        }

        @Override
        protected void onPostExecute(String toastText) {
            super.onPostExecute(toastText);
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

        }
    }
}
