package com.sap.dkom.fiorirace;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class TrafficGame extends Table {
    public final static String NAME = "Some user for leaderboard";
    private final static boolean PLAY_MUSIC = true;
    private final static boolean PLAY_SOUNDS = true;

    public final static Color GAME_COLOR = Color.GREEN;

    public final float lane2 = 390;
    public final float lane1 = 240;
    public final float lane0 = 90;
    public PlayerCar playerCar;
    Sound dropSound;
    Music rainMusic;
    Label scoreLabel;
    Life life1, life2, life3;
    private InfiniteScrollBg backgroundRoad;
    private Array<EnemyCar> enemyCars;
    private long lastCarTime = 0;
    private int score = 0;
    private LabelGroup group;
    private float speed = 1.3f;
    private int time = 0;
    private float startTime = TimeUtils.millis();
    private Game game;
    private MainActivity activity;
    private int lives = 3;

    public TrafficGame(Game fioriRace, MainActivity activity) {
        this.game = fioriRace;
        this.activity = activity;
        setBounds(0, 0, FioriRace.WIDTH, FioriRace.HEIGHT);
        setClip(true);
        backgroundRoad = new InfiniteScrollBg(getWidth(), getHeight());
        addActor(backgroundRoad);
        playerCar = new PlayerCar(this);
        playerCar.setColor(GAME_COLOR);

        addActor(playerCar);
        enemyCars = new Array<>();
        dropSound = Gdx.audio.newSound(Gdx.files.internal("smb_fireworks.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("passingBreeze.mp3"));
        rainMusic.setLooping(true);
        if (PLAY_MUSIC) {
            rainMusic.play();
        }

        // scoring
        LabelStyle textStyle;
        BitmapFont font = new BitmapFont();
        //font.setUseIntegerPositions(false);(Optional)

        textStyle = new LabelStyle();
        textStyle.font = font;

        group = new LabelGroup();

        LabelGroup groupLives = new LabelGroup();
        life1 = new Life();
        life1.setBounds(760f, 340, life1.getWidth(), life1.getHeight());
        groupLives.addActor(life1);
        life2 = new Life();
        life2.setBounds(760f, 390, life2.getWidth(), life2.getHeight());
        groupLives.addActor(life2);
        life3 = new Life();
        life3.setBounds(760f, 440, life3.getWidth(), life3.getHeight());

        groupLives.addActor(life3);

        addActor(groupLives);

        scoreLabel = new Label("" + score, textStyle);
        scoreLabel.setFontScale(2f, 2f);
        group.addActor(scoreLabel);

        group.setBounds(760f, 50, 10, 10);
        group.addAction(Actions.rotateBy(-90));
        addActor(group);
    }

    private boolean gameEnded = false;

    @Override
    public void act(float delta) {

        super.act(delta * speed);
        //speed += 0.001;

        if (TimeUtils.nanoTime() - startTime > 1000000000f) {
            startTime = TimeUtils.nanoTime();
            time++;
        }

        drawEnemyCars();
        scoreLabel.setText(score + "");
    }

    private void drawEnemyCars() {
        if (gameEnded) {
            return;
        }

        if (TimeUtils.nanoTime() - lastCarTime > 2400000000f) {
            spawnCar();
        }

        Iterator<EnemyCar> iter = enemyCars.iterator();
        while (iter.hasNext()) {
            EnemyCar enemyCar = iter.next();
            if (enemyCar.getBounds().x + enemyCar.getWidth() <= 0) {
                iter.remove();
                removeActor(enemyCar);
                score++;
            }
            if (enemyCar.getBounds().overlaps(playerCar.getBounds())) {
                iter.remove();
                if (enemyCar.getX() > playerCar.getX()) {
                    if (enemyCar.getY() > playerCar.getY()) {
                        enemyCar.crash(true, true);
                    } else {
                        enemyCar.crash(true, false);
                    }
                } else {
                    if (enemyCar.getY() > playerCar.getY()) {
                        enemyCar.crash(false, true);
                    } else {
                        enemyCar.crash(false, false);
                    }
                }
                lives--;
                updateLives();
                if (PLAY_SOUNDS) {
                    dropSound.play();
                }
            }
        }
    }

    private void updateLives(){
        switch (lives){
            case 2:
                life1.setVisible(false);
                break;
            case 1:
                life2.setVisible(false);
                break;
            case 0:
                life3.setVisible(false);
                endGame();
                break;
        }
    }

    private void endGame() {
        gameEnded = true;
        enemyCars.clear();
        GameOverScreen gos = new GameOverScreen(this.game, this.activity, score);
        this.game.setScreen(gos);
    }

    private void spawnCar() {
        int lane = MathUtils.random(0, 2);
        float yPos = 0;
        switch (lane){
            case 0:
                yPos = lane0;
                break;
            case 1:
                yPos = lane1;
                break;
            case 2:
                yPos = lane2;
                break;
        }

        EnemyCar enemyCar = new EnemyCar(getWidth(), yPos, MathUtils.random(4.0f, 6.0f));
        enemyCars.add(enemyCar);
        addActor(enemyCar);
        lastCarTime = TimeUtils.nanoTime();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(Color.WHITE);
        super.draw(batch, parentAlpha);
    }
}
