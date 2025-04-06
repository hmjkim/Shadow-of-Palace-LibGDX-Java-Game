package Sprites;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import game.GameScreen;
import game.MainGame;
import com.badlogic.gdx.utils.Array;
import game.TimeManager;

/**
 * The Player class represents the player character.
 * It creates a dynamic Box2D body for collision and physics,
 * loads the player image, and updates its sprite position accordingly.
 */
public class Player extends Sprite {
    private RoyalConsortType rank;

    // Constants for your sprite sheet grid
    private static final int CELL_WIDTH = 25; // 25 pixels per cell in width
    private static final int CELL_HEIGHT = 30; // 30 pixels per cell in height

    private State currentState;
    private final World world;
    private Body body;
    private final GameScreen screen;
    // private final Texture texture;
    // Animation
    private TextureRegion playerStand;
    private Animation<TextureRegion> walkUp;
    private Animation<TextureRegion> walkRight;
    private Animation<TextureRegion> walkDown;
    private float stateTimer;
    private Direction direction;

    private boolean invincible;
    private float invincibilityTimer;

    /**
     * Constructs the player, loads the image, and creates its Box2D body.
     *
     * @param world The Box2D world to which the player's body is added.
     */
    public Player(World world, GameScreen screen) {
        super(screen.getAtlas().findRegion(RoyalConsortType.GWI_IN.getRegionName()));
        this.world = world;
        this.screen = screen;
        currentState = State.STANDING;
        rank = RoyalConsortType.GWI_IN;
        stateTimer = 0;
        direction = Direction.SOUTH;
        this.invincible = false;
        this.invincibilityTimer = 0f;
        // --- Load Player Image ---
        // Load the player's texture. Ensure "player.png" is in your assets folder.
        // texture = new Texture("player.png");
        // Set the texture as the sprite region.
        // setRegion(texture);
        // Set sprite size based on conversion from pixels to Box2D world units.
        // setSize(texture.getWidth() / MainGame.PIXELS_PER_METER, texture.getHeight() / MainGame.PIXELS_PER_METER);
        // Set the origin for rotations and scaling.
        setOriginCenter();
        // --- Create the Player's Physics Body ---
        definePlayer();

        // Setup spritesheet
        updateAppearance(RoyalConsortType.GWI_IN, screen.getAtlas());

    }

    public RoyalConsortType getRank() {
        return rank;
    }

    public void setRank(RoyalConsortType rank) {
        this.rank = rank;
    }


    private TextureRegion getSpriteFrame(final int row, final int col, final FrameRegionDimensions dimensions) {

        // Get the base region of the current character
        TextureRegion baseRegion = screen.getAtlas().findRegion(rank.getRegionName());

        int baseX = baseRegion.getRegionX();
        int baseY = baseRegion.getRegionY();

        int frameWidth = dimensions.getFrameWidth();
        int frameHeight = dimensions.getFrameHeight();

        int x = baseX + col * CELL_WIDTH;
        int y = baseY + row * CELL_HEIGHT;

        return new TextureRegion(baseRegion.getTexture(), x, y, frameWidth, frameHeight);
    }

    public void setInvincible(float time) {
        invincible = true;
        invincibilityTimer = time;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public float getInvincibilityTimeRemaining() {
        return invincibilityTimer;
    }

    /**
     * Creates the Box2D dynamic body for the player, including collision shape and fixture.
     * Sets the starting position to (100, 100) pixels (converted to world units).
     */
    private void definePlayer() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        // Set the initial position (convert from pixels to world units).
        bdef.position.set(150 / MainGame.PIXELS_PER_METER, 160 / MainGame.PIXELS_PER_METER);
        body = world.createBody(bdef);

        // --- Define Collision Shape ---
        // Create a circular shape for the player's collision.
        CircleShape shape = new CircleShape();
        int shapeRadius = 7;
        shape.setRadius(shapeRadius / MainGame.PIXELS_PER_METER);

        // Create a fixture definition with density and friction.
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1f;
        fdef.friction = 0.5f;
        Fixture fixture = body.createFixture(fdef);
        fixture.setUserData(this);
        // Dispose the shape after use.
        shape.dispose();

        // --- Add Linear Damping ---
        // This helps the player slow down when no input is given.
        body.setLinearDamping(3f);
    }

    public void updateAppearance(final RoyalConsortType newRank, final TextureAtlas concubineAtlas) {
        setRank(newRank);

        // Get the base region for the new rank
        TextureRegion newBase = concubineAtlas.findRegion(newRank.getRegionName());
        setRegion(newBase); // fallback if animations fail

        if (newBase == null) {
            System.out.println("Player atlas region for " + newRank.getRegionName() + " not found!");
            return;
        }
        // Set the new animations
        FrameRegionDimensions[] dimensions = newRank.getFrameDimensions();

        Array<TextureRegion> framesWalkUp = new Array<>();
        Array<TextureRegion> framesRight = new Array<>();
        Array<TextureRegion> framesDown = new Array<>();

        for (int col = 0; col < 3; col++) {
            framesWalkUp.add(getSpriteFrame(0, col, dimensions[0]));
            framesRight.add(getSpriteFrame(1, col, dimensions[1]));
            framesDown.add(getSpriteFrame(2, col, dimensions[2]));
        }

        walkUp = new Animation<>(0.1f, framesWalkUp, Animation.PlayMode.LOOP);
        walkRight = new Animation<>(0.1f, framesRight, Animation.PlayMode.LOOP);
        walkDown = new Animation<>(0.1f, framesDown, Animation.PlayMode.LOOP);
        playerStand = getSpriteFrame(2, 1, dimensions[2]);

        // Update sprite size
        setBounds(0, 0,
            dimensions[2].getFrameWidth() / MainGame.PIXELS_PER_METER,
            dimensions[2].getFrameHeight() / MainGame.PIXELS_PER_METER);
    }


    /**
     * Updates the sprite's position to follow its Box2D body.
     *
     * @param delta The time elapsed since the last frame.
     */
    public void update(float delta) {
        if (invincible) {
            invincibilityTimer -= delta;
            if (invincibilityTimer <= 0f) {
                invincible = false;
            }
        }
        // Center the sprite on the Box2D body's position.
        setPosition(
            body.getPosition().x - getWidth() / 2,
            body.getPosition().y - getHeight() / 2
        );

        // If the player is walking, increase stateTimer; otherwise, reset it.
        if(getState() == State.WALKING) {
            stateTimer += delta;
        } else {
            stateTimer = 0;
        }

        // Update the sprite's region based on the current frame.
        setRegion(getFrame());
    }

    public TextureRegion getFrame() {
        // Determine current state
        currentState = getState();
        TextureRegion region;

        // If standing, always use the idle (down) frame.
        if (currentState == State.STANDING) {
            region = playerStand;  // Assume playerStand is the down-facing idle frame.
        } else {  // Walking state
            switch(direction) {
                case NORTH:
                    region = walkUp.getKeyFrame(stateTimer, true);
                    break;
                case EAST:
                    region = walkRight.getKeyFrame(stateTimer, true);
                    // If the region is flipped, flip it back for east.
                    if (region.isFlipX()) {
                        region.flip(true, false);
                    }
                    break;
                case SOUTH:
                    region = walkDown.getKeyFrame(stateTimer, true);
                    break;
                case WEST:
                    region = walkRight.getKeyFrame(stateTimer, true);
                    if (!region.isFlipX()) {
                        region.flip(true, false);
                    }
                    break;
                default:
                    region = playerStand;
                    break;
            }
        }
        return region;
    }

    public void setDirection(Direction dir) {
        this.direction = dir;
    }


    public State getState() {
        // Check if the overall speed is above a small threshold
        if (body.getLinearVelocity().len() > 0.1f)
            return State.WALKING;
            //if none of these return then player must be standing
        else
            return State.STANDING;
    }

    /**
     * Returns the player's Box2D body.
     *
     * @return the dynamic Body representing the player.
     */
    public Body getBody() {
        return body;
    }

    @Override
    public void draw(Batch batch) {
        // Draw the sprite (player image) using the current sprite batch.
        super.draw(batch);
    }

    public void hitByNPC() {
        if (isInvincible()) {
            return;
        }

        float damage = TimeManager.getInstance().getIsDayTime() ? 0.5f : 1.0f;
        System.out.println("Collision happened");
        PlayerHealth.getInstance().takeDamage(damage);
    }

//    /**
//     * Dispose of the player's texture.
//     */
//    public void dispose() {
//        texture.dispose();
//    }
}
