package game;

import Helper.FontManager;
import Sprites.Player;
import Sprites.RoyalConsort;
import Sprites.RoyalConsortType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import Sprites.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    private BitmapFont font;

    private TextureAtlas atlas;

    // Player object that contains its own Box2D dynamic body and image.
    private final Player player;

    private OrthographicCamera uiCamera;

    // Box2D world and debug renderer for physics simulation.
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private GameScreen screen;

    private TimeManager timeManager;
    private ShapeRenderer shapeRenderer;

    private int stage;
    private final List<Enemy> npcList;
    private List<Enemy> enemyRemovalQueue;
    private int enemyCount;

    // Royal Consorts
    private List<RoyalConsort> royalConsortList;
    private final List<RoyalConsort> consortRemovalQueue;

    // Fonts and Text
    private final GlyphLayout layout;
    private BitmapFont actionBoxFont;
    private final List<SystemMessage> messageQueue;
    private BitmapFont messageFont;


    // --- Tiled Map Setup ---
    // Load the Tiled map from the assets folder (ensure "map.tmx" exists).
    public GameScreen(MainGame mainGame) {
        atlas = new TextureAtlas("spritesheet/concubines.atlas");
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
//        viewport = new FitViewport(
//            MainGame.VIEWPORT_WIDTH / MainGame.PIXELS_PER_METER,
//            MainGame.VIEWPORT_HEIGHT / MainGame.PIXELS_PER_METER,
//            camera
//        );
        viewport = new ExtendViewport(
            MainGame.VIEWPORT_WIDTH / MainGame.PIXELS_PER_METER,
            MainGame.VIEWPORT_HEIGHT / MainGame.PIXELS_PER_METER,
            camera
        );
        viewport.apply();
        camera.update();

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.update();

        // --- Text ---
        layout = new GlyphLayout();;

        // --- Box2D World Setup ---
        // Create the Box2D world with zero gravity (suitable for a top-down view).
        world = new World(new Vector2(0, 0), true);
        // Initialize the debug renderer (optional – remove or comment out in production).
        debugRenderer = new Box2DDebugRenderer();
        world.setContactListener(new CollisionListener(this));

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
        player = new Player(world, this);

        this.timeManager = TimeManager.getInstance();
        this.shapeRenderer = new ShapeRenderer();

        this.stage = 1;

        royalConsortList = new ArrayList<>();
        npcList = new ArrayList<>();
        consortRemovalQueue = new ArrayList<>();
        enemyRemovalQueue = new ArrayList<>();
        this.enemyCount = 3;
        spawnEnemy();
        spawnRoyalConsorts();

        // System Message
        messageQueue = new ArrayList<>();

    }
    public void showMessage(String text, Color color) {
        messageQueue.add(new SystemMessage(text, color));
    }


    public void scheduleEnemyRemoval(Enemy enemy) {
        if (!enemyRemovalQueue.contains(enemy)) {
            enemyRemovalQueue.add(enemy);
        }
    }

    private void spawnEnemy() {
        MapObjects enemyObjects = map.getLayers().get("Enemy").getObjects();
        List<MapObject> enemyObjectsList = new ArrayList<>();
        for (MapObject object : enemyObjects.getByType(RectangleMapObject.class)) {
            enemyObjectsList.add(object);
        }
        Collections.sort(enemyObjectsList, new Comparator<MapObject>() {
            @Override
            public int compare(MapObject a, MapObject b) {
                try {
                    String nameA = (String) a.getProperties().get("name");
                    String nameB = (String) b.getProperties().get("name");
                    if(nameA == null || nameB == null) {
                        return 0;
                    }
                    String numStrA = nameA.replaceAll("[^0-9]", "");
                    String numStrB = nameB.replaceAll("[^0-9]", "");
                    int numA = numStrA.isEmpty() ? 0 : Integer.parseInt(numStrA);
                    int numB = numStrB.isEmpty() ? 0 : Integer.parseInt(numStrB);
                    return Integer.compare(numA, numB);
                } catch (Exception e) {
                    Gdx.app.error("Comparator", "Error comparing enemy names: " + e.getMessage());
                    return 0;
                }
            }
        });

        npcList.clear();
        int count = Math.min(enemyCount, enemyObjectsList.size());
        for (int i = 0; i < count; i++) {
            MapObject obj = enemyObjectsList.get(i);
            Rectangle rect = ((RectangleMapObject)obj).getRectangle();
            float centerX = (rect.x + rect.width / 2f) / MainGame.PIXELS_PER_METER;
            float centerY = (rect.y + rect.height / 2f) / MainGame.PIXELS_PER_METER;
            Vector2 spawnPos = new Vector2(centerX, centerY);
            Texture enemyTexture = new Texture("enemy/npc" + (i + 1) + ".png");
            npcList.add(new Enemy(world, this, enemyTexture, spawnPos));
        }
    }

    public void spawnRoyalConsorts() {
        MapObjects royalConsortObjects = map.getLayers().get("RoyalConsorts").getObjects();
        for (MapObject object : royalConsortObjects) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectangleObject = (RectangleMapObject) object;
                Rectangle rectangle = rectangleObject.getRectangle();
                // Get the object name (e.g., "Gwi In", "Suk Won", etc.)
                String objectName = object.getName();
                for (RoyalConsortType type : RoyalConsortType.values()) {
                    if (type.getName().equalsIgnoreCase(objectName)) {
                        // First, calculate the center of the rectangle (in pixels) and convert to world units.
                        float centerX = (rectangle.x + rectangle.width / 2f) / MainGame.PIXELS_PER_METER;
                        float centerY = (rectangle.y + rectangle.height / 2f) / MainGame.PIXELS_PER_METER;

                        // Create the texture region for the NPC using the front image file path from the enum.
                        TextureRegion region = new TextureRegion(new Texture(type.getFrontImagePath()));

                        // Calculate the sprite's size in world units (if using the default scaling similar to Enemy).
                        float spriteWidth = region.getRegionWidth() / MainGame.PIXELS_PER_METER;
                        float spriteHeight = region.getRegionHeight() / MainGame.PIXELS_PER_METER;

                        // Calculate the position so that the sprite is centered on the rectangle.
                        float finalX = centerX - spriteWidth / 2f;
                        float finalY = centerY - spriteHeight / 2f;

                        // Create the RoyalConsort instance with its type and adjusted position.
                        RoyalConsort consort = new RoyalConsort(region, type, finalX, finalY);
                        royalConsortList.add(consort);
                        break;
                    }
                }
            }
        }
    }


    public void nextStage() {
        stage++;
        enemyCount++;
        spawnEnemy();
    }

    public float getMapWidth() {
        int mapWidthInTiles = map.getProperties().get("width", Integer.class);
        int tilePixelWidth = map.getProperties().get("tilewidth", Integer.class);
        return (mapWidthInTiles * tilePixelWidth) / MainGame.PIXELS_PER_METER;
    }

    public float getMapHeight() {
        int mapHeightInTiles = map.getProperties().get("height", Integer.class);
        int tilePixelHeight = map.getProperties().get("tileheight", Integer.class);
        return (mapHeightInTiles * tilePixelHeight) / MainGame.PIXELS_PER_METER;
    }

    /**
     * Update game logic: handle input, step physics simulation, update player, and move camera.
     */
//    public void update(float delta) {
//        handleInput(delta);
//        // Step the Box2D world simulation (fixed time step recommended).
//        world.step(1/60f, 6, 2);
//        // Update the player's sprite position based on its physics body.
//        player.update(delta);
//
//        // --- Camera Follow ---
//        // Update the camera's position to follow the player's Box2D body.
//        camera.position.set(player.getBody().getPosition(), 0);
//        camera.update();
//        // Inform the map renderer of the camera's updated view.
//        mapRenderer.setView(camera);
//    }
    public void update(float delta) {
        handleInput(delta);
        // Remove expired messages
        messageQueue.removeIf(message -> message.update(delta));

        world.step(1/60f, 6, 2);
        if (!enemyRemovalQueue.isEmpty()) {
            for (Enemy enemy : enemyRemovalQueue) {
                // enemy.onPlayerCollision() 내부에서 world.destroyBody(body) 및 재생성을 수행합니다.
                enemy.onPlayerCollision();
            }
            enemyRemovalQueue.clear();
        }
        player.update(delta);

        Vector2 playerPos = player.getBody().getPosition();

        for (Enemy npc : npcList) {
            npc.update(delta, playerPos);
        }

        for (RoyalConsort consort : royalConsortList) {
            consort.updateActionBox(player.getBody().getPosition());

            if (consort.updateDeath(delta)) {
                consortRemovalQueue.add(consort);
            }
        }
        royalConsortList.removeAll(consortRemovalQueue);
        consortRemovalQueue.clear();

        // Get the player's Box2D position and try to center the camera on it
        camera.position.set(player.getBody().getPosition(), 0);

        // --- Clamp the camera position ---
        // Calculate half viewport dimensions (in world units)
        float halfViewportWidth = camera.viewportWidth * 0.5f;
        float halfViewportHeight = camera.viewportHeight * 0.5f;

        // Retrieve map dimensions from properties
        int mapWidthInTiles = map.getProperties().get("width", Integer.class);
        int mapHeightInTiles = map.getProperties().get("height", Integer.class);
        int tilePixelWidth = map.getProperties().get("tilewidth", Integer.class);
        int tilePixelHeight = map.getProperties().get("tileheight", Integer.class);
        float mapWidth = (mapWidthInTiles * tilePixelWidth) / MainGame.PIXELS_PER_METER;
        float mapHeight = (mapHeightInTiles * tilePixelHeight) / MainGame.PIXELS_PER_METER;

        // Clamp the camera's x and y values
        float camX = Math.max(halfViewportWidth, Math.min(camera.position.x, mapWidth - halfViewportWidth));
        float camY = Math.max(halfViewportHeight, Math.min(camera.position.y, mapHeight - halfViewportHeight));
        camera.position.set(camX, camY, 0);

        camera.update();
        mapRenderer.setView(camera);
        TimeManager.getInstance().update(delta);

        handlePoisonInput();
    }

    public boolean isPositionValid(float x, float y) {
        if (x < 0 || y < 0 || x > getMapWidth() || y > getMapHeight()) {
            return false;//check later
        }
        return true;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    private void handlePoisonInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            for (RoyalConsort consort : royalConsortList) {
                if (consort.shouldShowActionText() && consort.isAlive()) {
                    consort.isPoisoned(); // Mark them poisoned
                    player.setRank(consort.getType()); // inherit the new rank
                    System.out.println("Poisoned " + consort.getType().getName());
                    showMessage("Poisoned " + consort.getType().getName() + "!", Color.SCARLET);
                    break; // Only poison one at a time
                }
            }
        }
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
            player.setDirection(Direction.EAST);
            velocity.x = moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.setDirection(Direction.WEST);
            velocity.x = -moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.setDirection(Direction.NORTH);
            velocity.y = moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.setDirection(Direction.SOUTH);
            velocity.y = -moveSpeed;
        }
        // Set the player's velocity every frame.
        pBody.setLinearVelocity(velocity);
    }

    private Vector2 worldToScreenCoordinates(Vector2 worldPos) {
        Vector3 temporaryVector3 = new Vector3(worldPos.x, worldPos.y, 0);
        camera.project(temporaryVector3); // Converts from world → screen coordinates
        return new Vector2(temporaryVector3.x, temporaryVector3.y);
    }


    @Override
    public void render(float delta) {
        // Update game logic
        update(delta);

        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // --- WORLD CAMERA RENDERING ---
        camera.update();
        // Render the Tiled map
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Optionally, render the Box2D debug lines (comment out if not needed).
        // Renderer our Box2DDebugLines
        debugRenderer.render(world, camera.combined);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Enable alpha (transparency)
        game.batch.enableBlending();

        player.update(delta);
        // Draw world objects
        for (RoyalConsort royalConsort : royalConsortList) {
            royalConsort.draw(game.batch);
        }
        player.draw(game.batch);
        for (Enemy npc : npcList) {
            npc.draw(game.batch);
        }
        game.batch.end();

        // --- UI CAMERA RENDERING ---
        uiCamera.update();
        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin();

        game.inventory.drawInventory(game.batch, Gdx.graphics.getWidth());
        game.health.drawHearts(game.batch, Gdx.graphics.getHeight());


        // Action Box Poison Message
        for (RoyalConsort consort : royalConsortList) {
            if (consort.shouldShowActionText()) {
                Vector2 screenCenter = worldToScreenCoordinates(consort.getActionBoxCenterWorldPosition());

                String actionText = "Press [X] to Poison";
                layout.setText(actionBoxFont, actionText);

                // Center the text on the screen position
                float textX = screenCenter.x - layout.width / 2f;
                float textY = screenCenter.y + layout.height / 2f;

                actionBoxFont.draw(game.batch, layout, textX, textY);
            }
        }

        // Rank HUD
        String titleText = "Current Rank: " + player.getRank().getName(); // e.g., "Gwi In"
        layout.setText(font, titleText);

        float x = 20; // Left padding
        float y = Gdx.graphics.getHeight() - 140; // Top padding from screen

        font.draw(game.batch, layout, x, y);

        // System Message
        float messageY = 150; // Start position from bottom

        for (SystemMessage msg : messageQueue) {
            Color original = messageFont.getColor();
            messageFont.setColor(msg.getColor().r, msg.getColor().g, msg.getColor().b, msg.getAlpha());

            layout.setText(messageFont, msg.getText());
            float messageX = (Gdx.graphics.getWidth() - layout.width) / 2;
            messageFont.draw(game.batch, layout, messageX, messageY);

            messageY += layout.height + 10; // stack vertically
            messageFont.setColor(original); // Reset
        }

        game.batch.end();

        // Night overlay
        if (!TimeManager.getInstance().getIsDayTime()) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.setProjectionMatrix(uiCamera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.3f);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    @Override
    public void resize(int width, int height) {
        // Update the viewport when the window size changes.
        viewport.update(width, height);
        uiCamera.setToOrtho(false, width, height);
        uiCamera.update();
    }

    @Override
    public void show() {
        font = FontManager.getDefaultFont();
        font.setColor(1, 1, 1, 1); // white and opaque
        actionBoxFont = FontManager.generateFont(20, Color.valueOf("c45800"));
        messageFont = FontManager.generateFont(30, Color.WHITE);


    }
    @Override public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        // Dispose all disposable resources.
        map.dispose();
        mapRenderer.dispose();

        // Font
        FontManager.dispose();
        if (actionBoxFont != null) {
            actionBoxFont.dispose();
        }
        if (messageFont != null) {
            messageFont.dispose();
        }
        world.dispose();
        debugRenderer.dispose();
        shapeRenderer.dispose();
//        player.dispose();
    }
}
