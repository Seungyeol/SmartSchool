package com.aura.smartschool.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepListener implements SensorEventListener {
    private static final String TAG = "StepListener";
    private SensorManager mSensorManager;
    private Context context;

    private long lastTime;
    public static int steps;
    public static int bigSteps;
    private int tempCount;

    public StepListener(Context context) {
        this.context = context;
    }

    public void start() {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stop() {
        mSensorManager.unregisterListener(this);
        steps = 0;
        bigSteps = 0;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float accelerationSquareRoot =  + ((x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH)) - 1.0f;
        long actualTime = System.currentTimeMillis();
        if (actualTime - lastTime > 300) {
            if(accelerationSquareRoot < -0.45f){
                steps++;
                tempCount++;
                if(accelerationSquareRoot < -1.0f){
                    bigSteps++;
                }
                if(steps%50==0){
                    steps++;
                }
                if(tempCount==4){
                    steps = 0;
                }
                lastTime = actualTime;
            }
        }

    }

    public  int getSteps(){
        return steps;
    }

    public int getBigsteps(){
        return bigSteps;
    }
}
