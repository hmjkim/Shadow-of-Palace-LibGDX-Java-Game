package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameScreen implements Screen {
    /*
     * Manages overall game state.
     */
    private final MainGame game;

    /*
     * Controls the display area on the screen.
     */
    private final OrthographicCamera camera;

    /*
     * Adjusts the camera's view to fit various screen sizes.
     */
    private final Viewport viewport;

    /*
     * Contains the Tiled map data.
     */
    private final TiledMap map;

    /*
     * Draws the loaded map to the actual screen.
     */
    private final OrthogonalTiledMapRenderer mapRenderer;

    /*
     * Manages player.
     */
    private Player player;

    /**
     * Creates a new GameScreen object.
     */
    public GameScreen(MainGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(800, 600, this.camera);
        this.map = new TmxMapLoader().load("game_map.tmx"); /*put map file name (path) like "maps/game_map.tmx"*/
        this.mapRenderer = new OrthogonalTiledMapRenderer(map);
        initializePlayer();
    }

    /**
     * Initializes the player by setting its starting position from the tiled map.
     */
    private void initializePlayer() {
        MapObjects objects = map.getLayers().get("Objects").getObjects();
        /* searches the "Objects" layer of the loaded TiledMap for an object named "PlayerStart" */
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject && object.getName().equals("PlayerStart")) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                player = new Player(new Vector2(rectangle.x, rectangle.y));
                // presents a 2D coordinate, constructed from the x and y values of the rectangle obtained
                // from the Tiled map's "PlayerStart" object
                break;
            }
        }
        /* If the "PlayerStart" object is not found, no player is initialized */
    }

    /**
     * Renders the game screen.
     *
     * @param delta a float representing the time in seconds since the last render call
     */
    @Override
    public void render(float delta) {
        // clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();
        // rendering the map after setting the camera to current view
        mapRenderer.setView(this.camera);
        mapRenderer.render();

        // rendering the player
        game.batch.begin();
        player.draw(game.batch);
        game.batch.end();
    }

    /**
     * Adjusts the viewport dimensions when the game window is resized.
     *
     * @param width an int representing the new width of the screen
     * @param height an int representing the new height of the screen
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    //There are inside Screen Interface, fill those things later
    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    /**
     * Releases all resources used by this screen.
     */
    @Override
    public void dispose() {
        // it ensures that all disposable assets such as the TiledMap and its renderer
        // are properly released to prevent memory leaks.
        map.dispose();
        mapRenderer.dispose();
    }
}
