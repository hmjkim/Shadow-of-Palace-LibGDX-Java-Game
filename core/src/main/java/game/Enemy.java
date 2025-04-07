package game;

import Sprites.FrameRegionDimensions;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an enemy sprite with basic AI for movement and chasing.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public class Enemy extends Sprite {

    private static final float CONSTANT_SPEED = 1.0f;
    private static final float RANDOM_MOVE_INTERVAL = 3.5f;
    private static final float CHASE_INTERVAL = 15f;
    private static final float CHASE_DURATION = 5f;
    private static final float MIN_RESPAWN_DISTANCE = 2.5f;
    private static final float DESIRED_ENEMY_DISTANCE = 1.0f;
    private static final float CHECK_DISTANCE = 3.0f;
    private static final float THRESHOLD = 0.0001f;
    private static final float FRICTION_RATE = 0.5f;

    private final World world;
    private Body body;
    private float stateTimer;
    private float chaseTimer;
    private boolean isChasing;

    private Vector2 scatterDirection;
    private final Vector2 spawnPos;

    private boolean pendingRespawn;

    private final FrameRegionDimensions dimensions;
    private Animation<TextureRegion> walkUp;
    private Animation<TextureRegion> walkRight;
    private Animation<TextureRegion> walkDown;
    private TextureRegion currentFrame;

    /**
     * Constructs an enemy.
     *
     * @param world the Box2D world for physics simulation
     * @param atlas the TextureAtlas containing enemy regions
     * @param spawnPos the spawn position in world units
     * @param regionName the name of the region in the atlas to use for this enemy
     * @param dimensions the frame dimensions for the enemy animation
     */
    public Enemy(final World world, final TextureAtlas atlas,
                 final Vector2 spawnPos,
                 final String regionName,
                 final FrameRegionDimensions dimensions) {
        super(atlas.findRegion(regionName));
        this.world = world;
        this.dimensions = dimensions;

        this.spawnPos = spawnPos.cpy();
        this.pendingRespawn = false;

        this.stateTimer = 0f;
        this.chaseTimer = 0f;
        this.isChasing = false;
        this.scatterDirection = new Vector2(0, 0);

        setSize(dimensions.getFrameWidth() / MainGame.PIXELS_PER_METER,
            dimensions.getFrameHeight() / MainGame.PIXELS_PER_METER);

        createBody(spawnPos);

        setupAnimation(atlas.findRegion(regionName));
        currentFrame = walkDown.getKeyFrame(0);
    }

    /*
     * Creates the physics body for the enemy at the given position.
     */
    private void createBody(final Vector2 pos) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        final int shapeRadius = 7;
        shape.setRadius(shapeRadius / MainGame.PIXELS_PER_METER);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = FRICTION_RATE;

        final int maskBit = 0xFFFF;
        fixtureDef.filter.categoryBits = 0x0002;
        fixtureDef.filter.maskBits = (short) (maskBit & ~(0x0002));
        body.createFixture(fixtureDef).setUserData(this);
        shape.dispose();
    }

    /*
     * Sets up animations for the enemy using the provided texture region.
     */
    private void setupAnimation(final TextureRegion region) {
        final int columns = 3;

        Array<TextureRegion> framesUp = new Array<>();
        Array<TextureRegion> framesRight = new Array<>();
        Array<TextureRegion> framesDown = new Array<>();

        for (int col = 0; col < columns; col++) {
            int x = region.getRegionX() + col * dimensions.getFrameWidth();

            int yUp = region.getRegionY();
            framesUp.add(new TextureRegion(region.getTexture(), x, yUp,
                dimensions.getFrameWidth(), dimensions.getFrameHeight()));

            int yRight = region.getRegionY() + dimensions.getFrameHeight();
            framesRight.add(new TextureRegion(region.getTexture(), x, yRight,
                dimensions.getFrameWidth(), dimensions.getFrameHeight()));

            int yDown = region.getRegionY() + dimensions.getFrameHeight() * 2;
            framesDown.add(new TextureRegion(region.getTexture(), x, yDown,
                dimensions.getFrameWidth(), dimensions.getFrameHeight()));
        }

        final float duration = 0.15f;
        walkUp = new Animation<>(duration, framesUp, Animation.PlayMode.LOOP);
        walkRight = new Animation<>(duration, framesRight, Animation.PlayMode.LOOP);
        walkDown = new Animation<>(duration, framesDown, Animation.PlayMode.LOOP);
    }

    /**
     * Updates the enemy's state and movement.
     *
     * @param delta the time in seconds
     * @param playerPosition the player's current position in world units
     * @param allEnemies a list of all enemies for collision avoidance
     */
    public void update(final float delta, final Vector2 playerPosition,
                       final List<Enemy> allEnemies) {
        if (pendingRespawn) {
            if (playerPosition.dst(spawnPos) >= MIN_RESPAWN_DISTANCE) {
                createBody(spawnPos);
                pendingRespawn = false;

                stateTimer = 0f;
                isChasing = false;
                scatterDirection.setZero();
            }
            return;
        }

        stateTimer += delta;

        if (!isChasing) {
            if (stateTimer >= CHASE_INTERVAL) {
                isChasing = true;
                chaseTimer = 0f;
            }
        }
        Vector2 velocityVec = new Vector2();

        if (isChasing) {
            chaseTimer += delta;
            velocityVec.set(playerPosition).sub(body.getPosition());
            if (velocityVec.len2() < THRESHOLD) {
                velocityVec.set(getRandomDirection());
            } else {
                velocityVec.nor();
            }
            velocityVec.scl(CONSTANT_SPEED);

            if (chaseTimer >= CHASE_DURATION) {
                isChasing = false;
                stateTimer = 0f;
                scatterDirection.setZero();
            }
        } else {
            if (scatterDirection.len2() < THRESHOLD) {
                scatterDirection = chooseRandomUnblockedDirection(allEnemies);
                stateTimer = 0f;
            } else {
                if (stateTimer >= RANDOM_MOVE_INTERVAL) {
                    scatterDirection = chooseRandomUnblockedDirection(allEnemies);
                    stateTimer = 0f;
                }
            }
            velocityVec.set(scatterDirection).scl(CONSTANT_SPEED);
        }

        body.setLinearVelocity(velocityVec);

        setPosition(body.getPosition().x - getWidth() / 2f,
            body.getPosition().y - getHeight() / 2f);

        updateAnimation(velocityVec);
    }

    /*
     * Chooses a random direction that is not blocked and not too close to other enemies.
     */
    private Vector2 chooseRandomUnblockedDirection(final List<Enemy> allEnemies) {
        Vector2[] candidates = {
            new Vector2(0, 1),
            new Vector2(0, -1),
            new Vector2(1, 0),
            new Vector2(-1, 0)
        };
        List<Vector2> validDirections = new ArrayList<>();

        for (Vector2 dir : candidates) {
            if (!isBlockedDirection(dir)) {
                validDirections.add(dir);
            }
            if (!isTooCloseToOtherEnemies(dir, allEnemies)) {
                validDirections.add(dir);
            }
        }
        return validDirections.get(MathUtils.random(validDirections.size() - 1));
    }

    /*
     * Checks if the direction is blocked by a static body.
     */
    private boolean isBlockedDirection(final Vector2 dir) {
        Vector2 start = body.getPosition();
        Vector2 end = start.cpy().add(dir.cpy().scl(CHECK_DISTANCE));

        final boolean[] blocked = {false};
        RayCastCallback callback = (fixture, point, normal, fraction) -> {
            if (fixture.getBody().getType() == BodyDef.BodyType.StaticBody) {
                blocked[0] = true;
                return 0;
            }
            return 1;
        };
        world.rayCast(callback, start, end);
        return blocked[0];
    }

    /*
     * Checks if moving in the given direction would bring the enemy too close to others.
     */
    private boolean isTooCloseToOtherEnemies(final Vector2 dir, final List<Enemy> allEnemies) {
        if (body == null) {
            return false;
        }

        final float nextPosScalar = 0.5f;
        Vector2 myPos = body.getPosition();
        Vector2 nextPos = myPos.cpy().add(dir.cpy().scl(nextPosScalar));

        for (Enemy e : allEnemies) {
            if (e == this) {
                continue;
            }
            if (e.isPendingRespawn()) {
                continue;
            }

            Vector2 otherPos = e.getBodyPosition();
            float dist = nextPos.dst(otherPos);
            if (dist < DESIRED_ENEMY_DISTANCE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the current position of the enemy's body.
     *
     * @return the enemy's position as a Vector2
     */
    public Vector2 getBodyPosition() {
        if (body != null) {
            return body.getPosition();
        }
        return new Vector2(getX() + getWidth() / 2, getY() + getHeight() / 2);
    }

    /**
     * Indicates whether the enemy is pending respawn.
     *
     * @return true if pending respawn, false otherwise
     */
    public boolean isPendingRespawn() {
        return pendingRespawn;
    }

    /*
     * Returns a random normalized direction vector.
     */
    private Vector2 getRandomDirection() {
        final float fullCircle = 360f;
        float angle = MathUtils.random(fullCircle);
        return new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).nor();
    }

    /*
     * Updates the enemy's animation based on its velocity.
     */
    private void updateAnimation(final Vector2 velocity) {
        if (velocity.len2() < THRESHOLD) {
            currentFrame = walkDown.getKeyFrame(0);
            return;
        }

        if (Math.abs(velocity.x) > Math.abs(velocity.y)) {
            if (velocity.x > 0) {
                currentFrame = walkRight.getKeyFrame(stateTimer, true);
                if (currentFrame.isFlipX()) {
                    currentFrame.flip(true, false);
                }
            } else {
                currentFrame = walkRight.getKeyFrame(stateTimer, true);
                if (!currentFrame.isFlipX()) {
                    currentFrame.flip(true, false);
                }
            }
        } else {
            if (velocity.y > 0) {
                currentFrame = walkUp.getKeyFrame(stateTimer, true);
            } else {
                currentFrame = walkDown.getKeyFrame(stateTimer, true);
            }
            if (currentFrame.isFlipX()) {
                currentFrame.flip(true, false);
            }
        }
    }

    /**
     * Draws the enemy sprite using the current animation frame.
     *
     * @param batch the Batch used for drawing
     */
    @Override
    public void draw(final Batch batch) {
        if (pendingRespawn) {
            return;
        }
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }

    /**
     * Handles collision with the player by marking the enemy for respawn.
     */
    public void onPlayerCollision() {
        if (body != null) {
            world.destroyBody(body);
            body = null;
        }
        pendingRespawn = true;
    }

    /**
     * Determines if the Enemy is equal to another object.
     *
     * @param object the object to compare
     * @return true if the Enemy is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Enemy enemy = (Enemy) object;
        return Float.compare(stateTimer, enemy.stateTimer) == 0
            && Float.compare(chaseTimer, enemy.chaseTimer) == 0
            && isChasing == enemy.isChasing
            && isPendingRespawn() == enemy.isPendingRespawn()
            && Objects.equals(world, enemy.world)
            && Objects.equals(body, enemy.body)
            && Objects.equals(scatterDirection, enemy.scatterDirection)
            && Objects.equals(spawnPos, enemy.spawnPos)
            && Objects.equals(dimensions, enemy.dimensions)
            && Objects.equals(walkUp, enemy.walkUp)
            && Objects.equals(walkRight, enemy.walkRight)
            && Objects.equals(walkDown, enemy.walkDown)
            && Objects.equals(currentFrame, enemy.currentFrame);
    }

    /**
     * Gets the hashcode of the Enemy.
     *
     * @return the hashcode of the Enemy as an integer
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = Objects.hashCode(world);
        result = prime * result + Objects.hashCode(body);
        result = prime * result + Float.hashCode(stateTimer);
        result = prime * result + Float.hashCode(chaseTimer);
        result = prime * result + Boolean.hashCode(isChasing);
        result = prime * result + Objects.hashCode(scatterDirection);
        result = prime * result + Objects.hashCode(spawnPos);
        result = prime * result + Boolean.hashCode(isPendingRespawn());
        result = prime * result + Objects.hashCode(dimensions);
        result = prime * result + Objects.hashCode(walkUp);
        result = prime * result + Objects.hashCode(walkRight);
        result = prime * result + Objects.hashCode(walkDown);
        result = prime * result + Objects.hashCode(currentFrame);
        result = prime * result + super.hashCode();
        return result;
    }

    /**
     * Gets the string representation of the Enemy.
     *
     * @return the string representation of the Enemy
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Enemy{");
        sb.append("world=").append(world);
        sb.append(", body=").append(body);
        sb.append(", stateTimer=").append(stateTimer);
        sb.append(", chaseTimer=").append(chaseTimer);
        sb.append(", isChasing=").append(isChasing);
        sb.append(", scatterDirection=").append(scatterDirection);
        sb.append(", spawnPos=").append(spawnPos);
        sb.append(", pendingRespawn=").append(pendingRespawn);
        sb.append(", dimensions=").append(dimensions);
        sb.append(", walkUp=").append(walkUp);
        sb.append(", walkRight=").append(walkRight);
        sb.append(", walkDown=").append(walkDown);
        sb.append(", currentFrame=").append(currentFrame);
        sb.append('}');
        return sb.toString();
    }
}
