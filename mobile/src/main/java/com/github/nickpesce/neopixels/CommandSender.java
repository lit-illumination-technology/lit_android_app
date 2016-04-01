package com.github.nickpesce.neopixels;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class CommandSender{

    private Context context;
    private MainActivity mainActivity;
    private SharedPreferences prefs;
    public CommandSender(final Context context)
    {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public CommandSender(final Context context, MainActivity activity)
    {
        this(context);
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

    /**
     * Send the command asynchronously.
     */
    class Task extends AsyncTask<String, Void, String>
    {
        String command = "";
        @Override
        protected String doInBackground(String... s)
        {
            command = s[0];
            if(command == null)
                return null;
            InetAddress host;
            try {
                //get the host name from preferences.
                host = InetAddress.getByName(prefs.getString("hostname", "nickspi.student.umd.edu"));
            }catch(UnknownHostException e)
            {
                return "Could not find host!";
            }

            try {
                //get the port from preferences
                int port = Integer.parseInt(prefs.getString("port", "42297"));
                Socket socket = new Socket(host, port);
                //Set the socket to time out after 2s.
                socket.setSoTimeout(2000);
                //Create a stream to output data to.
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                //Print the data to the output stream
                out.println(command);

                //Create a stream to receive data from
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                String line;
                //Get all of the data sent back and put it in one string.
                String ret = in.readLine();
                while((line = in.readLine())!= null) {
                    ret += "\n" + line;
                }
                socket.close();
                return ret;
            }catch(IOException e)
            {
                e.printStackTrace();

                return "Could not connect!";
            }
        }

        /**
         * Deal with the returned data.
         * @param result The data that was returned from the connection
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result == null || command == null)return;
            if(command.equals("commands") && mainActivity != null)
                mainActivity.setCommands(result);
            else
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        }
    }
}
