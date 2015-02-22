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
    public static TextureRegion convertable;
    public static TextureRegion purplecar;
    public static TextureRegion orangecar;
    public static TextureRegion yellowcar;
    public static TextureRegion truck;

    public static void load() {
        atlas = new TextureAtlas(Gdx.files.internal("images.atlas"));
        car = atlas.findRegion("car");
        road = atlas.findRegion("road");
        heart = atlas.findRegion("heart");
        whitecar = atlas.findRegion("whitecar");
        convertable = atlas.findRegion("convertable");
        purplecar = atlas.findRegion("purplecar");
        orangecar = atlas.findRegion("orangecar");
        yellowcar = atlas.findRegion("yellowcar");
        truck = atlas.findRegion("truck");
    }

    public static void dispose() {
        atlas.dispose();
    }
}
