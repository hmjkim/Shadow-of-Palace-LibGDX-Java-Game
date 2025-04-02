package Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.MainGame;

public class RoyalConsort extends Sprite {

    private RoyalConsortType type;
    private boolean isAlive;

    public RoyalConsort(final TextureRegion region, final RoyalConsortType type, final float initialPositionX, final float initialPositionY) {
        super(region);
        this.type = type;
        this.isAlive = true;
        // Scale the sprite using the region's dimensions, just like in Enemy.
        setSize(region.getRegionWidth() / MainGame.PIXELS_PER_METER,
                region.getRegionHeight() / MainGame.PIXELS_PER_METER);
        setPosition(initialPositionX, initialPositionY);
    }

    public RoyalConsortType getType() {
        return type;
    }

    public void setType(final RoyalConsortType type) {
        this.type = type;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void isPoisoned() {
        isAlive = false;
    }
}
