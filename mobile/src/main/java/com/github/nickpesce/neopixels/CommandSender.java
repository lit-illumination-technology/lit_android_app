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
    private MainActivity mainActivity;
    public CommandSender(final Context context, final String hostName, int port)
    {
        this.context = context;
        this.hostName = hostName;
        this.port = port;
    }

    public CommandSender(final Context context, MainActivity activity, final String hostName, int port)
    {
        this(context, hostName, port);
        this.mainActivity = activity;
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
        String command = "";
        @Override
        protected String doInBackground(String... s)
        {
            command = s[0];
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
                buf = new byte[1024];
                DatagramPacket rec = new DatagramPacket(buf, buf.length);
                socket.receive(rec);
                socket.close();
                return new String(rec.getData());
            }catch(IOException e)
            {
                return "Could not connect!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result == null)return;
            if(command.equals("commands") && mainActivity != null)
                mainActivity.setCommands(result);
            else
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        }
    }
}
