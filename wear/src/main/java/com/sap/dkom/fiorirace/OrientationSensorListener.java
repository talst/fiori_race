package com.sap.dkom.fiorirace;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.text.DecimalFormat;


abstract public class OrientationSensorListener implements SensorEventListener {
    private static final String TAG = "Sensor listener";
    private final DecimalFormat decimalFormat = new DecimalFormat("#0.000");
    private final float[] tempCurrent = new float[16];
    private final float[] angleChange = new float[3];
    private float[] currentMatrix = null;
    private boolean movedLeft = false;
    private boolean movedRight = false;

    protected OrientationSensorListener() {
    }

    @Override
    synchronized public void onSensorChanged(SensorEvent event) {

        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            if (currentMatrix == null) {
                calibrate(event);
            }
            calcAngelChange(event);
            if (angleChange[0] > 0.45 && angleChange[1] < 0) {
                currentMatrix = tempCurrent.clone();
                if (!movedLeft) {
                    moveRight();
                } else {
                    unlockFromLeft();
                }
            } else if (angleChange[0] < -0.45 && angleChange[1] > 0) {
                currentMatrix = tempCurrent.clone();
                if (!movedRight) {
                    moveLeft();
                } else {
                    unlockFromRight();
                }
            }
        }
    }

    private void unlockFromRight() {
        Log.d(TAG, "Unlocked from right:\n" +
                "X: " + decimalFormat.format(angleChange[0]) + "\n" +
                "Y: " + decimalFormat.format(angleChange[1]));
        movedRight = false;
    }

    private void moveLeft() {
        Log.d(TAG, "Move left, locking:\n" +
                "X: " + decimalFormat.format(angleChange[0]) + "\n" +
                "Y: " + decimalFormat.format(angleChange[1]));
        move("left");
        movedLeft = true;
    }

    private void unlockFromLeft() {
        Log.d(TAG, "Unlocked from left:\n" +
                "X: " + decimalFormat.format(angleChange[0]) + "\n" +
                "Y: " + decimalFormat.format(angleChange[1]));
        movedLeft = false;
    }

    private void moveRight() {
        Log.d(TAG, "Move right, locking:\n" +
                "X: " + decimalFormat.format(angleChange[0]) + "\n" +
                "Y: " + decimalFormat.format(angleChange[1]));
        move("right");
        movedRight = true;
    }

    private void calcAngelChange(SensorEvent event) {
        float[] prevMatrix = currentMatrix.clone();
        SensorManager.getRotationMatrixFromVector(tempCurrent, event.values);
        SensorManager.getAngleChange(angleChange, tempCurrent, prevMatrix);
    }

    private void calibrate(SensorEvent event) {
        currentMatrix = new float[16];
        SensorManager.getRotationMatrixFromVector(currentMatrix, event.values);
    }

    private void move(String direction) {
        Log.d(TAG, direction);
        handleMovement(direction);
    }

    protected abstract void handleMovement(String direction);

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}