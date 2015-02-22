package com.sap.dkom.fiorirace;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;


/**
 * Created by I040335 on 02/02/2015.
 */
public class SplashScreen implements Screen {
    private Stage stage;
    private Label scoreLabel;
    private Group group;
    private SpriteBatch batch;
    private Game myGame;
    private Texture texture;
    private OrthographicCamera camera;
    private long startTime;
    private int rendCount;
    private GameScreen screen;
    private MainActivity activity;
    private final int CALIBRATION_TIME_IN_MS = 10000;

    public SplashScreen(Game g, GameScreen screen, MainActivity activity) // ** constructor called initially **//
    {
        this.activity = activity;
        this.screen = screen;
        activity.sendToWearable("calibrate", null, null);
        myGame = g; // ** get Game parameter **//*/
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);
        Label.LabelStyle textStyle = new Label.LabelStyle();
        BitmapFont font = new BitmapFont();
        textStyle.font = font;

        group = new Group();
        group.setBounds(100, 80f, 80f, 120f);
        group.addAction(Actions.rotateBy(-90));


        scoreLabel = new Label(String.valueOf(0), textStyle);
        scoreLabel.setFontScale(3f);
        scoreLabel.setX(-25f);
        scoreLabel.setColor(Color.WHITE);
        group.addActor(scoreLabel);


        stage = new Stage(new StretchViewport(FioriRace.WIDTH, FioriRace.HEIGHT));
        stage.addActor(group);


    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        long m = ((startTime + CALIBRATION_TIME_IN_MS) - TimeUtils.millis()) / 1000;
        scoreLabel.setText(String.valueOf(m));
        stage.getBatch().setProjectionMatrix(camera.combined);
        stage.getBatch().begin();
        stage.getBatch().draw(texture, 0, 0);
        stage.getBatch().end();
        rendCount++;
        if (TimeUtils.millis() > (startTime + CALIBRATION_TIME_IN_MS)) {
            activity.sendToWearable("offCalibrate", null, null);
            myGame.setScreen(this.screen);
        }

        stage.act();
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        Gdx.app.log("my Splash Screen", "show called");
        texture = new Texture(Gdx.files.internal("outrun.jpg")); //** texture is now the splash image **//
        startTime = TimeUtils.millis();
    }



    @Override
    public void hide() {
        Gdx.app.log("my Splash Screen", "hide called");
        Gdx.app.log("my Splash Screen", "rendered " + rendCount + " times.");
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
        //batch.dispose();
    }

}
