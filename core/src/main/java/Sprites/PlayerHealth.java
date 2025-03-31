package Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlayerHealth {
    private static final int MAXHEARTS = 5;
    private float currentHearts;
    private final Texture fullHeart;
    private final Texture halfHeart;
    private final Texture emptyHeart;

    private static PlayerHealth instance;

    private PlayerHealth() {
        currentHearts = MAXHEARTS;
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
        if (currentHearts > MAXHEARTS)
            currentHearts = MAXHEARTS;
    }

    public void drawHearts(SpriteBatch batch, int screenHeight) {
        int padding = 5;
        float heartWidth = fullHeart.getWidth();
        float heartHeight = fullHeart.getHeight();
        float x = padding + 20;
        float y = screenHeight - heartHeight - padding - 20;

        for (int i = 0; i < MAXHEARTS; i++) {
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
}
