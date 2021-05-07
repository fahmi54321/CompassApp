package com.android.a39compassapp;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView imageView;
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private float azimuth = 0f;
    private float currectAzimuth = 0f;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.compass);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        final float alpha = 0.97f;
        synchronized (this){
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                gravity[0] = alpha*gravity[0]+(1-alpha)*sensorEvent.values[0];
                gravity[1] = alpha*gravity[1]+(1-alpha)*sensorEvent.values[1];
                gravity[2] = alpha*gravity[2]+(1-alpha)*sensorEvent.values[2];
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                geomagnetic[0] = alpha*geomagnetic[0]+(1-alpha)*sensorEvent.values[0];
                geomagnetic[1] = alpha*geomagnetic[1]+(1-alpha)*sensorEvent.values[1];
                geomagnetic[2] = alpha*geomagnetic[2]+(1-alpha)*sensorEvent.values[2];
            }

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R,I,gravity,geomagnetic);
            if (success){
                float orientation[] = new float[3];
                SensorManager.getOrientation(R,orientation);
                azimuth = (float)Math.toDegrees(orientation[0]);
                azimuth = (azimuth+360) %360;

                Animation animation = new RotateAnimation(-currectAzimuth,-azimuth,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                 animation.setDuration(500);
                 animation.setRepeatCount(0);
                 animation.setFillAfter(true);

                 imageView.startAnimation(animation);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}