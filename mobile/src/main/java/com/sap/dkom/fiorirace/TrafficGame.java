package com.sap.dkom.fiorirace;

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
    private final static boolean PLAY_MUSIC = false;
    private final static boolean PLAY_SOUNDS = true;
    public final float lane2 = 390;
    public final float lane1 = 240;
    public final float lane0 = 90;
    public PlayerCar playerCar;
    Sound dropSound;
    Music rainMusic;
    Label label;
    Label label2;
    private InfiniteScrollBg backgroundRoad;
    private Array<EnemyCar> enemyCars;
    private long lastCarTime = 0;
    private int score = 0;
    private LabelGroup group;
    private float speed = 1.0f;
    private int time = 0;
    private float startTime = TimeUtils.millis();

    public TrafficGame() {
        setBounds(0, 0, FioriRace.WIDTH, FioriRace.HEIGHT);
        setClip(true);
        backgroundRoad = new InfiniteScrollBg(getWidth(), getHeight());
        addActor(backgroundRoad);
        playerCar = new PlayerCar(this);
        addActor(playerCar);
        enemyCars = new Array<>();
        dropSound = Gdx.audio.newSound(Gdx.files.internal("smb_fireworks.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("smb_fireworks.wav"));
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
        label = new Label("Score: " + score, textStyle);
        label.setBounds(10f, 10f, 1, 2);
        label.setFontScale(1f, 1f);
        group.addActor(label);

        label2 = new Label("HeartRate: " + "6", textStyle);
        label2.setBounds(100f, 10f, 100f, 2);
        label2.setFontScale(1f, 1f);
        group.addActor(label2);

        group.setBounds(780f, 480, 200, 100f);
        group.addAction(Actions.rotateBy(-90));
        addActor(group);
    }

    @Override
    public void act(float delta) {

        super.act(delta * speed);
        speed += 0.001;

        if (TimeUtils.nanoTime() - lastCarTime > 3000000000f) spawnCar();

        if (TimeUtils.nanoTime() - startTime > 1000000000f) {
            startTime = TimeUtils.nanoTime();
            time++;
        }

        Iterator<EnemyCar> iter = enemyCars.iterator();
        while (iter.hasNext()) {
            EnemyCar enemyCar = iter.next();
            if (enemyCar.getBounds().x + enemyCar.getWidth() <= 0) {
                iter.remove();
                removeActor(enemyCar);
            }
            if (enemyCar.getBounds().overlaps(playerCar.getBounds())) {
                iter.remove();
                if (enemyCar.getX() > playerCar.getX()) {
                    if (enemyCar.getY() > playerCar.getY()) {
                        enemyCar.crash(true, true);
                    } else {
                        enemyCar.crash(true, false);
                    }
                    score++;
                } else {
                    if (enemyCar.getY() > playerCar.getY()) {
                        enemyCar.crash(false, true);
                    } else {
                        enemyCar.crash(false, false);
                    }
                    score++;
                }
                if (PLAY_SOUNDS) {
                    dropSound.play();
                }
            }
        }

        label.setText("Score: " + score + "      Time: " + time);
    }

    private void spawnCar() {
        int lane = MathUtils.random(0, 2);
        float yPos = 0;
        if (lane == 0) yPos = lane0;
        if (lane == 1) yPos = lane1;
        if (lane == 2) yPos = lane2;
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
