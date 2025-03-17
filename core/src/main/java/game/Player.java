package game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Ror efficiently drawing multiple images at once
import com.badlogic.gdx.math.Vector2;

public class Player {
    /*
     * Represents player's position by a 2D coordinate.
     */
    private final Vector2 position;

    /*
     * Represents player's image.
     */
    private final Texture texture;

    /**
     * Creates a new Player object.
     */
    public Player(Vector2 position) {
        this.position = position;
        this.texture = new Texture("player.png"); // put player image path like "player.png"
    }

    /**
     * Draws the player image on the screen.
     *
     * @param batch a SpriteBatch used to draw the player's texture
     */
    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    /**
     * Disposes the player's texture resource.
     */
    public void dispose() {
        this.texture.dispose();
    }
}
