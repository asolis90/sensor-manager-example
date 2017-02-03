package com.asolis.sensormanagerexample;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    SensorManager mSensorManager;
    Sensor mAccelerometer;
    Sensor mMagnetometer;
    ImageView mCompass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mCompass = (ImageView) findViewById(R.id.compass);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    float[] magnetometerArr;
    float[] accelerometerArr;
    float azimut = 0f;
    float  currentDegree = 0f;
    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        switch(sensorEvent.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerArr = sensorEvent.values;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magnetometerArr = sensorEvent.values;
                break;
        }

        if (accelerometerArr != null && magnetometerArr != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            float azimutDegrees = 0;
            boolean success = SensorManager.getRotationMatrix(R, I, accelerometerArr, magnetometerArr);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0]; // orientation contains: azimut, pitch and roll in radians
                azimutDegrees = (float) (Math.toDegrees(azimut)+360)%360; // get azimut to degrees
            }
            RotateAnimation anim = new RotateAnimation(currentDegree, -azimutDegrees, Animation
                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(250);
            anim.setFillAfter(true);
            mCompass.startAnimation(anim);
            currentDegree = -azimutDegrees;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {
    }


    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }
}
