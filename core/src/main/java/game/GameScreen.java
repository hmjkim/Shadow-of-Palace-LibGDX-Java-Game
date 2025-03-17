package game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Vector2;

public class GameScreen implements Screen {
    // Contains the Tiled map data.
    private final TiledMap map;
    // Renders the loaded map to the actual screen.
    private final OrthogonalTiledMapRenderer mapRenderer;
    // Controls the display area on the screen.
    private final OrthographicCamera camera;
    // Adjusts the camera's view to fit various screen sizes.
    private final Viewport viewport;
    // Reference to the main game to access its SpriteBatch.
    private final MainGame game;
    // The player object.
    private final Player player;

    public GameScreen(MainGame mainGame) {
        this.game = mainGame;
        // Load the TMX map from the assets folder.
        map = new TmxMapLoader().load("game_map.tmx");
        // Create a renderer for the loaded map.
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        // Set up the camera and viewport with a virtual size of 800x600.
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camera);
        // Set the camera to an orthographic projection with the specified viewport dimensions.
        camera.setToOrtho(false, 800, 600);

        // Initialize the player at a default position (for example, at x=100, y=100).
        // Make sure you have a "player.png" in your assets folder or adjust accordingly.
        player = new Player(new Vector2(100, 100));
    }

    @Override
    public void render(float delta) {
        // Clear the screen with an opaque black color.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the camera and set the renderer's view.
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Update player's position
        player.update(delta);

        // Draw the player image.
        // Begin the SpriteBatch, draw the player, then end the batch.
        game.batch.begin();
        player.draw(game.batch);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Update the viewport dimensions when the window is resized.
        viewport.update(width, height);
    }

    @Override public void show() {
        // Called when this screen becomes the current screen.
    }

    @Override public void hide() {
        // Called when this screen is no longer the current screen.
    }

    @Override public void pause() {
        // Called when the application is paused.
    }

    @Override public void resume() {
        // Called when the application is resumed after being paused.
    }

    @Override
    public void dispose() {
        // Dispose of the map and renderer to free up resources and prevent memory leaks.
        map.dispose();
        mapRenderer.dispose();
        // Dispose the player's texture.
        player.dispose();
    }
}
