package com.sap.dkom.fiorirace;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;


abstract public class OrientationSensorListener implements SensorEventListener {
    private static final String TAG = "Sensor listener";
    private static final float NS2MS = 1.0f / 1000000.0f;
    private float timestamp;
    private float currentAzim;
    private float currentPitch;
    private float currentRoll;
    private boolean moved = false;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            if (event.timestamp == 0 || moved) {
                setCurrentValues(event);
                moved = false;
                timestamp = event.timestamp;
            } else {
                float dT = (event.timestamp - timestamp) * NS2MS;
                if (dT > 350) {
                    timestamp = event.timestamp;
                    float dAzim = calcDeltaAzim(event);
                    float dPitch = calcDeltaPitch(event);
                    float dRoll = calcDeltaRoll(event);
                    if (dPitch > 0.35) {
                        move("right");
                    } else if (dPitch < -0.35) {
                        move("left");
                    }
                }
            }
        }
    }

    private void move(String direction) {
        Log.d(TAG, direction);
        handleMovement(direction);
        moved = true;
    }

    protected abstract void handleMovement(String direction);

    private float calcDeltaRoll(SensorEvent event) {
        float lastRoll = currentRoll;
        currentRoll = event.values[2];
        return currentRoll - lastRoll;
    }

    private float calcDeltaPitch(SensorEvent event) {
        float lastPitch = currentPitch;
        currentPitch = event.values[1];
        return currentPitch - lastPitch;
    }

    private float calcDeltaAzim(SensorEvent event) {
        float lastAzim = currentAzim;
        currentAzim = event.values[0];
        return currentAzim - lastAzim;
    }

    private void setCurrentValues(SensorEvent event) {
        currentAzim = event.values[0];
        currentPitch = event.values[1];
        currentRoll = event.values[2];
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}