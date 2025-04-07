package Sprites;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import game.MainGame;
import game.TimeManager;

import java.util.Objects;

public class RoyalConsort extends Sprite {

    private RoyalConsortType type;
    private boolean isAlive;

    // Shared action box image (used to prompt the player).
    private static TextureRegion actionBox;
    // Flag to determine whether the action box should be shown.
    private boolean showActionBox;
    // Activation distance in world units (within which the action box appears).
    private static final float ACTIVATION_DISTANCE = 0.5f;

    // For Death Fadeout Animation
    private boolean isDying;
    private float deathTimer;
    private static final float DEATH_DURATION = 1.5f; // seconds

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
        this.showActionBox = false;
        this.isDying = false;
        this.deathTimer = 0f;
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
        return showActionBox && !isDying;

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

    public RoyalConsortType getType() {
        return type;
    }

    public void setType(final RoyalConsortType type) {
        this.type = type;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean updateDeath(float delta) {
        if (isDying) {
            deathTimer += delta;
            return deathTimer >= DEATH_DURATION;
        }
        return false;
    }

    /**
     * Marks the consort as eliminated via poisoning.
     */
    public void isPoisoned() {
        isAlive = false;
        isDying = true;
        deathTimer = 0;
    }

    @Override
    public void draw(Batch batch) {
        float alpha = 1f;

        if (isDying) {
            alpha = 1f - (deathTimer / DEATH_DURATION);
            if (alpha < 0f) alpha = 0f;
        }

        Color originalColor = batch.getColor();
        setAlpha(alpha);

        // 👻 Offset to make her float upward while fading
        float riseOffset = isDying ? (deathTimer / DEATH_DURATION) * 0.5f : 0f;

        // Apply vertical offset
        float originalY = getY();
        setY(originalY + riseOffset);

        super.draw(batch);

        // Reset Y back to prevent position drift
        setY(originalY);

        boolean isNight = !TimeManager.getInstance().getIsDayTime();
        if (showActionBox && !isDying && isNight) {
            float boxWidth = actionBox.getRegionWidth() / MainGame.PIXELS_PER_METER;
            float boxHeight = actionBox.getRegionHeight() / MainGame.PIXELS_PER_METER;
            float boxX = getX() + getWidth() / 2 - boxWidth / 2;
            float boxY = getY() + getHeight() + 0.1f;
            batch.draw(actionBox, boxX, boxY, boxWidth, boxHeight);
        }

        setColor(originalColor);
    }

    /**
     * Determines if the RoyalConsort is equal to another object.
     *
     * @param obj the object to compare
     * @return true if the RoyalConsort is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj.getClass() == this.getClass())) {
            return false;
        }

        RoyalConsort royalConsort = (RoyalConsort) obj;
        return super.equals(obj) && Objects.equals(this.type, royalConsort.type)
            && Objects.equals(this.isAlive, royalConsort.isAlive)
            && Objects.equals(this.showActionBox, royalConsort.showActionBox)
            && Objects.equals(this.isDying, royalConsort.isDying)
            && Objects.equals(this.deathTimer, royalConsort.deathTimer);
    }

    /**
     * Gets the hashcode of the RoyalConsort.
     *
     * @return the hashcode of the RoyalConsort as an integer
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(this.type);
        final int prime = 17;
        result = prime * result + Objects.hashCode(this.isAlive);
        result = prime * result + Objects.hashCode(this.showActionBox);
        result = prime * result + Objects.hashCode(this.isDying);
        result = prime * result + Objects.hashCode(this.deathTimer);
        result = prime * result + super.hashCode();
        return result;
    }

    /**
     * Gets the string representation of the RoyalConsort.
     *
     * @return the string representation of the RoyalConsort
     */
    @Override
    public String toString() {
        final StringBuilder sb;
        sb = new StringBuilder("RoyalConsort {");
        sb.append("type='").append(this.type).append('\'');
        sb.append("isAlive='").append(this.isAlive).append('\'');
        sb.append("showActionBox='").append(this.showActionBox).append('\'');
        sb.append("isDying='").append(this.isDying).append('\'');
        sb.append("deathTimer='").append(this.deathTimer);
        sb.append('}');
        return super.toString() + sb;
    }
}
