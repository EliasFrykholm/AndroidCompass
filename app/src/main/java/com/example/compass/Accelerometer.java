package com.example.compass;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class Accelerometer extends AppCompatActivity implements SensorEventListener {
    TextView value_X;
    TextView value_Y;
    TextView value_Z;
    DecimalFormat df = new DecimalFormat("#.###");
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    boolean haveSensor = false;
    private float[] mLastAccelerometer = new float[3];
    boolean mLastAccelerometerSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        value_X = findViewById(R.id.value_X);
        value_Y = findViewById(R.id.value_Y);
        value_Z = findViewById(R.id.value_Z);

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
