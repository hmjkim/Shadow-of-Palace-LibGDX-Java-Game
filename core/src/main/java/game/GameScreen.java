package game;

import Helper.FontManager;
import Ingredients.IngredientManager;
import Sprites.*;
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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import java.util.*;

/**
 * GameScreen sets up the Tiled map, Box2D world, collision objects, player, and camera.
 * It also handles continuous input for movement and renders all elements.
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

    private TextureAtlas atlas;
    private TextureAtlas enemyAtlas;

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

    // kitchen
    private Rectangle kitchenZone;
    private boolean showMysteryBoxPrompt;

    // Royal Consorts
    private List<RoyalConsort> royalConsortList;
    private final List<RoyalConsort> consortRemovalQueue;

    // Fonts and Text
    private final GlyphLayout layout;
    private BitmapFont actionBoxFont;
    private final List<SystemMessage> messageQueue;
    private BitmapFont messageFont;

    // Ingredients
    private List<Vector2> walkablePositions;
    private IngredientManager ingredientManager;

    //Target Royal Consort
    private List<RoyalConsortType> killOrder;
    private int currentTargetIndex;
    private Map<RoyalConsortType, Texture> targetIcons;

    // --- Tiled Map Setup ---
    // Load the Tiled map from the assets folder (ensure "map.tmx" exists).
    public GameScreen(MainGame mainGame) {
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
        this.showMysteryBoxPrompt = false;
        markKitchen();
        spawnPlayerBasedOnCurrentRank();

        // System Message
        messageQueue = new ArrayList<>();

        // Ingredients
        walkablePositions = getWalkablePositions();
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
        targetIcons.put(RoyalConsortType.BIN,    new Texture("target/target-bin.png"));
        targetIcons.put(RoyalConsortType.QUEEN, new Texture("target/target-queen.png"));

        // Spawn ingredients spread over the walkable area
        ingredientManager.spawnIngredients(INGREDIENT_COUNT);

    }

    public void showMessage(String text, Color color) {
        messageQueue.add(new SystemMessage(text, color));
    }

    private RoyalConsortType getCurrentTarget() {
        if (currentTargetIndex < killOrder.size()) {
            return killOrder.get(currentTargetIndex);
        }
        return null;
    }

    private void handleItemUsageInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            if (PlayerInventory.getInstance().getInvincibilityItemCount() >= 1) {
                PlayerInventory.getInstance().removeInvincibilityItem();
                player.setInvincible(20f);
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
            MysteryBoxOutcome outcome = MysteryBox.getInstance().craft(PlayerInventory.getInstance());
            if (outcome != null) {
                switch (outcome) {
                    case FAILURE:
                        System.out.println("You got nothing");
                        showMessage("You got nothing", Color.RED);
                        break;
                    case POISON:
                        System.out.println("You got a Poison item!");
                        showMessage("You got a Poison item!", Color.WHITE);
                        PlayerInventory.getInstance().addPoison();
                        break;
                    case INVINCIBILITY:
                        System.out.println("You got a Invincibility item!");
                        showMessage("You got a Invincibility item!", Color.WHITE);
                        PlayerInventory.getInstance().addInvincibility();
                        break;
                    case HEART_RECOVERY:
                        System.out.println("You got a Heart Recovery item!");
                        showMessage("You got a Heart Recovery item!", Color.WHITE);
                        PlayerInventory.getInstance().addHeartRecovery();
                        break;
                }
            } else {
                System.out.println("Not enough ingredients!");
                showMessage("Not enough ingredients!", Color.RED);
            }
        }
    }

    public void scheduleEnemyRemoval(Enemy enemy) {
        if (!enemyRemovalQueue.contains(enemy)) {
            enemyRemovalQueue.add(enemy);
        }
    }

    private void markKitchen() {
        if (map.getLayers().get("kitchen") != null) {
            MapObjects kitchenObjects = map.getLayers().get("kitchen").getObjects();
            if (!kitchenObjects.getByType(RectangleMapObject.class).isEmpty()) {
                RectangleMapObject kitchenObj = (RectangleMapObject) kitchenObjects.getByType(RectangleMapObject.class).first();
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
            Rectangle rect = ((RectangleMapObject) obj).getRectangle();
            float centerX = (rect.x + rect.width / 2f) / MainGame.PIXELS_PER_METER;
            float centerY = (rect.y + rect.height / 2f) / MainGame.PIXELS_PER_METER;
            Vector2 spawnPos = new Vector2(centerX, centerY);

            final int enemyTotalTypes = 4;
            Random random = new Random();
            int enemyType = random.nextInt(enemyTotalTypes) + 1; // min 1 to max enemy total types
            String enemyRegionName = "enemy" + enemyType;
            System.out.println(enemyRegionName);
            FrameRegionDimensions dimensions;
            switch(enemyType) {
                case 1:
                    dimensions = new FrameRegionDimensions(20, 30);
                    break;
                case 2:
                    dimensions = new FrameRegionDimensions(17, 30);
                    break;
                case 3:
                    dimensions = new FrameRegionDimensions(21, 31);
                    break;
                case 4:
                    dimensions = new FrameRegionDimensions(24, 32);
                    break;
                default:
                    // Provide a default value
                    dimensions = new FrameRegionDimensions(20, 30);
                    break;
            }

            npcList.add(new Enemy(world, this, enemyAtlas, spawnPos, enemyRegionName, dimensions));
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
                for (RoyalConsortType consortType : RoyalConsortType.values()) {
                    if (consortType.getName().equalsIgnoreCase(objectName)) {
                        // Check if this consort type is less than or equal to the player's current rank
                        // If so, skip spawning it because that rank is already occupied or "dead"
                        if (consortType.compareTo(player.getRank()) <= 0) {
                            // Skip this consort
                            break;
                        }

                        // First, calculate the center of the rectangle (in pixels) and convert to world units.
                        float centerX = (rectangle.x + rectangle.width / 2f) / MainGame.PIXELS_PER_METER;
                        float centerY = (rectangle.y + rectangle.height / 2f) / MainGame.PIXELS_PER_METER;

                        // Create the texture region for the NPC using the front image file path from the enum.
                        TextureRegion region = new TextureRegion(new Texture(consortType.getFrontImagePath()));

                        // Calculate the sprite's size in world units
                        float spriteWidth = region.getRegionWidth() / MainGame.PIXELS_PER_METER;
                        float spriteHeight = region.getRegionHeight() / MainGame.PIXELS_PER_METER;

                        // Calculate the position so that the sprite is centered on the rectangle
                        float finalX = centerX - spriteWidth / 2f;
                        float finalY = centerY - spriteHeight / 2f;

                        // Create the RoyalConsort instance with its type and adjusted position
                        RoyalConsort consort = new RoyalConsort(region, consortType, finalX, finalY);
                        royalConsortList.add(consort);
                        break;
                    }
                }
            }
        }
    }

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
    public void update(float delta) {
        handleInput(delta);
        // Remove expired messages
        messageQueue.removeIf(message -> message.update(delta));

        world.step(1/60f, 6, 2);
        if (!enemyRemovalQueue.isEmpty()) {
            for (Enemy enemy : enemyRemovalQueue) {
                enemy.onPlayerCollision();
            }
            enemyRemovalQueue.clear();
        }
        player.update(delta);

        Vector2 playerPos = player.getBody().getPosition();

        // Collect a nearby ingredient when SPACE is pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            ingredientManager.collectNearbyIngredient(playerPos, 0.3f, PlayerInventory.getInstance());
        }

        // If less than 70% of the ingredients remain, replenish to the original amount.
        if (ingredientManager.getIngredients().size < INGREDIENT_COUNT * 0.7f) {
            ingredientManager.resupplyIngredients(INGREDIENT_COUNT);
        }

        for (Enemy npc : npcList) {
            npc.update(delta, playerPos);
        }

        if (kitchenZone != null && kitchenZone.contains(playerPos)) {
            showMysteryBoxPrompt = true;
        } else {
            showMysteryBoxPrompt = false;
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

    public TextureAtlas getAtlas() {
        return atlas;
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

                            // inherit the new rank and change appearance
                            player.updateAppearance(consort.getType(), getAtlas());
                            System.out.println("Poisoned " + consort.getType().getName());
                            showMessage("Poisoned " + consort.getType().getName() + "!", Color.SCARLET);
                        } else {
                            System.out.println("Wrong target! Lose 1 heart.");
                            showMessage("You tried to kill the wrong concubine! -1 heart", Color.RED);
                            PlayerHealth.getInstance().takeDamage(1);
                        }
                        break; // Only poison one at a time
                    }
                }
            }
        }
    }

    private void spawnPlayerBasedOnCurrentRank() {
        // Retrieve the player's current rank
        RoyalConsortType currentRank = player.getRank();
        // Loop through all royal consorts to find the one matching the current rank
        for (RoyalConsort royalConsort : royalConsortList) {
            if (royalConsort.getType() == currentRank) {
                // Calculate the center position of the royal consort
                float consortCenterX = royalConsort.getX() + royalConsort.getWidth() / 2;
                float consortCenterY = royalConsort.getY() + royalConsort.getHeight() / 2;
                // Set the player's Box2D body to that position (keeping its current rotation)
                player.getBody().setTransform(consortCenterX, consortCenterY, 0);
                break;
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
        handleItemUsageInput();
    }

    public Player getPlayer() {
        return player;
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
                Vector2 screenCenter = worldToScreenCoordinates(consort.getActionBoxCenterWorldPosition());

                String actionText = "Press [X] to Poison";
                layout.setText(actionBoxFont, actionText);

                // Center the text on the screen position
                float textX = screenCenter.x - layout.width / 2f;
                float textY = screenCenter.y + layout.height / 2f;

                actionBoxFont.draw(game.batch, layout, textX, textY);
            }
        }

        //Show up Mystery Box message
        if (showMysteryBoxPrompt) {
            float centerX = kitchenZone.x + kitchenZone.width / 2f;
            float centerY = kitchenZone.y + kitchenZone.height / 2f;
            Vector2 zoneCenter = new Vector2(centerX, centerY);
            Vector2 screenPos = worldToScreenCoordinates(zoneCenter);

            float bubbleWidth = RoyalConsort.getActionBoxRegion().getRegionWidth() / MainGame.PIXELS_PER_METER;
            float bubbleHeight = RoyalConsort.getActionBoxRegion().getRegionHeight() / MainGame.PIXELS_PER_METER;
            float bubbleX = screenPos.x - bubbleWidth / 2f;
            float bubbleY = screenPos.y + 50;

            game.batch.draw(RoyalConsort.getActionBoxRegion(), bubbleX, bubbleY, bubbleWidth, bubbleHeight);

            MysteryBox mysteryBox = MysteryBox.getInstance();
            boolean hasAllIngredients = PlayerInventory.getInstance().hasEnough(
                mysteryBox.getWaterRequired(),
                mysteryBox.getLeafRequired(),
                mysteryBox.getRockRequired()
            );
            String prompt;
            Color promptColor;
            if (TimeManager.getInstance().getIsDayTime()) {
                prompt = "Mystery Box crafting is only available at night";
                promptColor = Color.RED;
            } else {
                if (hasAllIngredients) {
                    prompt = "Press [M] to Craft Mystery Box";
                    promptColor = Color.WHITE;
                } else {
                    prompt = "Not enough ingredients!";
                    promptColor = Color.RED;
                }
            }

            layout.setText(actionBoxFont, prompt);
            float textX = bubbleX + (bubbleWidth - layout.width) / 2f;
            float textY = bubbleY + (bubbleHeight + layout.height) / 2f;
            actionBoxFont.setColor(promptColor);
            actionBoxFont.draw(game.batch, layout, textX, textY);
            actionBoxFont.setColor(Color.WHITE);
        }

        // Rank HUD
        String titleText = "Current Rank: " + player.getRank().getName(); // e.g., "Gwi In"
        layout.setText(font, titleText);

        float x = 30; // Left padding
        float y = Gdx.graphics.getHeight() - 140; // Top padding from screen

        font.draw(game.batch, layout, x, y);

        //Display remained time when player is invincible
        if (player.isInvincible()) {
            String invText = "Invincible: " + String.format("%.0f", player.getInvincibilityTimeRemaining()) + " sec";
            layout.setText(font, invText);
            float positionY = y - layout.height - 10;
            font.draw(game.batch, layout, x, positionY);
        }

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

        RoyalConsortType currentTarget = getCurrentTarget();
        if (currentTarget != null) {
            Texture icon = targetIcons.get(currentTarget);
            float iconWidth = 200;
            float iconHeight = 200;
            float iconX = Gdx.graphics.getWidth() - iconWidth - 20;
            float iconY = Gdx.graphics.getHeight() - iconHeight - 10;

            game.batch.draw(icon, iconX, iconY, iconWidth, iconHeight);
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

        for (Texture tex : targetIcons.values()) {
            tex.dispose();
        }
//        player.dispose();
    }
}
