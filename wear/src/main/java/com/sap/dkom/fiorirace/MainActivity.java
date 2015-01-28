package com.sap.dkom.fiorirace;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.text.DecimalFormat;
import java.util.Scanner;

public class MainActivity extends Activity implements SensorEventListener {

    public static final String TAG = "WearFiori";
    /**
     * Messages from Phone to Wear:
     */
    private MessageApi.MessageListener mMessageListener = new MessageApi.MessageListener() {
        @Override
        public void onMessageReceived(MessageEvent m) {
            Scanner s = new Scanner(m.getPath());
            String command = s.next();
            if (command.equals("stop")) {
                moveTaskToBack(true);
            } else if (command.equals("start")) {
                Log.d(TAG, "mMessageListener start");
            }
        }
    };
    private static final boolean D = true;
    private static final float NS2MS = 1.0f / 1000000.0f;
    SensorManager sensorManager = null;
    private GoogleApiClient mGoogleApiClient = null;
    private Node mPhoneNode = null;
    private float timestamp;
    private float currentAzim;
    private float currentPitch;
    private float currentRoll;
    private TextView outputAzim;
    private TextView outputPitch;
    private TextView outputRoll;
    private TextView outputMove;
    private DecimalFormat decimalFormat;

    void findPhoneNode() {
        PendingResult<NodeApi.GetConnectedNodesResult> pending = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        pending.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                if (result.getNodes().size() > 0) {
                    mPhoneNode = result.getNodes().get(0);
                    if (D)
                        Log.d(TAG, "Found phone: name=" + mPhoneNode.getDisplayName() + ", id=" + mPhoneNode.getId());
                    sendToPhone("start", null, null);
                } else {
                    mPhoneNode = null;
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPhoneNode != null) {
            sendToPhone("stop", null, new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e(TAG, "ERROR: failed to send Message: " + result.getStatus());
                    }
                    moveTaskToBack(true);
                }
            });
        } else {
            findPhoneNode();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPhoneNode != null) {
            sendToPhone("stop", null, new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e(TAG, "ERROR: failed to send Message: " + result.getStatus());
                    }
                    moveTaskToBack(true);
                }
            });
        }
        Wearable.MessageApi.removeListener(mGoogleApiClient, mMessageListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.activity_main);
        outputAzim = (TextView) findViewById(R.id.azim);
        outputPitch = (TextView) findViewById(R.id.pitch);
        outputRoll = (TextView) findViewById(R.id.roll);
        outputMove = (TextView) findViewById(R.id.move);
        decimalFormat = new DecimalFormat("#0.000");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        findPhoneNode();
                        Wearable.MessageApi.addListener(mGoogleApiClient, mMessageListener);
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
        if (mPhoneNode != null) {
            sendToPhone("stop", null, null);
        } else {
            findPhoneNode();
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_GAME);
        if (mPhoneNode != null) {
            sendToPhone("start", null, null);
        } else {
            findPhoneNode();
        }
        super.onResume();
    }

    /**
     * Sending messages to Phone
     *
     * @param path
     * @param data
     * @param callback
     */
    private void sendToPhone(String path, byte[] data, final ResultCallback<MessageApi.SendMessageResult> callback) {
        Log.d(TAG, "sendToPhone " + path);
        if (mPhoneNode != null) {
            PendingResult<MessageApi.SendMessageResult> pending = Wearable.MessageApi.sendMessage(mGoogleApiClient, mPhoneNode.getId(), path, data);

            // Result from the phone:
            pending.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult result) {
                    if (callback != null) {
                        callback.onResult(result);
                    }
                    if (!result.getStatus().isSuccess()) {
                        if (D) Log.d(TAG, "ERROR: failed to send Message: " + result.getStatus());
                    }
                }
            });
        } else {
            if (D) Log.d(TAG, "ERROR: tried to send message before device was found");
        }
    }

    /**
     * Clicking on the wear surface
     *
     * @param view
     */
    public void surfaceView_onClick(View view) {
        Log.d(TAG, "surfaceView_onClick");
        // Just for an example:
        sendDirction("left");
    }

    private void sendDirction(String direction) {
        Log.d(TAG, "sendDirction");
        if (mPhoneNode != null) {
            sendToPhone(direction, null, null);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            if (event.timestamp == 0) {
                currentAzim = event.values[0];
                currentPitch = event.values[1];
                currentRoll = event.values[2];
            }
            float dT = (event.timestamp - timestamp) * NS2MS;
            if (dT > 350) {
                timestamp = event.timestamp;

                float lastAzim = currentAzim;
                currentAzim = event.values[0];
                float dAzim = currentAzim - lastAzim;
                outputAzim.setText("dAzim: " + decimalFormat.format(dAzim));
                Log.d(TAG, "dAzim: " + decimalFormat.format(dAzim));

                float lastPitch = currentPitch;
                currentPitch = event.values[1];
                float dPitch = currentPitch - lastPitch;
                outputPitch.setText("dPitch: " + decimalFormat.format(dPitch));
                Log.d(TAG, "dPitch: " + decimalFormat.format(dPitch));

                float lastRoll = currentRoll;
                currentRoll = event.values[2];
                float dRoll = currentRoll - lastRoll;
                outputRoll.setText("dRoll: " + decimalFormat.format(dRoll));
                Log.d(TAG, "dRoll: " + decimalFormat.format(dRoll));

                if (dRoll > 0.35) {
                    Log.d(TAG, "left");
                    outputMove.setText("left");
                } else if (dRoll < -0.35) {
                    Log.d(TAG, "right");
                    outputMove.setText("right");
                } else {
                    outputMove.setText("no movement");
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
