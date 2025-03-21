package game;

import Sprites.PlayerInventory;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * MainGame sets up the global SpriteBatch and game constants.
 */
public class MainGame extends Game {
    /**
     * Batch for drawing images.
     */
    public SpriteBatch batch;
    public PlayerInventory inventory;

    // Conversion constant: How many pixels equal one Box2D world unit.
    public static final float PIXELS_PER_METER = 100f;
    // Desired viewport dimensions in pixels (this zooms the camera to a 700×500 pixel area).
    public static final int VIEWPORT_WIDTH = 700;
    public static final int VIEWPORT_HEIGHT = 500;

    /**
     * Creates a new MainGame object.
     */
    public MainGame() {
    }

    /**
     * Sets the initial screen
     */
    @Override
    public void create() {
        this.batch = new SpriteBatch();
        inventory = PlayerInventory.getInstance();
        batch = new SpriteBatch();
        // Set the initial screen (GameScreen uses Box2D, Tiled map, etc.)
        setScreen(new GameScreen(this));
    }

    /**
     * Delegates the rendering of the current screen.
     */
    @Override
    public void render() {
        super.render();
    }

    /**
     * Releases all resources used by this screen.
     */
    @Override
    public void dispose() {
        if (batch != null)
            batch.dispose();
        if (getScreen() != null)
            getScreen().dispose();
        inventory.dispose();
    }
}


