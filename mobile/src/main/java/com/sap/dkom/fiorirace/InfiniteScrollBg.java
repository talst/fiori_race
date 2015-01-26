package com.sap.dkom.fiorirace;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;


public class InfiniteScrollBg extends Actor {
    public final float speed = 1.0f;
    public RepeatAction moveAction;

    public InfiniteScrollBg(float width, float height) {
        setWidth(width);
        setHeight(height);
        setPosition(width, 0);
        moveAction = forever(sequence(moveTo(0, 0, speed), moveTo(width, 0)));
        addAction(moveAction);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(Assets.road, getX() - getWidth(), getY(), getWidth() * 2, getHeight());
    }
}
