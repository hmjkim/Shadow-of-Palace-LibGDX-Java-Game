package Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Objects;

/**
 * Manages player health.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public final class PlayerHealth {
    private static final int MAX_HEARTS = 5;
    private static PlayerHealth instance;

    private float currentHearts;
    private final Texture fullHeart;
    private final Texture halfHeart;
    private final Texture emptyHeart;

    private PlayerHealth() {
        currentHearts = MAX_HEARTS;
        fullHeart = new Texture("heart/full-heart.png");
        halfHeart = new Texture("heart/half-heart.png");
        emptyHeart = new Texture("heart/empty-heart.png");
    }

    /**
     * Returns the singleton instance.
     *
     * @return the PlayerHealth instance
     */
    public static PlayerHealth getInstance() {
        if (instance == null) {
            instance = new PlayerHealth();
        }
        return instance;
    }

    /**
     * Returns the current heart value.
     *
     * @return the current hearts
     */
    public float getCurrentHearts() {
        return currentHearts;
    }

    /**
     * Sets the current heart value.
     *
     * @param currentHearts the new heart value
     */
    public void setCurrentHearts(final float currentHearts) {
        this.currentHearts = currentHearts;
    }

    /**
     * Applies damage to the player.
     *
     * @param damage the damage amount
     */
    public void takeDamage(final float damage) {
        this.currentHearts -= damage;
        System.out.println("damage: " + this.currentHearts);
        if (this.currentHearts <= 0) {
            this.currentHearts = 0;
        }
        if (this.currentHearts == 0) {
            gameOver();
        }
    }

    /**
     * Heals the player.
     *
     * @param amount the healing amount
     */
    public void heal(final float amount) {
        currentHearts += amount;
        if (currentHearts > MAX_HEARTS) {
            currentHearts = MAX_HEARTS;
        }
    }

    /**
     * Draws the heart icons on the screen.
     *
     * @param batch the SpriteBatch for rendering
     * @param screenHeight the height of the screen in pixels
     */
    public void drawHearts(final SpriteBatch batch, final int screenHeight) {
        final int padding = 5;
        float heartWidth = fullHeart.getWidth();
        float heartHeight = fullHeart.getHeight();
        final int offset = 20;
        float x = padding + offset;
        float y = screenHeight - heartHeight - padding - offset;

        for (int i = 0; i < MAX_HEARTS; i++) {
            float drawX = x + i * (heartWidth + padding);
            // Draw a full heart if within the integer part of currentHearts.
            if (i < (int) currentHearts) {
                batch.draw(fullHeart, drawX, y);
            } else if (i < currentHearts) {
                // Draw a half heart if the slot is less than currentHearts (but not full).
                batch.draw(halfHeart, drawX, y);
            } else {
                // Draw an empty heart.
                batch.draw(emptyHeart, drawX, y);
            }
        }
    }

    /**
     * Disposes of heart textures.
     */
    public void dispose() {
        fullHeart.dispose();
        halfHeart.dispose();
    }

    /**
     * Triggers game over.
     */
    public void gameOver() {
        System.out.println("Game Over");
    }

    /**
     * Resets the player's health.
     */
    public void reset() {
        currentHearts = MAX_HEARTS;
    }

    /**
     * Determines if the PlayerHealth is equal to another object.
     *
     * @param obj the object to compare
     * @return true if the PlayerHealth is equal to the other object, false otherwise
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

        PlayerHealth playerHealth = (PlayerHealth) obj;
        return Objects.equals(this.currentHearts, playerHealth.currentHearts)
            && Objects.equals(this.fullHeart, playerHealth.fullHeart)
            && Objects.equals(this.halfHeart, playerHealth.halfHeart)
            && Objects.equals(this.emptyHeart, playerHealth.emptyHeart);
    }

    /**
     * Gets the hashcode of the PlayerHealth.
     *
     * @return the hashcode of the PlayerHealth as an integer
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(this.currentHearts);
        final int prime = 15;
        result = prime * result + Objects.hashCode(this.fullHeart);
        result = prime * result + Objects.hashCode(this.halfHeart);
        result = prime * result + Objects.hashCode(this.emptyHeart);
        return result;
    }

    /**
     * Gets the string representation of the PlayerHealth.
     *
     * @return the string representation of the PlayerHealth
     */
    @Override
    public String toString() {
        final StringBuilder sb;
        sb = new StringBuilder("PlayerHealth {");
        sb.append("currentHearts='").append(this.currentHearts).append('\'');
        sb.append("fullHeart='").append(this.fullHeart).append('\'');
        sb.append("halfHeart='").append(this.halfHeart).append('\'');
        sb.append("emptyHeart='").append(this.emptyHeart);
        sb.append('}');
        return sb.toString();
    }
}
