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
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * Created by I040335 on 02/02/2015.
 */
public class GameOverScreen implements Screen, GestureListener {

    private Stage stage;
    private SpriteBatch batch;
    private Game myGame;
    private Texture texture;
    private OrthographicCamera camera;
    private long startTime;
    private int rendCount;
    private GameScreen screen;
    private MainActivity activity;
    private int score;
    Label scoreLabel;
    Group group;

    public GameOverScreen(Game g, MainActivity activity, int score) // ** constructor called initially **//
    {
        this.score = score;
        this.activity = activity;
        this.screen = screen;

        myGame = g; // ** get Game parameter **//
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);
        batch = new SpriteBatch();

        Label.LabelStyle textStyle = new Label.LabelStyle();
        BitmapFont font = new BitmapFont();
        textStyle.font = font;

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
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new GestureDetector(this));
        texture = new Texture(Gdx.files.internal("gameover.jpg")); //** texture is now the splash image **//

        startTime = TimeUtils.millis();
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
