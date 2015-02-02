package com.sap.dkom.fiorirace;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;


abstract public class OrientationSensorListener implements SensorEventListener {
    private static final String TAG = "Sensor listener";
    private final float[] angleChange = new float[3];
    private final Vibrator vibrator;
    private float[] zeroMatrix = null;
    private boolean moved;
    private float[] currentMatrix = new float[16];
    private long timestamp;
    private boolean calibrate = false;


    public OrientationSensorListener(MainActivity mainActivity) {
        vibrator = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public boolean isCalibrate() {
        return calibrate;
    }

    public void setCalibrate(boolean calibrate) {
        this.calibrate = calibrate;
    }

    @Override
    synchronized public void onSensorChanged(SensorEvent event) {

        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;


        if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            if (calibrate) {
                calibrate(event);
            } else if (moved && zeroMatrix != null) {
                long dT = event.timestamp - timestamp;
                if (dT > 1_000_000_000l) {
                    moved = false;
                } else {
                    calcAngelChange(event);
                    if (Math.abs(angleChange[0]) < 0.1 && Math.abs(angleChange[1]) < 0.1) {
                        vibrator.vibrate(200);
                        moved = false;
                    }
                }
            } else if (zeroMatrix != null) {
                calcAngelChange(event);
                if (angleChange[0] > 0.45 && angleChange[1] < 0) {
                    timestamp = event.timestamp;
                    vibrator.vibrate(200);
                    moveRight();
                } else if (angleChange[0] < -0.45 && angleChange[1] > 0) {
                    timestamp = event.timestamp;
                    vibrator.vibrate(200);
                    moveLeft();
                }
            } else {
                Log.d(TAG, "not calibrated");
            }
        }
    }

    private void moveLeft() {
        Log.d(TAG, "Move left");
        move("left");
    }

    private void moveRight() {
        Log.d(TAG, "Move right");
        move("right");
    }

    private void calcAngelChange(SensorEvent event) {
        SensorManager.getRotationMatrixFromVector(currentMatrix, event.values);
        SensorManager.getAngleChange(angleChange, currentMatrix, zeroMatrix);
    }

    private void calibrate(SensorEvent event) {
        zeroMatrix = new float[16];
        SensorManager.getRotationMatrixFromVector(zeroMatrix, event.values);
    }

    private void move(String direction) {
        Log.d(TAG, direction);
        moved = true;
        handleMovement(direction);
    }

    protected abstract void handleMovement(String direction);

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}