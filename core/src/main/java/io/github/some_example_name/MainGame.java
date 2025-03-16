package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainGame extends Game {
    /**
     * Batch for drawing images.
     */
    public SpriteBatch batch;

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
        if (this.batch != null) {
            this.batch.dispose();
        }
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }
}
