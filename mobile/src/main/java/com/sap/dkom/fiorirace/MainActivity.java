package com.sap.dkom.fiorirace;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
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
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AndroidApplication implements SurfaceHolder.Callback {

    private static final String TAG = "WearFiori";
    private static final boolean D = true;
    private static Context context;
    public boolean mPreviewRunning = false;
    private GoogleApiClient mGoogleApiClient;
    private Node mWearableNode = null;
    private int displayFrameLag = 0;
    private long lastMessageTime = 0;
    /**
     * Messages from Wear to Phone
     */
    private MessageApi.MessageListener mMessageListener = new MessageApi.MessageListener() {
        @Override
        public void onMessageReceived(MessageEvent m) {
            if (D) Log.d(TAG, "onMessageReceived: " + m.getPath());
            lastMessageTime = System.currentTimeMillis();
            Scanner s = new Scanner(m.getPath());
            String command = s.next();

            // Showing the action in the Main Text View:
//            TextView myAwesomeTextView = (TextView)findViewById(R.id.textView1);
//            myAwesomeTextView.setText(command);

            // Example to commands received from WEAR:
            if (command.equals("left")) {
                doMove("left");
            } else if (command.equals("right")) {
                doMove("right");
            } else if (command.equals("heart")) {
                String arg0 = "0";
                if (s.hasNext()) arg0 = s.next();
                showHB(arg0);
            } else if (command.equals("stop")) {
                Gdx.app.exit();
                //moveTaskToBack(true);
            }
        }
    };
    private long displayTimeLag = 0;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView;

    public static Context getAppContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (D) Log.d(TAG, "onCreate");
        MainActivity.context = getApplicationContext();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        if (D) Log.d(TAG, "onConnected: " + connectionHint);
                        findWearableNode();
                        Wearable.MessageApi.addListener(mGoogleApiClient, mMessageListener);
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        if (D) Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        if (D) Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();

        lastMessageTime = System.currentTimeMillis();

        Timer mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (displayFrameLag > 1) {
                    displayFrameLag--;
                }
                if (displayTimeLag > 1000) {
                    displayTimeLag -= 1000;
                }
            }
        }, 0, 1000);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        FioriRace race = new FioriRace();
        race.setActivity(this);
        initialize(race, config);
    }

    public void onBackPressed() {
        finish();
        this.sendToWearable("stop", null, null);
        Gdx.app.exit();
    }

    private void doMove(String direction) {
        Log.d(TAG, "doMove" + direction);
    }

    private void showHB(String heartbit) {
        Log.d(TAG, "showHB" + heartbit);
    }

    private void findWearableNode() {
        PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                if (result.getNodes().size() > 0) {
                    mWearableNode = result.getNodes().get(0);
                    if (D)
                        Log.d(TAG, "Found wearable: name=" + mWearableNode.getDisplayName() + ", id=" + mWearableNode.getId());
                } else {
                    mWearableNode = null;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (D) Log.d(TAG, "onDestroy");
        Wearable.MessageApi.removeListener(mGoogleApiClient, mMessageListener);
        super.onDestroy();
    }

    public void surfaceView_onClick(View view) {
        // Just for an example:
        doMove("left");
    }

    public void sendToWearable(String path, byte[] data, final ResultCallback<MessageApi.SendMessageResult> callback) {

        if (mWearableNode != null) {
            Log.d(TAG, "Trying to send message " + path);
            PendingResult<MessageApi.SendMessageResult> pending = Wearable.MessageApi.sendMessage(mGoogleApiClient, mWearableNode.getId(), path, data);
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

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        lastMessageTime = System.currentTimeMillis();
        super.onResume();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.d(TAG, "surfaceChanged");
        if (mSurfaceHolder.getSurface() == null) {
            return;
        }

        // Example to sent to wear:
        /*byte[] baos = null;
        sendToWearable(String.format("show %d", System.currentTimeMillis()), baos, new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult result) {
             if(displayFrameLag>0) displayFrameLag--;
            }
        });*/
        mPreviewRunning = true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mPreviewRunning = false;
    }
}

