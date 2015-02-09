package com.sap.dkom.fiorirace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
    public static TextureAtlas atlas;
    public static TextureRegion car;
    public static TextureRegion road;
    public static TextureRegion heart;
    public static TextureRegion whitecar;

    public static void load() {
        atlas = new TextureAtlas(Gdx.files.internal("images.atlas"));
        car = atlas.findRegion("car");
        road = atlas.findRegion("road");
        heart = atlas.findRegion("heart");
        whitecar = atlas.findRegion("whitecar");
    }

    public static void dispose() {
        atlas.dispose();
    }
}
