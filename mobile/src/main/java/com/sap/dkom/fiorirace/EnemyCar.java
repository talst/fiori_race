package com.sap.dkom.fiorirace;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class EnemyCar extends Actor {

    private Rectangle bounds = new Rectangle();
    private TextureRegion region;

    public EnemyCar(float x, float y, float speed) {
        setWidth(160);
        setHeight(85);

        region = Assets.whitecar;
        int rnd = MathUtils.random(0, 5);
        switch (rnd) {
            case 0:
                region = Assets.convertable;
                break;
            case 1:
                region = Assets.whitecar;
                break;
            case 2:
                region = Assets.purplecar;
                break;
            case 3:
                region = Assets.orangecar;
                break;
            case 4:
                region = Assets.yellowcar;
                break;
            case 5:
                region = Assets.truck;
                setWidth(243);
                setHeight(75);
                break;
        }
        setPosition(x, y - getHeight() / 2);
        addAction(Actions.moveTo(-getWidth(), getY(), speed));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updateBounds();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(region, getX(), getY(), getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), 1, 1, getRotation());
    }

    private void updateBounds() {
        bounds.set(getX(), getY(), getWidth(), getHeight());
    }

    public void crash(boolean front, boolean above) {
        clearActions();
        addAction(Actions.fadeOut(1f));
        if (front && above)
            addAction(Actions.sequence(Actions.parallel(Actions.rotateBy(-360, 1.5f), Actions.moveBy(200, 200, 1.5f)), Actions.removeActor()));
        if (front && !above)
            addAction(Actions.sequence(Actions.parallel(Actions.rotateBy(360, 1.5f), Actions.moveBy(200, -200, 1.5f)), Actions.removeActor()));
        if (!front && above)
            addAction(Actions.sequence(Actions.parallel(Actions.rotateBy(360, 1.5f), Actions.moveBy(-200, 200, 1.5f)), Actions.removeActor()));
        if (!front && !above)
            addAction(Actions.sequence(Actions.parallel(Actions.rotateBy(-360, 1.5f), Actions.moveBy(-200, -200, 1.5f)), Actions.removeActor()));
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
