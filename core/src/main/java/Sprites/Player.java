package Sprites;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import game.GameScreen;
import game.MainGame;
import com.badlogic.gdx.utils.Array;
import game.TimeManager;

import java.util.Objects;

/**
 * Represents the player.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public class Player extends Sprite {
    // Constants for your sprite sheet grid
    private static final int CELL_WIDTH = 25; // 25 pixels per cell in width
    private static final int CELL_HEIGHT = 30; // 30 pixels per cell in height

    private RoyalConsortType rank;
    private State currentState;
    private final World world;
    private Body body;
    private final GameScreen screen;

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
     * Constructs a Player with the specified world, screen.
     *
     * @param world the Box2D world to which the player's body is added.
     * @param screen the GameScreen providing assets and context
     */
    public Player(final World world, final GameScreen screen) {
        super(screen.getAtlas().findRegion(RoyalConsortType.GWI_IN.getRegionName()));
        this.world = world;
        this.screen = screen;
        currentState = State.STANDING;
        rank = RoyalConsortType.GWI_IN;
        stateTimer = 0;
        direction = Direction.SOUTH;
        this.invincible = false;
        this.invincibilityTimer = 0f;
        // Set the origin for rotations and scaling.
        setOriginCenter();
        // --- Create the Player's Physics Body ---
        definePlayer();

        // Setup sprite sheet
        updateAppearance(RoyalConsortType.GWI_IN, screen.getAtlas());

    }

    /**
     * Returns the player's rank.
     *
     * @return the RoyalConsortType representing the player's rank
     */
    public RoyalConsortType getRank() {
        return rank;
    }

    /**
     * Sets the player's rank.
     *
     * @param rank the new RoyalConsortType for the player
     */
    public void setRank(final RoyalConsortType rank) {
        this.rank = rank;
    }

    private TextureRegion getSpriteFrame(final int row, final int col,
                                         final FrameRegionDimensions dimensions) {

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

    /**
     * Sets the player's invincibility.
     *
     * @param time the duration of invincibility in seconds
     */
    public void setInvincible(final float time) {
        invincible = true;
        invincibilityTimer = time;
    }

    /**
     * Returns whether the player is invincible.
     *
     * @return true if invincible; false otherwise
     */
    public boolean isInvincible() {
        return invincible;
    }

    /**
     * Returns the remaining invincibility time.
     *
     * @return the remaining invincibility time in seconds
     */
    public float getInvincibilityTimeRemaining() {
        return invincibilityTimer;
    }

    private void definePlayer() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set the initial position (convert from pixels to world units).
        final int pixel1 = 150;
        final int pixel2 = 160;
        final float frictionValue = 0.5f;
        bodyDef.position.set(pixel1 / MainGame.PIXELS_PER_METER,
            pixel2 / MainGame.PIXELS_PER_METER);
        body = world.createBody(bodyDef);

        // --- Define Collision Shape ---
        // Create a circular shape for the player's collision.
        CircleShape shape = new CircleShape();
        final int shapeRadius = 7;
        shape.setRadius(shapeRadius / MainGame.PIXELS_PER_METER);

        // Create a fixture definition with density and friction.
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = frictionValue;
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        // Dispose the shape after use.
        shape.dispose();

        // --- Add Linear Damping ---
        // This helps the player slow down when no input is given.
        final float linearDamping = 3f;
        body.setLinearDamping(linearDamping);
    }

    /**
     * Updates the player's appearance.
     *
     * @param newRank the new RoyalConsortType for the player
     * @param concubineAtlas the texture atlas containing the sprite sheet
     */
    public void updateAppearance(final RoyalConsortType newRank,
                                 final TextureAtlas concubineAtlas) {
        setRank(newRank);

        // Get the base region for the new rank
        TextureRegion newBase = concubineAtlas.findRegion(newRank.getRegionName());
        setRegion(newBase); // fallback if animations fail

        if (newBase == null) {
            System.out.println("Player atlas region for "
                + newRank.getRegionName() + " not found!");
            return;
        }
        // Set the new animations
        FrameRegionDimensions[] dimensions = newRank.getFrameDimensions();

        Array<TextureRegion> framesWalkUp = new Array<>();
        Array<TextureRegion> framesRight = new Array<>();
        Array<TextureRegion> framesDown = new Array<>();

        final int colLimit = 3;
        for (int col = 0; col < colLimit; col++) {
            framesWalkUp.add(getSpriteFrame(0, col, dimensions[0]));
            framesRight.add(getSpriteFrame(1, col, dimensions[1]));
            framesDown.add(getSpriteFrame(2, col, dimensions[2]));
        }

        final float frameDuration = 0.1f;
        walkUp = new Animation<>(frameDuration, framesWalkUp, Animation.PlayMode.LOOP);
        walkRight = new Animation<>(frameDuration, framesRight, Animation.PlayMode.LOOP);
        walkDown = new Animation<>(frameDuration, framesDown, Animation.PlayMode.LOOP);
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
    public void update(final float delta) {
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
        if (getState() == State.WALKING) {
            stateTimer += delta;
        } else {
            stateTimer = 0;
        }

        // Update the sprite's region based on the current frame.
        setRegion(getFrame());
    }

    /**
     * Retrieves the current animation frame.
     *
     * @return the current TextureRegion frame
     */
    public TextureRegion getFrame() {
        // Determine current state
        currentState = getState();
        TextureRegion region;

        // If standing, always use the idle (down) frame.
        if (currentState == State.STANDING) {
            region = playerStand;  // Assume playerStand is the down-facing idle frame.
        } else {  // Walking state
            switch (direction) {
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
            }
        }
        return region;
    }

    /**
     * Sets the player's movement direction.
     *
     * @param dir the new Direction for movement
     */
    public void setDirection(final Direction dir) {
        this.direction = dir;
    }

    /**
     * Determines the player's state based on body velocity.
     *
     * @return State.WALKING if moving
     */
    public State getState() {
        // Check if the overall speed is above a small threshold
        final float speed = 0.1f;
        if (body.getLinearVelocity().len() > speed) {
            return State.WALKING;
        } else {
            //if none of these return then player must be standing
            return State.STANDING;
        }
    }

    /**
     * Returns the player's Box2D body.
     *
     * @return the dynamic Body representing the player.
     */
    public Body getBody() {
        return body;
    }

    /**
     * Draws the player sprite.
     *
     * @param batch the SpriteBatch used for drawing
     */
    @Override
    public void draw(final Batch batch) {
        // Draw the sprite (player image) using the current sprite batch.
        super.draw(batch);
    }

    /**
     * Applies damage to the player upon collision with an enemy.
     */
    public void hitByNPC() {
        if (isInvincible()) {
            return;
        }

        float damage = 1.0f;
        final float dayTimeDamage = 0.5f;
        if (TimeManager.getInstance().getIsDayTime()) {
            damage = dayTimeDamage;
        }

        System.out.println("Collision happened");
        PlayerHealth.getInstance().takeDamage(damage);
    }

    /**
     * Determines if the Player is equal to another object.
     *
     * @param obj the object to compare
     * @return true if the Player is equal to the other object, false otherwise
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

        Player player = (Player) obj;
        return super.equals(obj) && Objects.equals(this.rank, player.rank)
            && Objects.equals(this.currentState, player.currentState)
            && Objects.equals(this.world, player.world)
            && Objects.equals(this.body, player.body)
            && Objects.equals(this.screen, player.screen)
            && Objects.equals(this.playerStand, player.playerStand)
            && Objects.equals(this.walkUp, player.walkUp)
            && Objects.equals(this.walkDown, player.walkDown)
            && Objects.equals(this.walkRight, player.walkRight)
            && Objects.equals(this.stateTimer, player.stateTimer)
            && Objects.equals(this.direction, player.direction)
            && Objects.equals(this.invincible, player.invincible)
            && Objects.equals(this.invincibilityTimer, player.invincibilityTimer);
    }

    /**
     * Gets the hashcode of the Player.
     *
     * @return the hashcode of the Player as an integer
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(this.rank);
        final int prime = 19;
        result = prime * result + Objects.hashCode(this.currentState);
        result = prime * result + Objects.hashCode(this.world);
        result = prime * result + Objects.hashCode(this.body);
        result = prime * result + Objects.hashCode(this.screen);
        result = prime * result + Objects.hashCode(this.playerStand);
        result = prime * result + Objects.hashCode(this.walkDown);
        result = prime * result + Objects.hashCode(this.walkRight);
        result = prime * result + Objects.hashCode(this.walkUp);
        result = prime * result + Objects.hashCode(this.stateTimer);
        result = prime * result + Objects.hashCode(this.direction);
        result = prime * result + Objects.hashCode(this.invincible);
        result = prime * result + Objects.hashCode(this.invincibilityTimer);
        result = prime * result + super.hashCode();
        return result;
    }

    /**
     * Gets the string representation of the Player.
     *
     * @return the string representation of the Player
     */
    @Override
    public String toString() {
        final StringBuilder sb;
        sb = new StringBuilder("Player {");
        sb.append("rank='").append(this.rank).append('\'');
        sb.append("currentState='").append(this.currentState).append('\'');
        sb.append("world='").append(this.world).append('\'');
        sb.append("body='").append(this.body).append('\'');
        sb.append("screen='").append(this.screen).append('\'');
        sb.append("playerStand='").append(this.playerStand).append('\'');
        sb.append("walkDown='").append(this.walkDown).append('\'');
        sb.append("walkRight='").append(this.walkRight).append('\'');
        sb.append("walkUp='").append(this.walkUp).append('\'');
        sb.append("stateTimer='").append(this.stateTimer).append('\'');
        sb.append("direction='").append(this.direction).append('\'');
        sb.append("invincibilityTimer='").append(this.invincibilityTimer).append('\'');
        sb.append("invincible='").append(this.invincible);
        sb.append('}');
        return super.toString() + sb;
    }
}
