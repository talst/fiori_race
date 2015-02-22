package com.sap.dkom.fiorirace;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

public class DataLayerListenerService extends WearableListenerService {

    private static final String TAG = "WearListenerService";
    private static final boolean D = false;

    @Override
    public void onCreate() {
        if (D) Log.d(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (D) Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onPeerConnected(Node peer) {
        if (D) Log.d(TAG, "onPeerConnected");
        super.onPeerConnected(peer);
        if (D) Log.d(TAG, "Connected: name=" + peer.getDisplayName() + ", id=" + peer.getId());
    }

    @Override
    public void onMessageReceived(MessageEvent m) {
        if (D) Log.d(TAG, "onMessageReceived: " + m.getPath());
        if (m.getPath().equals("start") && !isForeground(getPackageName())) {
            Intent startIntent = new Intent(this, MainActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        }
    }

    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);

        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
    }
}