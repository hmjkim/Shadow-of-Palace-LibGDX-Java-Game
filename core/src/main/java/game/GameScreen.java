package game;

import Helper.FontManager;
import Ingredients.IngredientManager;
import Manager.GameSaveManager;
import Manager.GameState;
import Sprites.Player;
import Sprites.PlayerInventory;
import Sprites.PlayerHealth;
import Sprites.RoyalConsort;
import Sprites.RoyalConsortType;
import Sprites.Direction;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import Sprites.FrameRegionDimensions;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.Objects;

/**
 * Initializes GameScreen.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public class GameScreen implements Screen {
    private static final int INGREDIENT_COUNT = 30;
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

    private final TextureAtlas atlas;
    private final TextureAtlas enemyAtlas;

    // Player object that contains its own Box2D dynamic body and image.
    private final Player player;

    private final OrthographicCamera uiCamera;

    // Box2D world and debug renderer for physics simulation.
    private final World world;
    private final Box2DDebugRenderer debugRenderer;

    private final ShapeRenderer shapeRenderer;

    private int stage;
    private final List<Enemy> npcList;
    private final List<Enemy> enemyRemovalQueue;
    private int enemyCount;

    // Royal Consorts
    private final List<RoyalConsort> royalConsortList;
    private final List<RoyalConsort> consortRemovalQueue;

    // Kitchen
    private Kitchen kitchen;
    private Rectangle kitchenZone;

    // Fonts and Text
    private final GlyphLayout layout;
    private BitmapFont actionBoxFont;
    private final List<SystemMessage> messageQueue;
    private BitmapFont messageFont;

    private final IngredientManager ingredientManager;

    //Target Royal Consort
    private final List<RoyalConsortType> killOrder;
    private int currentTargetIndex;
    private final Map<RoyalConsortType, Texture> targetIcons;

    // Pause
    private boolean isPaused;
    private final PauseMenu pauseMenu;
    private final Stage mainStage;

    /**
     * Constructs a GameScreen with the specified mainGame.
     *
     * @param mainGame the MainGame instance
     */
    // --- Tiled Map Setup ---
    // Load the Tiled map from the assets folder (ensure "map.tmx" exists).
    public GameScreen(final MainGame mainGame) {
        atlas = new TextureAtlas("royalconsorts/royalconsorts.atlas");
        enemyAtlas = new TextureAtlas("enemy/enemies.atlas");
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
        layout = new GlyphLayout();

        // --- Box2D World Setup ---
        // Create the Box2D world with zero gravity (suitable for a top-down view).
        world = new World(new Vector2(0, 0), true);
        // Initialize the debug renderer (optional – remove or comment out in production).
        debugRenderer = new Box2DDebugRenderer();
        world.setContactListener(new CollisionListener(this));

        // --- Collision Object Setup ---
        // Parse collision objects from an object layer named
        // "Obstacles" (or "Collision") in the Tiled map.
        // Ensure your Tiled map has an object layer with rectangle objects that define obstacles.
        for (RectangleMapObject object
            : map.getLayers().get("Obstacles").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            // Position the static body at the center of the rectangle,
            // converting pixels to world units.
            bodyDef.position.set(
                (rect.x + rect.width / 2) / MainGame.PIXELS_PER_METER,
                (rect.y + rect.height / 2) / MainGame.PIXELS_PER_METER
            );
            Body body = world.createBody(bodyDef);

            // Define the shape of the obstacle.
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(
                rect.width / 2 / MainGame.PIXELS_PER_METER,
                rect.height / 2 / MainGame.PIXELS_PER_METER
            );

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
            shape.dispose();
        }

        // --- Player Setup ---
        // Create the player, passing the Box2D world so the player can create its dynamic body.
        player = new Player(world, this);

        this.shapeRenderer = new ShapeRenderer();

        this.stage = 1;
        final int startEnemyNumber = 11;

        royalConsortList = new ArrayList<>();
        npcList = new ArrayList<>();
        consortRemovalQueue = new ArrayList<>();
        enemyRemovalQueue = new ArrayList<>();
        this.enemyCount = startEnemyNumber;
        spawnEnemy();
        spawnRoyalConsorts();
        spawnPlayerBasedOnCurrentRank();

        markKitchen();

        // System Message
        messageQueue = new ArrayList<>();

        // Ingredients
        // Ingredients
        List<Vector2> walkablePositions = getWalkablePositions();
        ingredientManager = new IngredientManager(walkablePositions);

        this.currentTargetIndex = 0;
        killOrder = new ArrayList<>();
        killOrder.add(RoyalConsortType.SUK_WON);
        killOrder.add(RoyalConsortType.SUK_BIN);
        killOrder.add(RoyalConsortType.BIN);
        killOrder.add(RoyalConsortType.QUEEN);

        targetIcons = new HashMap<>();
        targetIcons.put(RoyalConsortType.SUK_WON, new Texture("target/target-sukwon.png"));
        targetIcons.put(RoyalConsortType.SUK_BIN, new Texture("target/target-sukbin.png"));
        targetIcons.put(RoyalConsortType.BIN, new Texture("target/target-bin.png"));
        targetIcons.put(RoyalConsortType.QUEEN, new Texture("target/target-queen.png"));

        // Spawn ingredients spread over the walkable area
        ingredientManager.spawnIngredients(INGREDIENT_COUNT);

        if (kitchenZone != null) {
            kitchen = new Kitchen(kitchenZone, this);
        }

        // Pause
        isPaused = false;
        mainStage = new Stage(viewport);
        pauseMenu = new PauseMenu(new PauseMenu.PauseScreenListener() {
            @Override
            public void onResume() {
                isPaused = false;
                pauseMenu.setVisible(false);
                Gdx.input.setInputProcessor(mainStage);
            }

            @Override
            public void onRestart() {
                game.startNewGame();
            }

            @Override
            public void onSave() {
                saveGameState();
            }

            @Override
            public void onExit() {
                Gdx.app.exit();
            }
        });
        pauseMenu.setVisible(false);
    }

    /**
     * Constructs a GameScreen with the specified mainGame, loadedState.
     *
     * @param mainGame the MainGame instance
     * @param loadedState the loaded GameState
     */
    public GameScreen(final MainGame mainGame, final GameState loadedState) {
        this(mainGame);  // Call the default constructor to do all initialization
        if (loadedState != null) {
            applyGameState(loadedState);
        }
    }

    /**
     * Displays a system message.
     *
     * @param text the message text
     * @param color the message color
     */
    public void showMessage(final String text, final Color color) {
        messageQueue.add(new SystemMessage(text, color));
    }

    private RoyalConsortType getCurrentTarget() {
        if (currentTargetIndex < killOrder.size()) {
            return killOrder.get(currentTargetIndex);
        }
        return null;
    }

    private void markKitchen() {
        if (map.getLayers().get("kitchen") != null) {
            MapObjects kitchenObjects = map.getLayers().get("kitchen").getObjects();
            if (!kitchenObjects.getByType(RectangleMapObject.class).isEmpty()) {
                RectangleMapObject kitchenObj = kitchenObjects.getByType(RectangleMapObject.class).first();
                Rectangle rect = kitchenObj.getRectangle();
                kitchenZone = new Rectangle(
                    rect.x / MainGame.PIXELS_PER_METER,
                    rect.y / MainGame.PIXELS_PER_METER,
                    rect.width / MainGame.PIXELS_PER_METER,
                    rect.height / MainGame.PIXELS_PER_METER
                );
            }
        }
    }

    private void handleItemUsageInput() {
        final float invincibleTime = 20f;
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            if (PlayerInventory.getInstance().getInvincibilityItemCount() >= 1) {
                PlayerInventory.getInstance().removeInvincibilityItem();
                player.setInvincible(invincibleTime);
                System.out.println("Invincibility item used");
            } else {
                System.out.println("No invincibility item available");
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            if (PlayerInventory.getInstance().getHeartRecoveryItemCount() >= 1) {
                PlayerInventory.getInstance().removeHeartRecoveryItem();
                PlayerHealth.getInstance().heal(1);
                System.out.println("HeartRecovery item used");
            } else {
                System.out.println("No HeartRecovery item available");
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            Vector2 playerPos = player.getBody().getPosition();
            boolean isInsideKitchen = kitchen.contains(playerPos.x, playerPos.y);
            if (isInsideKitchen && !TimeManager.getInstance().getIsDayTime()) {
                kitchen.handleCraft();
            }
        }
    }

    /**
     * Schedules an enemy for removal.
     *
     * @param enemy the enemy to remove
     */
    public void scheduleEnemyRemoval(final Enemy enemy) {
        if (!enemyRemovalQueue.contains(enemy)) {
            enemyRemovalQueue.add(enemy);
        }
    }

    /**
     * Spawns enemies from the Tiled map.
     */
    private void spawnEnemy() {
        MapObjects enemyObjects = map.getLayers().get("Enemy").getObjects();
        List<MapObject> enemyObjectsList = new ArrayList<>();
        for (MapObject object : enemyObjects.getByType(RectangleMapObject.class)) {
            enemyObjectsList.add(object);
        }
        enemyObjectsList.sort((a, b) -> {
            String nameA = (String) a.getProperties().get("name");
            String nameB = (String) b.getProperties().get("name");
            if (nameA == null || nameB == null) {
                return 0;
            }
            String numStrA = nameA.replaceAll("[^0-9]", "");
            String numStrB = nameB.replaceAll("[^0-9]", "");
            int numA = numStrA.isEmpty() ? 0 : Integer.parseInt(numStrA);
            int numB = numStrB.isEmpty() ? 0 : Integer.parseInt(numStrB);
            return Integer.compare(numA, numB);
        });

        npcList.clear();
        int count = Math.min(enemyCount, enemyObjectsList.size());
        for (int i = 0; i < count; i++) {
            MapObject obj = enemyObjectsList.get(i);
            Rectangle rect = ((RectangleMapObject) obj).getRectangle();
            float centerX = (rect.x + rect.width / 2f) / MainGame.PIXELS_PER_METER;
            float centerY = (rect.y + rect.height / 2f) / MainGame.PIXELS_PER_METER;
            Vector2 spawnPos = new Vector2(centerX, centerY);

            final int enemyTotalTypes = 4;
            Random random = new Random();
            String enemyRegionName = "enemy" + (random.nextInt(enemyTotalTypes) + 1);
            System.out.println(enemyRegionName);
            FrameRegionDimensions dimensions;
            final int case1Width = 20;
            final int case1Height = 30;
            final int case2Width = 17;
            final int case2Height = 30;
            final int case3Width = 21;
            final int case3Height = 31;
            final int case4Width = 24;
            final int case4Height = 32;
            final int defaultWidth = 20;
            final int defaultHeight = 30;
            switch (enemyRegionName) {
                case "enemy1":
                    dimensions = new FrameRegionDimensions(case1Width, case1Height);
                    break;
                case "enemy2":
                    dimensions = new FrameRegionDimensions(case2Width, case2Height);
                    break;
                case "enemy3":
                    dimensions = new FrameRegionDimensions(case3Width, case3Height);
                    break;
                case "enemy4":
                    dimensions = new FrameRegionDimensions(case4Width, case4Height);
                    break;
                default:
                    // Provide a default value
                    dimensions = new FrameRegionDimensions(defaultWidth, defaultHeight);
                    break;
            }

            npcList.add(new Enemy(world, enemyAtlas, spawnPos, enemyRegionName, dimensions));
        }
    }

    /**
     * Spawns royal consorts from the Tiled map.
     */
    public void spawnRoyalConsorts() {
        MapObjects royalConsortObjects = map.getLayers().get("RoyalConsorts").getObjects();
        for (MapObject object : royalConsortObjects) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectangleObject = (RectangleMapObject) object;
                Rectangle rectangle = rectangleObject.getRectangle();
                // Get the object name (e.g., "Gwi In", "Suk Won", etc.)
                String objectName = object.getName();
                for (RoyalConsortType consortType : RoyalConsortType.values()) {
                    if (consortType.getName().equalsIgnoreCase(objectName)) {
                        // Check if this consort type is less than
                        // or equal to the player's current rank
                        // If so, skip spawning it
                        // because that rank is already occupied or "dead"
                        if (consortType.compareTo(player.getRank()) <= 0) {
                            // Skip this consort
                            break;
                        }
                        // Calculate the center of the rectangle (in pixels)
                        // and convert to world units.
                        RoyalConsort consort = getRoyalConsort(consortType, rectangle);
                        royalConsortList.add(consort);
                        break;
                    }
                }
            }
        }
    }

    private static RoyalConsort getRoyalConsort(RoyalConsortType consortType, Rectangle rectangle) {
        float centerX = (rectangle.x + rectangle.width / 2f)
            / MainGame.PIXELS_PER_METER;
        float centerY = (rectangle.y + rectangle.height / 2f)
            / MainGame.PIXELS_PER_METER;

        // Create the texture region for the NPC
        // using the front image file path from the enum.
        TextureRegion region
            = new TextureRegion(new Texture(consortType.getFrontImagePath()));

        // Calculate the sprite's size in world units
        float spriteWidth = region.getRegionWidth() / MainGame.PIXELS_PER_METER;
        float spriteHeight = region.getRegionHeight() / MainGame.PIXELS_PER_METER;

        // Calculate the position so that the sprite is centered
        // on the rectangle
        float finalX = centerX - spriteWidth / 2f;
        float finalY = centerY - spriteHeight / 2f;

        // Create the RoyalConsort instance with its type
        // and adjusted position
        return new RoyalConsort(region, consortType, finalX, finalY);
    }

    /**
     * Returns the list of walkable positions.
     *
     * @return a list of Vector2 positions for walkable tiles
     */
    public List<Vector2> getWalkablePositions() {
        List<Vector2> walkablePositions = new ArrayList<>();

        // Access the "Walkable" object layer in Tiled map
        MapObjects walkableObjects = map.getLayers().get("Walkable").getObjects();
        for (RectangleMapObject object : walkableObjects.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = object.getRectangle();
            // Calculate the center position of the rectangle and convert to world units
            float centerX = (rectangle.x + rectangle.width / 2f) / MainGame.PIXELS_PER_METER;
            float centerY = (rectangle.y + rectangle.height / 2f) / MainGame.PIXELS_PER_METER;
            walkablePositions.add(new Vector2(centerX, centerY));
        }
        return walkablePositions;
    }

    /**
     * Advances the game to the next stage.
     */
    public void nextStage() {
        this.stage++;
        enemyCount++;
        spawnEnemy();
    }

    /**
     * Updates the game logic.
     *
     * @param delta the time elapsed since the last frame
     */
    public void update(final float delta) {
        handleInput();

        // if state is paused, don't proceed with update
        if (isPaused) {
            return;
        }

        // Check if the player's health has reached 0 (game over condition)
        if (PlayerHealth.getInstance().getCurrentHearts() <= 0) {
            game.setScreen(new EndScreen(game, "Game Over", Color.RED));
            return; // Skip further update logic
        }

        // Remove expired messages
        messageQueue.removeIf(message -> message.update(delta));

        final float divideNum = 60f;
        final int velocityIterations = 6;
        world.step(1 / divideNum, velocityIterations, 2);
        if (!enemyRemovalQueue.isEmpty()) {
            for (Enemy enemy : enemyRemovalQueue) {
                enemy.onPlayerCollision();
            }
            enemyRemovalQueue.clear();
        }
        player.update(delta);

        Vector2 playerPos = player.getBody().getPosition();

        // Collect a nearby ingredient when SPACE is pressed
        final float threshold = 0.3f;
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            ingredientManager.collectNearbyIngredient(playerPos,
                threshold, PlayerInventory.getInstance());
        }

        // If less than 70% of the ingredients remain, replenish to the original amount.
        final float checkPercent = 0.7f;
        if (ingredientManager.getIngredients().size < INGREDIENT_COUNT * checkPercent) {
            ingredientManager.resupplyIngredients(INGREDIENT_COUNT);
        }

        for (Enemy npc : npcList) {
            npc.update(delta, playerPos, npcList);
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
        final float cameraOffset = 0.5f;
        float halfViewportWidth = camera.viewportWidth * cameraOffset;
        float halfViewportHeight = camera.viewportHeight * cameraOffset;

        // Retrieve map dimensions from properties
        int mapWidthInTiles = map.getProperties().get("width", Integer.class);
        int mapHeightInTiles = map.getProperties().get("height", Integer.class);
        int tilePixelWidth = map.getProperties().get("tilewidth", Integer.class);
        int tilePixelHeight = map.getProperties().get("tileheight", Integer.class);
        float mapWidth = (mapWidthInTiles * tilePixelWidth) / MainGame.PIXELS_PER_METER;
        float mapHeight = (mapHeightInTiles * tilePixelHeight)
            / MainGame.PIXELS_PER_METER;

        // Clamp the camera's x and y values
        float camX = Math.max(halfViewportWidth,
            Math.min(camera.position.x, mapWidth - halfViewportWidth));
        float camY = Math.max(halfViewportHeight,
            Math.min(camera.position.y, mapHeight - halfViewportHeight));
        camera.position.set(camX, camY, 0);

        camera.update();
        mapRenderer.setView(camera);
        TimeManager.getInstance().update(delta);

        handlePoisonInput();
    }

    /**
     * Returns the atlas for royal consorts.
     *
     * @return the TextureAtlas for royal consorts
     */
    public TextureAtlas getAtlas() {
        return atlas;
    }

    private void saveGameState() {
        GameState state = new GameState();

        // Save player's position
        state.setPlayerXPosition(player.getBody().getPosition().x);
        state.setPlayerYPosition(player.getBody().getPosition().y);

        // Save player's remaining hearts
        state.setPlayerHealth(PlayerHealth.getInstance().getCurrentHearts());

        // Save player's rank
        state.setPlayerRank(player.getRank().name());

        // Save current stage
        state.setStage(stage);

        // Save item/ ingredients count
        PlayerInventory inventory = PlayerInventory.getInstance();
        state.setWaterCount(inventory.getWaterCount());
        state.setHerbCount(inventory.getHerbCount());
        state.setStoneCount(inventory.getStoneCount());
        state.setPoisonCount(inventory.getPoisonItemCount());
        state.setInvincibilityCount(inventory.getInvincibilityItemCount());
        state.setHeartRecoveryCount(inventory.getHeartRecoveryItemCount());

        GameSaveManager.saveGame(state);
        Gdx.app.log("GameState", state.toString());

        System.out.println("Game saved!");
    }

    private void applyGameState(final GameState state) {
        // Set the player's position
        player.getBody().setTransform(state.getPlayerXPosition(), state.getPlayerYPosition(), 0);

        // Update the player's rank (convert the string saved in state back to the enum)
        player.setRank(RoyalConsortType.valueOf(state.getPlayerRank()));

        // Set the current stage
        this.stage = state.getStage();

        // Update player's health (make sure you have a setter in PlayerHealth)
        PlayerHealth.getInstance().setCurrentHearts(state.getPlayerHealth());

        // Update inventory counts (you'll need to add setters
        // in your PlayerInventory if they don't exist yet)
        PlayerInventory inventory = PlayerInventory.getInstance();
        inventory.setWaterCount(state.getWaterCount());
        inventory.setHerbCount(state.getHerbCount());
        inventory.setStoneCount(state.getStoneCount());
        inventory.setPoisonItemCount(state.getPoisonCount());
        inventory.setInvincibilityItemCount(state.getInvincibilityCount());
        inventory.setHeartRecoveryItemCount(state.getHeartRecoveryCount());

        // Now that the player’s rank is updated,
        // filter out any consorts that should not be present.
        filterRoyalConsorts();

        recalculateRoyalConsortsAndNextTarget();

        Gdx.app.log("GameScreen", "Game state applied successfully.");
    }

    private void recalculateRoyalConsortsAndNextTarget() {
        // 1. Update the player's appearance based on the current rank.
        RoyalConsortType currentRank = player.getRank();
        player.updateAppearance(currentRank, getAtlas());

        // 2. Remove any dead royal consorts from the list.
        royalConsortList.removeIf(consort -> !consort.isAlive());

        // 3. Determine the next target (one rank up from the current rank).
        RoyalConsortType nextTarget = null;
        for (RoyalConsortType target : killOrder) {
            if (target.ordinal() > currentRank.ordinal()) {
                nextTarget = target;
                break;
            }
        }
        if (nextTarget != null) {
            currentTargetIndex = killOrder.indexOf(nextTarget);
        } else {
            // If there is no next target (player might be at the highest rank),
            // you can decide to either keep the current target, wrap around, or set a flag.
            currentTargetIndex = -1;
            Gdx.app.log("GameScreen", "Player is at the highest rank. No next target.");
        }

        assert nextTarget != null;
        Gdx.app.log("GameScreen", "Player appearance updated to " + currentRank
            + ". Next target: " + nextTarget.getName());
    }

    private void filterRoyalConsorts() {
        RoyalConsortType currentRank = player.getRank();
        // Remove any consort whose type is lower than or equal to the player's rank.
        royalConsortList.removeIf(consort -> consort.getType().compareTo(currentRank) <= 0);
    }


    private void handlePoisonInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            if (TimeManager.getInstance().getIsDayTime()) {
                return;
            }
            if (PlayerInventory.getInstance().getPoisonItemCount() >= 1) {
                PlayerInventory.getInstance().removePoisonItem();
                System.out.println("Poison item used");

                RoyalConsortType currentTarget = getCurrentTarget();

                for (RoyalConsort consort : royalConsortList) {
                    if (consort.shouldShowActionText() && consort.isAlive()) {
                        if (consort.getType() == currentTarget) {
                            consort.isPoisoned(); // Mark them poisoned
                            currentTargetIndex++;
                            MysteryBox.getInstance().updateRecipe();
                            nextStage();

                            // inherit the new rank and change appearance
                            player.updateAppearance(consort.getType(), getAtlas());
                            // If the player just became the Queen, show Game Over
                            if (consort.getType() == RoyalConsortType.QUEEN) {
                                showMessage("You are now the Queen!", Color.GOLD);
                                System.out.println("Player has become the Queen!"
                                    + "Transitioning in 2 seconds...");

                                // move to end screen after 2seconds delay
                                Timer.schedule(new Timer.Task() {
                                    @Override
                                    public void run() {
                                        game.setScreen(new EndScreen(game,
                                            "The Throne Is Yours!", Color.GOLD));
                                    }
                                }, 2f);

                                return;
                            }

                            System.out.println("Poisoned " + consort.getType().getName());
                            showMessage("Poisoned "
                                + consort.getType().getName() + "!", Color.SCARLET);
                        } else {
                            System.out.println("Wrong target! Lose 1 heart.");
                            showMessage("You tried to kill"
                                + "the wrong concubine! -1 heart", Color.RED);
                            PlayerHealth.getInstance().takeDamage(1);
                        }
                        break; // Only poison one at a time
                    }
                }
            }
        }
    }

    private void spawnPlayerBasedOnCurrentRank() {
        RoyalConsortType currentRank = player.getRank();
        boolean found = false;
        final RoyalConsortType initialRank = RoyalConsortType.GWI_IN;

        // For ranks other than Gwi In, search the existing consort list
        if (currentRank != initialRank) {
            for (RoyalConsort royalConsort : royalConsortList) {
                if (royalConsort.getType() == currentRank) {
                    float consortCenterX = royalConsort.getX() + royalConsort.getWidth() / 2;
                    float consortCenterY = royalConsort.getY() + royalConsort.getHeight() / 2;
                    player.getBody().setTransform(consortCenterX, consortCenterY, 0);
                    found = true;
                    break;
                }
            }
        } else {
            // For Gwi In, retrieve the tile directly from the map
            MapObjects consortObjects = map.getLayers().get("RoyalConsorts").getObjects();
            for (RectangleMapObject object : consortObjects.getByType(RectangleMapObject.class)) {
                if (initialRank.getName().equalsIgnoreCase(object.getName())) {
                    Rectangle rect = object.getRectangle();
                    float spawnX = (rect.x + rect.width / 2f) / MainGame.PIXELS_PER_METER;
                    float spawnY = (rect.y + rect.height / 2f) / MainGame.PIXELS_PER_METER;
                    player.getBody().setTransform(spawnX, spawnY, 0);
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            System.out.println("Spawn point not found for " + currentRank.getName());
        } else {
            System.out.println("Spawning player based on current rank: " + currentRank.getName());
        }
    }

    private void handleInput() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused; // Toggle pause flag
            pauseMenu.setVisible(isPaused);
            if (isPaused) {
                Gdx.input.setInputProcessor(pauseMenu.getStage());
            } else {
                Gdx.input.setInputProcessor(mainStage);
            }
        }

        if (!isPaused) {
            Body pBody = player.getBody();
            // Set a constant movement speed in world units per second
            float moveSpeed = 2.0f;
            // Start with zero velocity.
            Vector2 velocity = new Vector2(0, 0);

            // Check which keys are held down and adjust the velocity accordingly
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
            handleItemUsageInput();
        }
    }

    /**
     * Returns the player.
     *
     * @return the Player instance
     */
    public Player getPlayer() {
        return player;
    }

    private Vector2 worldToScreenCoordinates(final Vector2 worldPos) {
        Vector3 temporaryVector3 = new Vector3(worldPos.x, worldPos.y, 0);
        camera.project(temporaryVector3); // Converts from world → screen coordinates
        return new Vector2(temporaryVector3.x, temporaryVector3.y);
    }


    /**
     * Renders the game.
     *
     * @param delta the time elapsed since the last frame
     */
    @Override
    public void render(final float delta) {

        if (!isPaused) {
            // Update game logic
            update(delta);
        } else {
            pauseMenu.act(delta);
        }
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // --- WORLD CAMERA RENDERING ---
        camera.update();
        // Render the Tiled map
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Renderer our Box2DDebugLines
        // debugRenderer.render(world, camera.combined);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Enable alpha (transparency)
        game.batch.enableBlending();

        player.update(delta);

        // Draw world objects
        for (RoyalConsort royalConsort : royalConsortList) {
            royalConsort.draw(game.batch);
        }
        for (Enemy npc : npcList) {
            npc.draw(game.batch);
        }
        player.draw(game.batch);

        // Draw the ingredients
        ingredientManager.render(game.batch);

        game.batch.end();

        // --- UI CAMERA RENDERING ---
        uiCamera.update();
        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin();

        game.inventory.drawInventory(game.batch, Gdx.graphics.getWidth());
        game.health.drawHearts(game.batch, Gdx.graphics.getHeight());


        // Action Box Poison Message
        boolean isNight = !TimeManager.getInstance().getIsDayTime();
        for (RoyalConsort consort : royalConsortList) {
            if (consort.shouldShowActionText() && isNight) {
                Vector2 screenCenter
                    = worldToScreenCoordinates(consort.getActionBoxCenterWorldPosition());

                String actionText = "Press [X] to Poison";
                layout.setText(actionBoxFont, actionText);

                // Center the text on the screen position
                float textX = screenCenter.x - layout.width / 2f;
                float textY = screenCenter.y + layout.height / 2f;

                actionBoxFont.draw(game.batch, layout, textX, textY);
            }
        }

        MysteryBox mysteryBox = MysteryBox.getInstance();
        boolean hasAllIngredients = PlayerInventory.getInstance().hasEnough(
            mysteryBox.getWaterRequired(),
            mysteryBox.getLeafRequired(),
            mysteryBox.getRockRequired()
        );
        float plusHeightLength = 0f;
        float plusWidthLength = 0f;
        final float widthTextBox = 55f;
        final float heightTextBox = 100f;
        final float startNum = 150;
        if (kitchen != null) {
            Vector2 playerPos = player.getBody().getPosition();
            boolean isInKitchen = kitchen.contains(playerPos.x, playerPos.y);
            kitchen.setShowText(isInKitchen);
            final String recipeText = "              Recipe\n";
            final String requiredIngredient = String.format("water: %d, Rock: %d, Leaf: %d",
                MysteryBox.getInstance().getWaterRequired(),
                MysteryBox.getInstance().getRockRequired(),
                MysteryBox.getInstance().getLeafRequired());
            if (isInKitchen) {
                if (TimeManager.getInstance().getIsDayTime()) {
                    kitchen.setMessage("Only available at night");
                } else {
                    if (hasAllIngredients) {
                        kitchen.setMessage("      " + recipeText + "       " + requiredIngredient
                            + "\n\n<Press [M] to Craft Mystery Box>");
                        plusWidthLength = widthTextBox;
                    } else {
                        kitchen.setMessage(recipeText + "  "
                            + requiredIngredient + "\n\n<Not enough ingredients!>");
                    }
                    plusHeightLength = heightTextBox;
                }
            } else {
                kitchen.setMessage("");
            }

            kitchen.draw(game.batch, camera, actionBoxFont,
                layout, plusHeightLength, plusWidthLength);
        }

        // Rank HUD
        String titleText = "Current Rank: " + player.getRank().getName(); // e.g., "Gwi In"
        layout.setText(font, titleText);
        final int yOffset = 140;

        final float x = 30; // Left padding
        float y = Gdx.graphics.getHeight() - yOffset; // Top padding from screen

        font.draw(game.batch, layout, x, y);

        final int positionYOffset = 10;
        //Display remained time when player is invincible
        if (player.isInvincible()) {
            String invText = "Invincible: "
                + String.format("%.0f", player.getInvincibilityTimeRemaining())
                + " sec";
            layout.setText(font, invText);
            float positionY = y - layout.height - positionYOffset;
            font.draw(game.batch, layout, x, positionY);
        }

        // System Message
        float messageY = startNum; // Start position from bottom
        final int messageYOffset = 10;

        for (SystemMessage msg : messageQueue) {
            Color original = messageFont.getColor();
            messageFont.setColor(msg.getColor().r,
                msg.getColor().g, msg.getColor().b, msg.getAlpha());

            layout.setText(messageFont, msg.getText());
            float messageX = (Gdx.graphics.getWidth() - layout.width) / 2;
            messageFont.draw(game.batch, layout, messageX, messageY);

            messageY += layout.height + messageYOffset; // stack vertically
            messageFont.setColor(original); // Reset
        }

        RoyalConsortType currentTarget = getCurrentTarget();
        final int iconXOffset = 20;
        final int iconYOffset = 10;
        if (currentTarget != null) {
            Texture icon = targetIcons.get(currentTarget);
            final float iconWidth = 200;
            final float iconHeight = 200;
            float iconX = Gdx.graphics.getWidth() - iconWidth - iconXOffset;
            float iconY = Gdx.graphics.getHeight() - iconHeight - iconYOffset;

            game.batch.draw(icon, iconX, iconY, iconWidth, iconHeight);
        }

        game.batch.end();

        // Night overlay
        final float colorA = 0.3f;
        if (!TimeManager.getInstance().getIsDayTime()) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.setProjectionMatrix(uiCamera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, colorA);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        if (isPaused) {
            pauseMenu.draw();
        }
    }

    /**
     * Resizes the game screen.
     *
     * @param width the new screen width in pixels
     * @param height the new screen height in pixels
     */
    @Override
    public void resize(final int width, final int height) {
        // Update the viewport when the window size changes.
        viewport.update(width, height);
        uiCamera.setToOrtho(false, width, height);
        uiCamera.update();

        // Update the stage's viewport on resize
        pauseMenu.getStage().getViewport().update(width, height, true);
        // update main stage in the background as well(when pause menu pops up)
        mainStage.getViewport().update(width, height, true);
    }

    /**
     * Displays the game screen.
     */
    @Override
    public void show() {
        font = FontManager.getDefaultFont();
        final int actionBoxFontSize = 20;
        final int messageFontSize = 30;
        if (font == null) {
            FontManager.initialize();
            font = FontManager.getDefaultFont();
        }
        font.setColor(1, 1, 1, 1); // white and opaque
        actionBoxFont = FontManager.generateFont(actionBoxFontSize,
            Color.valueOf("c45800"));
        messageFont = FontManager.generateFont(messageFontSize, Color.WHITE);
    }

    /**
     * Hides the game screen.
     */
    @Override
    public void hide() {
    }

    /**
     * Pauses the game screen.
     */
    @Override
    public void pause() {
    }

    /**
     * Resumes the game screen.
     */
    @Override
    public void resume() {
    }

    /**
     * Disposes game resources.
     */
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

        for (Texture tex : targetIcons.values()) {
            tex.dispose();
        }
    }

    /**
     * Determines if the GameScreen is equal to another object.
     *
     * @param obj the object to compare
     * @return true if the GameScreen is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj.getClass() == this.getClass())) {
            return false;
        }

        GameScreen gameScreen = (GameScreen) obj;
        return Objects.equals(this.map, gameScreen.map)
            && Objects.equals(this.mapRenderer, gameScreen.mapRenderer)
            && Objects.equals(this.camera, gameScreen.camera)
            && Objects.equals(this.viewport, gameScreen.viewport)
            && Objects.equals(this.game, gameScreen.game)
            && Objects.equals(this.font, gameScreen.font)
            && Objects.equals(this.atlas, gameScreen.atlas)
            && Objects.equals(this.stage, gameScreen.stage)
            && Objects.equals(this.enemyAtlas, gameScreen.enemyAtlas)
            && Objects.equals(this.player, gameScreen.player)
            && Objects.equals(this.uiCamera, gameScreen.uiCamera)
            && Objects.equals(this.world, gameScreen.world)
            && Objects.equals(this.debugRenderer, gameScreen.debugRenderer)
            && Objects.equals(this.shapeRenderer, gameScreen.shapeRenderer)
            && Objects.equals(this.npcList, gameScreen.npcList)
            && Objects.equals(this.enemyRemovalQueue, gameScreen.enemyRemovalQueue)
            && Objects.equals(this.enemyCount, gameScreen.enemyCount)
            && Objects.equals(this.royalConsortList, gameScreen.royalConsortList)
            && Objects.equals(this.consortRemovalQueue, gameScreen.consortRemovalQueue)
            && Objects.equals(this.kitchen, gameScreen.kitchen)
            && Objects.equals(this.kitchenZone, gameScreen.kitchenZone)
            && Objects.equals(this.layout, gameScreen.layout)
            && Objects.equals(this.actionBoxFont, gameScreen.actionBoxFont)
            && Objects.equals(this.messageQueue, gameScreen.messageQueue)
            && Objects.equals(this.messageFont, gameScreen.messageFont)
            && Objects.equals(this.ingredientManager, gameScreen.ingredientManager)
            && Objects.equals(this.killOrder, gameScreen.killOrder)
            && Objects.equals(this.currentTargetIndex, gameScreen.currentTargetIndex)
            && Objects.equals(this.targetIcons, gameScreen.targetIcons)
            && Objects.equals(this.isPaused, gameScreen.isPaused)
            && Objects.equals(this.pauseMenu, gameScreen.pauseMenu)
            && Objects.equals(this.mainStage, gameScreen.mainStage);
    }

    /**
     * Gets the hashcode of the GameScreen.
     *
     * @return the hashcode of the GameScreen as an integer
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(this.map);
        final int prime = 21;
        result = prime * result + Objects.hashCode(this.mapRenderer);
        result = prime * result + Objects.hashCode(this.camera);
        result = prime * result + Objects.hashCode(this.viewport);
        result = prime * result + Objects.hashCode(this.game);
        result = prime * result + Objects.hashCode(this.font);
        result = prime * result + Objects.hashCode(this.atlas);
        result = prime * result + Objects.hashCode(this.stage);
        result = prime * result + Objects.hashCode(this.enemyAtlas);
        result = prime * result + Objects.hashCode(this.player);
        result = prime * result + Objects.hashCode(this.uiCamera);
        result = prime * result + Objects.hashCode(this.world);
        result = prime * result + Objects.hashCode(this.debugRenderer);
        result = prime * result + Objects.hashCode(this.shapeRenderer);
        result = prime * result + Objects.hashCode(this.npcList);
        result = prime * result + Objects.hashCode(this.enemyRemovalQueue);
        result = prime * result + Objects.hashCode(this.enemyCount);
        result = prime * result + Objects.hashCode(this.royalConsortList);
        result = prime * result + Objects.hashCode(this.consortRemovalQueue);
        result = prime * result + Objects.hashCode(this.kitchen);
        result = prime * result + Objects.hashCode(this.kitchenZone);
        result = prime * result + Objects.hashCode(this.layout);
        result = prime * result + Objects.hashCode(this.actionBoxFont);
        result = prime * result + Objects.hashCode(this.messageQueue);
        result = prime * result + Objects.hashCode(this.messageFont);
        result = prime * result + Objects.hashCode(this.ingredientManager);
        result = prime * result + Objects.hashCode(this.killOrder);
        result = prime * result + Objects.hashCode(this.currentTargetIndex);
        result = prime * result + Objects.hashCode(this.targetIcons);
        result = prime * result + Objects.hashCode(this.isPaused);
        result = prime * result + Objects.hashCode(this.pauseMenu);
        result = prime * result + Objects.hashCode(this.mainStage);
        return result;
    }

    /**
     * Gets the string representation of the GameScreen.
     *
     * @return the string representation of the GameScreen
     */
    @Override
    public String toString() {
        final StringBuilder sb;
        sb = new StringBuilder("GameScreen {");
        sb.append("map='").append(this.map).append('\'');
        sb.append("mapRenderer='").append(this.mapRenderer).append('\'');
        sb.append("camera='").append(this.camera).append('\'');
        sb.append("viewport='").append(this.viewport).append('\'');
        sb.append("game='").append(this.game).append('\'');
        sb.append(", font='").append(font).append('\'');
        sb.append(", atlas='").append(atlas).append('\'');
        sb.append(", stage='").append(stage).append('\'');
        sb.append(", enemyAtlas='").append(enemyAtlas).append('\'');
        sb.append(", player='").append(player).append('\'');
        sb.append(", uiCamera='").append(uiCamera).append('\'');
        sb.append(", world='").append(world).append('\'');
        sb.append(", debugRenderer='").append(debugRenderer).append('\'');
        sb.append(", shapeRenderer='").append(shapeRenderer).append('\'');
        sb.append(", npcList='").append(npcList).append('\'');
        sb.append(", enemyRemovalQueue='").append(enemyRemovalQueue).append('\'');
        sb.append(", enemyCount='").append(enemyCount).append('\'');
        sb.append(", royalConsortList='").append(royalConsortList).append('\'');
        sb.append(", consortRemovalQueue='").append(consortRemovalQueue).append('\'');
        sb.append(", kitchen='").append(kitchen).append('\'');
        sb.append(", kitchenZone='").append(kitchenZone).append('\'');
        sb.append(", layout='").append(layout).append('\'');
        sb.append(", actionBoxFont='").append(actionBoxFont).append('\'');
        sb.append(", messageQueue='").append(messageQueue).append('\'');
        sb.append(", messageFont='").append(messageFont).append('\'');
        sb.append(", ingredientManager='").append(ingredientManager).append('\'');
        sb.append(", killOrder='").append(killOrder).append('\'');
        sb.append(", currentTargetIndex='").append(currentTargetIndex).append('\'');
        sb.append(", targetIcons='").append(targetIcons).append('\'');
        sb.append(", isPaused='").append(isPaused).append('\'');
        sb.append(", pauseMenu='").append(pauseMenu).append('\'');
        sb.append(", mainStage='").append(mainStage);
        sb.append('}');
        return sb.toString();
    }
}
