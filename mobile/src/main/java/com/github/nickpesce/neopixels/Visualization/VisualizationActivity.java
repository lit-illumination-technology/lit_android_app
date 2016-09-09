package com.github.nickpesce.neopixels.Visualization;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;

import com.github.nickpesce.neopixels.R;

public class VisualizationActivity extends AppCompatActivity {

    public static final int MY_REQUEST_RECORD_AUDIO = 34;

    private LightView lightView;
    private Visualization visualization;

    public void update(byte[] pixels) {
        lightView.updatePixels(pixels);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        visualization = new Visualization(this);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_REQUEST_RECORD_AUDIO);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    visualization.start();
                }
            }).start();
        }

        setContentView(R.layout.activity_visualization);
        lightView = (LightView)findViewById(R.id.lightView);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MY_REQUEST_RECORD_AUDIO:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        visualization.start();
                    }
                }).start();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //visualization.destroy();
    }
}
