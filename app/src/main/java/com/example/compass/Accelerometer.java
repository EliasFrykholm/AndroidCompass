package com.example.compass;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class Accelerometer extends AppCompatActivity implements SensorEventListener {
    static final float ALPHA = 0.25f; // if ALPHA = 1 OR 0, no filter applies.
    TextView value_X;
    TextView value_Y;
    TextView value_Z;
    int defaultTextColor;
    int errorTextColor;
    int newMarginX;
    int newMarginY;
    ImageView marker;
    int gridWidth;
    FrameLayout markerView;
    int newMarginZ;
    ImageView zMarker;
    int zScaleWidth;
    FrameLayout zScale;
    DecimalFormat df = new DecimalFormat("#.#");
    MediaPlayer mediaPlayer;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    boolean haveSensor = false;
    private float[] mLastAccelerometer = new float[3];
    boolean mLastAccelerometerSet = false;
    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        mediaPlayer = MediaPlayer.create(this, R.raw.pling);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        value_X = findViewById(R.id.value_X);
        value_Y = findViewById(R.id.value_Y);
        value_Z = findViewById(R.id.value_Z);

        value_X.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                value_X.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                defaultTextColor = value_X.getCurrentTextColor();
            }
        });
        errorTextColor = getResources().getColor(R.color.design_default_color_error);

        marker = findViewById(R.id.marker);
        markerView = findViewById(R.id.marker_grid);
        markerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                markerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                gridWidth = markerView.getWidth();
            }
        });

        zMarker = findViewById(R.id.zMarker);
        zScale = findViewById(R.id.zScale);
        zScale.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                zScale.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                zScaleWidth = zScale.getWidth();
            }
        });

        start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mLastAccelerometer = lowPass(event.values.clone(), mLastAccelerometer);
            mLastAccelerometerSet = true;
        }

        value_X.setText(df.format(-mLastAccelerometer[0]));
        if(Math.abs(mLastAccelerometer[0])>5){
            value_X.setTextColor(errorTextColor);
        } else{
            value_X.setTextColor(defaultTextColor);
        }
        value_Y.setText(df.format(-mLastAccelerometer[1]));
        if(Math.abs(mLastAccelerometer[1])>5){
            value_Y.setTextColor(errorTextColor);
        } else{
            value_Y.setTextColor(defaultTextColor);
        }
        value_Z.setText(df.format(mLastAccelerometer[2]));
        if(Math.abs(mLastAccelerometer[2])>5){
            value_Z.setTextColor(errorTextColor);
        } else{
            value_Z.setTextColor(defaultTextColor);
        }

        if(Math.hypot(mLastAccelerometer[0], mLastAccelerometer[1]) > 5){
            marker.setImageResource(R.drawable.circle_primary);
        } else {
            if(Math.hypot(mLastAccelerometer[0], mLastAccelerometer[1]) < 0.3 && !mediaPlayer.isPlaying()){
                mediaPlayer.start();
            }
            marker.setImageResource(R.drawable.circle_accent);
        }

        newMarginX = (int) (gridWidth/2 - 50 + -mLastAccelerometer[0]*gridWidth/20);
        newMarginY = (int) (gridWidth/2 - 50 + mLastAccelerometer[1]*gridWidth/20);
        lp.setMargins(newMarginX, newMarginY, 0, 0);
        marker.setLayoutParams(lp);

        if(Math.abs(mLastAccelerometer[2]) > 5){
            zMarker.setImageResource(R.drawable.line_primary);
        } else {
            zMarker.setImageResource(R.drawable.line_accent);
        }
        newMarginZ = (int) (zScaleWidth/2 - 30 + mLastAccelerometer[2]*zScaleWidth/20);
        lp2.setMargins(newMarginZ, 0, 0, 0);
        zMarker.setLayoutParams(lp2);
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
        mSensorManager.unregisterListener(this);
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

    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }
}
