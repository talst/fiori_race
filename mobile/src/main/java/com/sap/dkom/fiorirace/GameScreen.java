package com.sap.dkom.fiorirace;

import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.util.Scanner;

public class GameScreen implements Screen, GestureListener {

    private static final boolean D = true;
    private static final String TAG = "Mobile";
    private final GoogleApiClient mGoogleApiClient;
    private Stage stage;
    private TrafficGame trafficGame;

    private MessageApi.MessageListener mMessageListener = new MessageApi.MessageListener() {
        public long lastMessageTime;

        @Override
        public void onMessageReceived(MessageEvent m) {
            lastMessageTime = System.currentTimeMillis();
            Scanner s = new Scanner(m.getPath());
            String command = s.next();

            if (command.equals("left")) {
                trafficGame.playerCar.tryMoveUp();
            } else if (command.equals("right")) {
                trafficGame.playerCar.tryMoveDown();
            }
        }
    };

    public GameScreen(Game fioriRace, MainActivity activity, Music gameMusic) {

        stage = new Stage(new StretchViewport(FioriRace.WIDTH, FioriRace.HEIGHT));
        trafficGame = new TrafficGame(fioriRace, activity, gameMusic);
        stage.addActor(trafficGame);
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.getAppContext())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        if (D) Log.d(TAG, "onConnected: " + connectionHint);
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
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new GestureDetector(this));
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        if (velocityY < -100) trafficGame.playerCar.tryMoveUp();
        if (velocityY > 100) trafficGame.playerCar.tryMoveDown();
        return false;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
}
