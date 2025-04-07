package game;

import Helper.FontManager;
import Sprites.PlayerHealth;
import Sprites.PlayerInventory;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
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
    public PlayerHealth health;
    private Music bgm;

    // Conversion constant: How many pixels equal one Box2D world unit.
    public static final float PIXELS_PER_METER = 70f;
    // Desired viewport dimensions in pixels (this zooms the camera to a 700×500 pixel area).
    public static final int VIEWPORT_WIDTH = 700;
    public static final int VIEWPORT_HEIGHT = 500;

    /**
     * Creates a new MainGame object.
     */
    public MainGame() {
    }

    public void startNewGame() {
        FontManager.initialize();
        // Reset inventory, health and time
        PlayerHealth.getInstance().reset();
        PlayerInventory.getInstance().reset();
        TimeManager.getInstance().reset(); // if you have this method

        if (getScreen() != null) {
            getScreen().dispose();
        }

        // Create a brand-new GameScreen
        setScreen(new GameScreen(this));
    }


    /**
     * Sets the initial screen
     */
    @Override
    public void create() {
        // Initialize the font
        FontManager.initialize();
        bgm = Gdx.audio.newMusic(Gdx.files.internal("background.mp3"));
        bgm.setLooping(true);
        bgm.setVolume(0.5f);
        bgm.play();
        this.batch = new SpriteBatch();
        inventory = PlayerInventory.getInstance();
        health = PlayerHealth.getInstance();
        batch = new SpriteBatch();
//         Set the initial screen (GameScreen uses Box2D, Tiled map, etc.)
        setScreen(new MainMenuScreen(this));
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
        health.dispose();
        if (bgm != null) {
            bgm.dispose();
        }
        // Dispose of the font when the game is over
        FontManager.dispose();
    }
}


