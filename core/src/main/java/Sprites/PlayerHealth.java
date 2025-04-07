package Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Objects;

public class PlayerHealth {
    private static final int MAX_HEARTS = 5;
    private float currentHearts;
    private final Texture fullHeart;
    private final Texture halfHeart;
    private final Texture emptyHeart;

    private static PlayerHealth instance;

    private PlayerHealth() {
        currentHearts = MAX_HEARTS;
        fullHeart = new Texture("heart/full-heart.png");
        halfHeart = new Texture("heart/half-heart.png");
        emptyHeart = new Texture("heart/empty-heart.png");
    }

    public static PlayerHealth getInstance() {
        if (instance == null) {
            instance = new PlayerHealth();
        }
        return instance;
    }

    public float getCurrentHearts() {
        return currentHearts;
    }

    public void setCurrentHearts(final float currentHearts) {
        this.currentHearts = currentHearts;
    }

    public void takeDamage(float damage) {
        this.currentHearts -= damage;
        System.out.println("damage: " + this.currentHearts);
        if (this.currentHearts <= 0) {
            this.currentHearts = 0;
        }
        if (this.currentHearts == 0) {
            gameOver();
        }
    }

    public void heal(float amount) {
        currentHearts += amount;
        if (currentHearts > MAX_HEARTS)
            currentHearts = MAX_HEARTS;
    }

    public void drawHearts(SpriteBatch batch, int screenHeight) {
        int padding = 5;
        float heartWidth = fullHeart.getWidth();
        float heartHeight = fullHeart.getHeight();
        float x = padding + 20;
        float y = screenHeight - heartHeight - padding - 20;

        for (int i = 0; i < MAX_HEARTS; i++) {
            float drawX = x + i * (heartWidth + padding);
            if (i < (int) currentHearts) {
                batch.draw(fullHeart, drawX, y);
            } else if (i < currentHearts) {
                batch.draw(halfHeart, drawX, y);
            } else {
                batch.draw(emptyHeart, drawX, y);
            }
        }
    }

    public void dispose() {
        fullHeart.dispose();
        halfHeart.dispose();
    }

    public void gameOver() {
        System.out.println("Game Over");
    }

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
        final int prime = 11;
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
