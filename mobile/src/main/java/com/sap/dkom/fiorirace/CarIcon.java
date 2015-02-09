package com.sap.dkom.fiorirace;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by I072673 on 02/02/2015.
 */
public class CarIcon extends Actor {

    public CarIcon()
    {
        setWidth(120);
        setHeight(68);

        //setRotation(180);
        //setColor(Color.YELLOW);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a);
        batch.draw(Assets.car, getX(), getY(), getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, getRotation());
    }

}
