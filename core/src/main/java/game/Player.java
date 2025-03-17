package game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Ror efficiently drawing multiple images at once
import com.badlogic.gdx.math.Vector2;

import java.nio.file.LinkPermission;

public class Player {
    /*
     * Represents player's position by a 2D coordinate.
     */
    private final Vector2 position;

    private static final float speed = 200f;
    /*
     * Represents player's image.
     */
    private final Texture texture;

    /**
     * Creates a new Player object.
     *
     * @param position a vector2 representing player's position
     */
    public Player(Vector2 position) {
        this.position = position;
        this.texture = new Texture("player.png"); // put player image path like "player.png"
    }

    /**
     * Updates player's position.
     *
     * @param delta a float representing elapsed time since last frame
     */
    public void update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            position.x -= speed * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            position.x += speed * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            position.y += speed * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            position.y -= speed * delta;
        }
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
