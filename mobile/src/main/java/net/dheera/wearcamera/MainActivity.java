package net.dheera.wearcamera;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity implements SurfaceHolder.Callback {

    private GoogleApiClient mGoogleApiClient;
    private Node mWearableNode = null;

    private static final String TAG = "WearFiori";
    private static final boolean D = true;
    private int displayFrameLag = 0;
    private long lastMessageTime = 0;
    private long displayTimeLag = 0;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView;
    public boolean mPreviewRunning=false;

    /**
     * Messages from Wear to Phone
     */
    private MessageApi.MessageListener mMessageListener = new MessageApi.MessageListener() {
        @Override
        public void onMessageReceived (MessageEvent m){
            if(D) Log.d(TAG, "onMessageReceived: " + m.getPath());
            lastMessageTime = System.currentTimeMillis();
            Scanner s = new Scanner(m.getPath());
            String command = s.next();

            // Example to commands received from WEAR:
            if(command.equals("left")) {
                doMove("left");
            } else if(command.equals("right")) {
                doMove("right");
            } else if(command.equals("heart")) {
                String arg0 = "0";
                if (s.hasNext()) arg0 = s.next();
                showHB(arg0);
            }
            else if(command.equals("stop")) {
                moveTaskToBack(true);
            }
        }
    };

    private void doMove(String direction) {
        Log.d(TAG, "doMove" + direction);
    }

    private void showHB(String heartbit) {
        Log.d(TAG, "showHB" +heartbit);
    }

    private void findWearableNode() {
        PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                if(result.getNodes().size()>0) {
                    mWearableNode = result.getNodes().get(0);
                    if(D) Log.d(TAG, "Found wearable: name=" + mWearableNode.getDisplayName() + ", id=" + mWearableNode.getId());
                } else {
                    mWearableNode = null;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(D) Log.d(TAG, "onDestroy");
        Wearable.MessageApi.removeListener(mGoogleApiClient, mMessageListener);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(D) Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle connectionHint) {
                            if(D) Log.d(TAG, "onConnected: " + connectionHint);
                            findWearableNode();
                            Wearable.MessageApi.addListener(mGoogleApiClient, mMessageListener);
                        }
                        @Override
                        public void onConnectionSuspended(int cause) {
                            if(D) Log.d(TAG, "onConnectionSuspended: " + cause);
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            if(D) Log.d(TAG, "onConnectionFailed: " + result);
                        }
                    })
                    .addApi(Wearable.API)
                    .build();
            mGoogleApiClient.connect();


        // slowly subtract from the lag; in case the lag
        // is occurring due to transmission errors
        // this will un-stick the application
        // from a stuck state in which displayFrameLag>6
        // and nothing gets transmitted (therefore nothing
        // else pulls down displayFrameLag to allow transmission
        // again)

        lastMessageTime = System.currentTimeMillis();

        Timer mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(displayFrameLag>1) { displayFrameLag--; }
                if(displayTimeLag>1000) { displayTimeLag-=1000; }
            }
        }, 0, 1000);
    }

    public void surfaceView_onClick(View view) {
        // Just for an example:
        doMove("left");
    }

    private void sendToWearable(String path, byte[] data, final ResultCallback<MessageApi.SendMessageResult> callback) {
        if (mWearableNode != null) {
            PendingResult<MessageApi.SendMessageResult> pending = Wearable.MessageApi.sendMessage(mGoogleApiClient, mWearableNode.getId(), path, data);
            pending.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult result) {
                    if (callback != null) {
                        callback.onResult(result);
                    }
                    if (!result.getStatus().isSuccess()) {
                        if(D) Log.d(TAG, "ERROR: failed to send Message: " + result.getStatus());
                    }
                }
            });
        } else {
            if(D) Log.d(TAG, "ERROR: tried to send message before device was found");
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
        if (mSurfaceHolder.getSurface() == null){
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

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mPreviewRunning = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
