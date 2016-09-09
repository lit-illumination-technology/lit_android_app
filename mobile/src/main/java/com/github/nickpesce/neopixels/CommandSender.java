package com.github.nickpesce.neopixels;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class CommandSender{

    private Context context;
    private SharedPreferences prefs;
    private RequestQueue queue;

    public CommandSender(final Context context)
    {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        queue = Volley.newRequestQueue(context);
    }

    public void sendCommand(JSONObject command) {
        String port = prefs.getString("port", "42297");
        String url = prefs.getString("hostname", "nickspi.student.umd.edu") + ":" + port + "/command";
        //Build the request(With callbacks)
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, command,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String res;
                        try {
                            response.getBoolean("status");
                            res = response.getString("result");
                        } catch (JSONException e) {
                            Toast.makeText(context, ""+e, Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            return;
                        }
                        Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse!=null)
                            Toast.makeText(context, new String(error.networkResponse.data), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "Response Error", Toast.LENGTH_SHORT).show();

                    }
                }) {
            //add the basic authentication headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap();
                String name = prefs.getString("username", "admin");
                String pass = prefs.getString("password", "pass123");
                String credentials = name + ":" + pass;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }
        };
        //Add the request to the queue
        queue.add(request);
    }

    public void startEffect(String JSON) {
        JSONObject command;
        try {
            command = new JSONObject(JSON);
            sendCommand(command);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startEffect(String effect, HashMap<String, Object> args)
    {
        JSONObject command = new JSONObject();
        //Build the request payload
        try {
            command.put("effect", effect);
            if(args != null && !args.isEmpty())
                command.put("args", new JSONObject(args));
            sendCommand(command);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getEffects(final CommandEditor requester) {
        String port = prefs.getString("port", "12345");
        String url = null;
        url = prefs.getString("hostname", "host.example.net") + ":" + port + "/get_effects.json";
        prefs.getString("password", "pass123");


        //Build the request(With callbacks)
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray effects;
                        try {
                            effects = response.getJSONArray("effects");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                        //LinkedHashMap preserves insertion order
                        LinkedHashMap<String, Byte> ret = new LinkedHashMap();
                        for(int i = 0; i < effects.length(); i++) {
                            try {
                                JSONObject effect = (JSONObject) effects.get(i);
                                ret.put(effect.getString("name"), (byte) (effect.getInt("modifiers")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        requester.callBackEffects(ret);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        requester.callBackError("Could not connect: " + error.toString());
                        Toast.makeText(context, "Could not connect: " + error.toString(), Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }
        );

        //Add the request to the queue
        queue.add(request);
    }




}
