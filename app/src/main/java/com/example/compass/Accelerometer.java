package com.example.compass;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class Accelerometer extends AppCompatActivity implements SensorEventListener {
    TextView value_X;
    TextView value_Y;
    TextView value_Z;
    int newMarginX;
    int newMarginY;
    ImageView marker;
    int gridWidth;
    FrameLayout markerView;
    DecimalFormat df = new DecimalFormat("#.###");
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    boolean haveSensor = false;
    private float[] mLastAccelerometer = new float[3];
    boolean mLastAccelerometerSet = false;
    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        value_X = findViewById(R.id.value_X);
        value_Y = findViewById(R.id.value_Y);
        value_Z = findViewById(R.id.value_Z);
        marker = findViewById(R.id.marker);
        markerView = findViewById(R.id.marker_grid);
        markerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                markerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                gridWidth = markerView.getWidth();
            }
        });

        start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        }

        value_X.setText(df.format(mLastAccelerometer[0]));
        value_Y.setText(df.format(mLastAccelerometer[1]));
        value_Z.setText(df.format(mLastAccelerometer[2]));
        newMarginX = (int) (gridWidth/2 - 40 + mLastAccelerometer[0]*gridWidth/20);
        newMarginY = (int) (gridWidth/2 - 40 + mLastAccelerometer[1]*gridWidth/20);
        lp.setMargins(newMarginX, newMarginY, 0, 0);
        marker.setLayoutParams(lp);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void start() {
        if ((mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) ) {
            noSensorsAlert();
        }
        else {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            haveSensor = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void noSensorsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device doesn't support the Compass.")
                .setCancelable(false)
                .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    public void stop() {
        if(haveSensor){
            mSensorManager.unregisterListener(this,mAccelerometer);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }
}
