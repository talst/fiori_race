package com.sap.dkom.fiorirace;

import com.badlogic.gdx.Game;

public class FioriRace extends Game {
    public final static int WIDTH = 800;
    public final static int HEIGHT = 480;
    private GameScreen gameScreen;
    private SplashScreen splash;
    private MainActivity activity;


    @Override
    public void create() {
        Assets.load();
        gameScreen = new GameScreen(this, this.activity, null);
        splash = new SplashScreen(this, gameScreen, this.activity);
        setScreen(splash);
    }

    @Override
    public void dispose() {
        Assets.dispose();
        splash.dispose();
        gameScreen.dispose();
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

}
