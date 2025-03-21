package game;

import Sprites.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * GameScreen sets up the Tiled map, Box2D world, collision objects, player, and camera.
 * It also handles continuous input for movement and renders all elements.
 */
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

    // Player object that contains its own Box2D dynamic body and image.
    private final Player player;

    private OrthographicCamera uiCamera;

    // Box2D world and debug renderer for physics simulation.
    private World world;
    private Box2DDebugRenderer debugRenderer;

        // --- Tiled Map Setup ---
        // Load the Tiled map from the assets folder (ensure "map.tmx" exists).
    public GameScreen(MainGame mainGame) {
        this.game = mainGame;
        // Load the TMX map from the "assets" folder.
        map = new TmxMapLoader().load("map.tmx");
        // Create the map renderer with a scale so that 1 meter = PIXELS_PER_METER pixels.
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / MainGame.PIXELS_PER_METER);

        // --- Camera and Viewport Setup ---
        // Create an orthographic camera.
        camera = new OrthographicCamera();
        // Set up a viewport with the desired size in world units.
        // Convert the desired viewport dimensions (in pixels) to world units.
        viewport = new FitViewport(
            MainGame.VIEWPORT_WIDTH / MainGame.PIXELS_PER_METER,
            MainGame.VIEWPORT_HEIGHT / MainGame.PIXELS_PER_METER,
            camera
        );
        viewport.apply();
        camera.update();

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.update();

        // --- Box2D World Setup ---
        // Create the Box2D world with zero gravity (suitable for a top-down view).
        world = new World(new Vector2(0, 0), true);
        // Initialize the debug renderer (optional – remove or comment out in production).
        debugRenderer = new Box2DDebugRenderer();

        // --- Collision Object Setup ---
        // Parse collision objects from an object layer named "Obstacles" (or "Collision") in the Tiled map.
        // Ensure your Tiled map has an object layer with rectangle objects that define obstacles.
        for (MapObject object : map.getLayers().get("Obstacles").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;
            // Position the static body at the center of the rectangle, converting pixels to world units.
            bdef.position.set(
                (rect.x + rect.width / 2) / MainGame.PIXELS_PER_METER,
                (rect.y + rect.height / 2) / MainGame.PIXELS_PER_METER
            );
            Body body = world.createBody(bdef);

            // Define the shape of the obstacle.
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(
                rect.width / 2 / MainGame.PIXELS_PER_METER,
                rect.height / 2 / MainGame.PIXELS_PER_METER
            );

            FixtureDef fdef = new FixtureDef();
            fdef.shape = shape;
            body.createFixture(fdef);
            shape.dispose();
        }

        // --- Player Setup ---
        // Create the player, passing the Box2D world so the player can create its dynamic body.
        player = new Player(world);
    }

    /**
     * Update game logic: handle input, step physics simulation, update player, and move camera.
     */
    public void update(float delta) {
        handleInput(delta);
        // Step the Box2D world simulation (fixed time step recommended).
        world.step(1/60f, 6, 2);
        // Update the player's sprite position based on its physics body.
        player.update(delta);

        // --- Camera Follow ---
        // Update the camera's position to follow the player's Box2D body.
        camera.position.set(player.getBody().getPosition(), 0);
        camera.update();
        // Inform the map renderer of the camera's updated view.
        mapRenderer.setView(camera);
    }

    /**
     * Handle continuous input by setting the player's velocity directly.
     */
    private void handleInput(float delta) {
        Body pBody = player.getBody();
        // Set a constant movement speed in world units per second.
        float moveSpeed = 2.0f;
        // Start with zero velocity.
        Vector2 velocity = new Vector2(0, 0);

        // Check which keys are held down and adjust the velocity accordingly.
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.x = moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.x = -moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            velocity.y = moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            velocity.y = -moveSpeed;
        }
        // Set the player's velocity every frame.
        pBody.setLinearVelocity(velocity);
    }

    @Override
    public void render(float delta) {
        // Update game logic.
        update(delta);

        // Clear the screen.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        // Render the Tiled map.
        mapRenderer.setView(camera);
        mapRenderer.render();

        player.update(delta);
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        player.draw(game.batch);
        game.batch.end();
        // Optionally, render the Box2D debug lines (comment out if not needed).

        uiCamera.update();
        // Set the SpriteBatch's projection matrix to the camera's combined matrix.
        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin();
        // Draw the player sprite.
        player.draw(game.batch);
        game.inventory.drawInventory(game.batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Update the viewport when the window size changes.
        viewport.update(width, height);
        uiCamera.setToOrtho(false, width, height);
        uiCamera.update();
    }

    @Override public void show() { }
    @Override public void hide() { }
    @Override public void pause() { }
    @Override public void resume() { }

    @Override
    public void dispose() {
        // Dispose all disposable resources.
        map.dispose();
        mapRenderer.dispose();
        world.dispose();
        debugRenderer.dispose();
        player.dispose();
    }
}
