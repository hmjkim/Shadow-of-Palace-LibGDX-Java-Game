package Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import game.MainGame;

public class RoyalConsort extends Sprite {

    private RoyalConsortType type;
    private boolean isAlive;

    // Shared action box image (used to prompt the player).
    private static TextureRegion actionBox;
    // Flag to determine whether the action box should be shown.
    private boolean showActionBox = false;
    // Activation distance in world units (within which the action box appears).
    private static final float ACTIVATION_DISTANCE = 0.5f;

    /**
     * Constructs a RoyalConsort with its front view image.
     *
     * @param region the consort's front view image.
     * @param type the consort's type and rank.
     * @param initialPositionX the x position in world units.
     * @param initialPositionY the y position in world units.
     */
    public RoyalConsort(final TextureRegion region, final RoyalConsortType type,
                        final float initialPositionX, final float initialPositionY) {
        super(region);
        this.type = type;
        this.isAlive = true;
        setSize(region.getRegionWidth() / MainGame.PIXELS_PER_METER,
            region.getRegionHeight() / MainGame.PIXELS_PER_METER);
        setPosition(initialPositionX, initialPositionY);
        setOriginCenter();
        // Load the actionBox once if it hasn't been loaded yet.
        if (actionBox == null) {
            actionBox = new TextureRegion(new Texture("action-box.png"));
        }
    }

    /**
     * Updates whether the action box should be shown, based on the player's position.
     *
     * @param playerPosition the player's position in world units
     */
    public void updateActionBox(Vector2 playerPosition) {
        // Calculate the position above the consort's head.
        float headX = getX() + getWidth() / 2;
        float headY = getY() + getHeight();
        // Calculate the Euclidean distance between that point and the player's position
        float distance = new Vector2(headX, headY).dst(playerPosition);
        showActionBox = (distance <= ACTIVATION_DISTANCE);
    }

    public boolean shouldShowActionText() {
        return showActionBox;
    }

    public Vector2 getActionBoxCenterWorldPosition() {
        float boxWidth = actionBox.getRegionWidth() / MainGame.PIXELS_PER_METER;
        float boxHeight = actionBox.getRegionHeight() / MainGame.PIXELS_PER_METER;

        float boxCenterX = getX() + getWidth() / 2f;
        float boxCenterY = getY() + getHeight() + 0.1f + boxHeight / 2f;

        return new Vector2(boxCenterX, boxCenterY);
    }

    public static TextureRegion getActionBoxRegion() {
        return actionBox;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch); // Draw the consort

        // If the action box should be visible, draw it above the consort
        if (showActionBox) {
            float boxWidth = actionBox.getRegionWidth() / MainGame.PIXELS_PER_METER;
            float boxHeight = actionBox.getRegionHeight() / MainGame.PIXELS_PER_METER;
            float boxX = getX() + getWidth() / 2 - boxWidth / 2;
            float boxY = getY() + getHeight() + 0.1f;
            batch.draw(actionBox, boxX, boxY, boxWidth, boxHeight);
        }
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

    /**
     * Marks the consort as eliminated via poisoning.
     */
    public void isPoisoned() {
        isAlive = false;
        setAlpha(0.5f); // semi-transparent
    }
}
