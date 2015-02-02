package com.sap.dkom.fiorirace;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by I040335 on 02/02/2015.
 */
public class GameOverScreen implements Screen, GestureListener {
    private SpriteBatch batch;
    private Game myGame;
    private Texture texture;
    private OrthographicCamera camera;
    private long startTime;
    private int rendCount;
    private GameScreen screen;
    private MainActivity activity;
    private int score;

    public GameOverScreen(Game g, MainActivity activity, int score) // ** constructor called initially **//
    {
        this.score = score;
        this.activity = activity;
        this.screen = screen;
        Gdx.app.log("my Spash Screen", "constructor called");
        myGame = g; // ** get Game parameter **//
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(texture, 0, 0);
        batch.end();
        rendCount++;
        /*if (TimeUtils.millis()>(startTime+5000)) {
            activity.sendToWearable("offCalibrate", null, null);
            myGame.setScreen(this.screen);
        }*/
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new GestureDetector(this));
        Gdx.app.log("my Splash Screen", "show called");
        texture = new Texture(Gdx.files.internal("gameover.jpg")); //** texture is now the splash image **//

        startTime = TimeUtils.millis();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        Gdx.app.log("my Splash Screen", "hide called");
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        texture.dispose();
        batch.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        myGame.setScreen(new SplashScreen(myGame,
                new GameScreen(myGame, activity), activity));
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
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
