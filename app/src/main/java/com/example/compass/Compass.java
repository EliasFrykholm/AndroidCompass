package com.example.compass;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.TextView;

public class Compass extends AppCompatActivity implements SensorEventListener {
    ImageView compass_img;
    TextView txt_compass;
    int mAzimuth;
    private SensorManager mSensorManager;
    private Vibrator vibrator;
    private int previousVibration = -1;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    boolean haveSensor = false, haveSensor2 = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        compass_img = findViewById(R.id.img_compass);
        txt_compass = findViewById(R.id.txt_azimuth);

        start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        mAzimuth = Math.round(mAzimuth);
        compass_img.setRotation(-mAzimuth);

        String where = "NW";
        if (mAzimuth >= 345 || mAzimuth <= 15) {
            where = "N";
            compass_img.setImageResource(R.drawable.ic_compass_n);
            if(previousVibration != 0){
                vibrator.vibrate(new long[]{0, 100, 100}, 0);
                previousVibration = 0;
            }
        } else if (mAzimuth <= 195 && mAzimuth > 165){
            where = "S";
            compass_img.setImageResource(R.drawable.ic_compass_s);
            if(previousVibration != -1){
                vibrator.cancel();
                previousVibration = -1;
            }
        } else if (mAzimuth <= 105 && mAzimuth > 75){
            where = "E";
            compass_img.setImageResource(R.drawable.ic_compass_e);
            if(previousVibration != 2){
                vibrator.vibrate(new long[]{0, 100, 800}, 0);
                previousVibration = 2;
            }
        } else if (mAzimuth <= 285 && mAzimuth > 255){
            where = "W";
            compass_img.setImageResource((R.drawable.ic_compass_w));
            if(previousVibration != 2){
                vibrator.vibrate(new long[]{0, 100, 800}, 0);
                previousVibration = 2;
            }
        } else {
            compass_img.setImageResource(R.drawable.ic_compass);
        }
        if (mAzimuth < 345 && mAzimuth > 285) {
            where = "NW";
            if (previousVibration != 1) {
                vibrator.vibrate(new long[]{0, 100, 400}, 0);
                previousVibration = 1;
            }
        }
        if (mAzimuth <= 255 && mAzimuth > 195){
            where = "SW";
            if(previousVibration != -1){
                vibrator.cancel();
                previousVibration = -1;
            }
        }
        if (mAzimuth <= 165 && mAzimuth > 105){
            where = "SE";
            if(previousVibration != -1){
                vibrator.cancel();
                previousVibration = -1;
            }
        }

        if (mAzimuth <= 75 && mAzimuth > 15){
            where = "NE";
            if(previousVibration != 1){
                vibrator.vibrate(new long[]{0, 100, 400}, 0);
                previousVibration = 1;
            }
        }
        txt_compass.setText(mAzimuth + "° " + where);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void start() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)) {
                noSensorsAlert();
            }
            else {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        }
        else{
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI);
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
        mSensorManager.unregisterListener(this);
        vibrator.cancel();
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
