package com.sap.dkom.fiorirace;

import android.app.Activity;
import android.hardware.Sensor;
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

import java.util.Scanner;

public class MainActivity extends Activity {

    public static final String TAG = "WearFiori";
    /**
     * Messages from Phone to Wear:
     */
    private MessageApi.MessageListener mMessageListener = new MessageApi.MessageListener() {
        @Override
        public void onMessageReceived(MessageEvent m) {
            Scanner s = new Scanner(m.getPath());
            String command = s.next();
            switch (command) {
                case "stop":
                    moveTaskToBack(true);
                    break;
                case "start":
                    Log.d(TAG, "mMessageListener start");
                    break;
                case "calibrate":
                    calibrate(true);
                    break;
                case "offCalibrate":
                    calibrate(false);
                    break;
            }
        }
    };

    private void calibrate(boolean isCalibrate) {
        if (isCalibrate) {
            calibrateTextView.post(new Runnable() {
                @Override
                public void run() {
                    calibrateTextView.setText("Calibrating!");
                }
            });
            mSensorEventListener.setCalibrate(true);
        } else {
            calibrateTextView.post(new Runnable() {
                @Override
                public void run() {
                    calibrateTextView.setText("GO!");
                }
            });
            mSensorEventListener.setCalibrate(false);
        }
    }

    private static final boolean D = true;
    SensorManager sensorManager = null;
    private OrientationSensorListener mSensorEventListener;
    private GoogleApiClient mGoogleApiClient = null;
    private Node mPhoneNode = null;
    private TextView calibrateTextView;


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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mSensorEventListener = new OrientationSensorListener(this) {
            @Override
            protected void handleMovement(String direction) {
                calibrateTextView.setText(direction);
                sendDirction(direction);
            }
        };
        calibrateTextView = (TextView) findViewById(R.id.calibrate);

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
        sensorManager.unregisterListener(mSensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR));
        if (mPhoneNode != null) {
            sendToPhone("stop", null, null);
        } else {
            findPhoneNode();
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        sensorManager.registerListener(mSensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_GAME);
        if (mPhoneNode != null) {
            sendToPhone("start", null, null);
        } else {
            findPhoneNode();
        }
        super.onResume();
    }

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

    public void surfaceView_onClick(View view) {
        Log.d(TAG, "surfaceView_onClick");
        calibrate(!mSensorEventListener.isCalibrate());
    }

    public void sendDirction(String direction) {
        Log.d(TAG, "sendDirction");
        if (mPhoneNode != null) {
            sendToPhone(direction, null, null);
        }
    }


}
