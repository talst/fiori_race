package com.sap.dkom.fiorirace;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by I040335 on 02/02/2015.
 */
public class GameOverScreen implements Screen, GestureListener {

    private final Music gameMusic;
    private final int score;
    private Stage stage;
    private SpriteBatch batch;
    private Game myGame;
    private Texture texture;
    private OrthographicCamera camera;
    private MainActivity activity;
    Label scoreLabel;
    Group group;

    public GameOverScreen(Game g, MainActivity activity, int score, Music gameMusic) // ** constructor called initially **//
    {
        this.score = score;
        this.gameMusic = gameMusic;
        this.activity = activity;

        myGame = g; // ** get Game parameter **//
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);
        batch = new SpriteBatch();

        Label.LabelStyle textStyle = new Label.LabelStyle();
        textStyle.font = new BitmapFont();

        group = new Group();
        group.setBounds(150f, 200f, 180, 180);
        group.addAction(Actions.rotateBy(-90));

        scoreLabel = new Label(score + "", textStyle);
        scoreLabel.setFontScale(4f);
        scoreLabel.setX(140);
        scoreLabel.setColor(Color.YELLOW);
        group.addActor(scoreLabel);

        CarIcon icon = new CarIcon();
        icon.setRotation(90);
        icon.setX(0);
        icon.setScale(0.4f);
        group.addActor(icon);

        stage = new Stage(new StretchViewport(FioriRace.WIDTH, FioriRace.HEIGHT));
        stage.addActor(group);
    }

    public void postData(String name, String color, String score) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://leaderboardi305845trial.hanatrial.ondemand.com/test/Leaderboard");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("value", score));
            nameValuePairs.add(new BasicNameValuePair("color", color));
            nameValuePairs.add(new BasicNameValuePair("name", name));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            httpclient.execute(httppost);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().setProjectionMatrix(camera.combined);
        stage.getBatch().begin();
        stage.getBatch().draw(texture, 0, 0);
        stage.getBatch().end();

        stage.act(delta);
        stage.draw();


        postData(TrafficGame.NAME, getColor(), String.valueOf(score));
    }

    private String getColor() {
        if (TrafficGame.GAME_COLOR == Color.GREEN) {
            return "green";
        } else if (TrafficGame.GAME_COLOR == Color.RED) {
            return "red";
        } else if (TrafficGame.GAME_COLOR == Color.YELLOW) {
            return "yellow";
        } else if (TrafficGame.GAME_COLOR == Color.BLUE) {
            return "blue";
        }
        return "black";
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new GestureDetector(this));
        texture = new Texture(Gdx.files.internal("gameover.jpg")); //** texture is now the splash image **//

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
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
                new GameScreen(myGame, activity, gameMusic), activity));
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
