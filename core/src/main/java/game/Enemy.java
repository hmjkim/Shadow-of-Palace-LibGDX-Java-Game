package game;

import Sprites.Direction;
import Sprites.FrameRegionDimensions;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;


public class Enemy extends Sprite {
    private final World world;
    private Body body;

    private static final float CONSTANT_SPEED = 1.0f;
    private static final float RANDOM_MOVE_INTERVAL = 3.5f;
    private static final float CHASE_INTERVAL = 15f;
    private static final float CHASE_DURATION = 5f;
    private static final float MIN_RESPAWN_DISTANCE = 2.5f;
    private static final float DESIRED_ENEMY_DISTANCE = 1.0f;
    private static final float CHECK_DISTANCE = 3.0f;

    private float stateTimer;
    private float chaseTimer;
    private boolean isChasing;

    private Vector2 scatterDirection;
    private final GameScreen screen;
    private final Vector2 spawnPos;

    private boolean pendingRespawn;

    private final FrameRegionDimensions dimensions;
    private Animation<TextureRegion> walkUp;
    private Animation<TextureRegion> walkRight;
    private Animation<TextureRegion> walkDown;
    private TextureRegion currentFrame;
    private Direction direction;

    public Enemy(World world, GameScreen screen, TextureAtlas atlas,
                 Vector2 spawnPos, String regionName, FrameRegionDimensions dimensions) {
        super(atlas.findRegion(regionName));
        this.world = world;
        this.screen = screen;
        this.dimensions = dimensions;

        this.spawnPos = spawnPos.cpy();
        this.pendingRespawn = false;

        this.stateTimer = 0f;
        this.chaseTimer = 0f;
        this.isChasing = false;
        this.scatterDirection = new Vector2(0, 0);
        this.direction = Direction.SOUTH;

        setSize(dimensions.getFrameWidth() / MainGame.PIXELS_PER_METER,
            dimensions.getFrameHeight() / MainGame.PIXELS_PER_METER);

        createBody(spawnPos);

        setupAnimation(atlas.findRegion(regionName));
        currentFrame = walkDown.getKeyFrame(0);
    }

    private void createBody(Vector2 pos) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(7 / MainGame.PIXELS_PER_METER);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;

        fixtureDef.filter.categoryBits = 0x0002;
        fixtureDef.filter.maskBits = (short)(0xFFFF & ~(0x0002));
        body.createFixture(fixtureDef).setUserData(this);
        shape.dispose();
    }

    private void setupAnimation(TextureRegion region) {
        int columns = 3;

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

            int yDown = region.getRegionY() + dimensions.getFrameHeight()*2;
            framesDown.add(new TextureRegion(region.getTexture(), x, yDown,
                dimensions.getFrameWidth(), dimensions.getFrameHeight()));
        }

        walkUp = new Animation<>(0.15f, framesUp, Animation.PlayMode.LOOP);
        walkRight = new Animation<>(0.15f, framesRight, Animation.PlayMode.LOOP);
        walkDown = new Animation<>(0.15f, framesDown, Animation.PlayMode.LOOP);
    }

    public void update(float delta, Vector2 playerPosition, List<Enemy> allEnemies) {
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
            if (velocityVec.len2() < 0.0001f) {
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
            if (scatterDirection.len2() < 0.0001f) {
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

        updateAnimation(delta, velocityVec);
    }

    private Vector2 chooseRandomUnblockedDirection(List<Enemy> allEnemies) {
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

    private boolean isBlockedDirection(Vector2 dir) {
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

    private boolean isTooCloseToOtherEnemies(Vector2 dir, List<Enemy> allEnemies) {
        if (body == null) return false;
        Vector2 myPos = body.getPosition();
        Vector2 nextPos = myPos.cpy().add(dir.cpy().scl(0.5f));

        for (Enemy e : allEnemies) {
            if (e == this) continue;
            if (e.isPendingRespawn()) continue;

            Vector2 otherPos = e.getBodyPosition();
            float dist = nextPos.dst(otherPos);
            if (dist < DESIRED_ENEMY_DISTANCE) {
                return true;
            }
        }
        return false;
    }

    public Vector2 getBodyPosition() {
        if (body != null) {
            return body.getPosition();
        }
        return new Vector2(getX() + getWidth()/2, getY() + getHeight()/2);
    }
    public boolean isPendingRespawn() {
        return pendingRespawn;
    }

    private Vector2 getRandomDirection() {
        float angle = MathUtils.random(360f);
        return new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).nor();
    }

    private void updateAnimation(float delta, Vector2 velocity) {
        if (velocity.len2() < 0.0001f) {
            currentFrame = walkDown.getKeyFrame(0);
            return;
        }

        if (Math.abs(velocity.x) > Math.abs(velocity.y)) {
            if (velocity.x > 0) {
                direction = Direction.EAST;
                currentFrame = walkRight.getKeyFrame(stateTimer, true);
                if (currentFrame.isFlipX()) currentFrame.flip(true, false);
            } else {
                direction = Direction.WEST;
                currentFrame = walkRight.getKeyFrame(stateTimer, true);
                if (!currentFrame.isFlipX()) currentFrame.flip(true, false);
            }
        } else {
            if (velocity.y > 0) {
                direction = Direction.NORTH;
                currentFrame = walkUp.getKeyFrame(stateTimer, true);
                if (currentFrame.isFlipX()) currentFrame.flip(true, false);
            } else {
                direction = Direction.SOUTH;
                currentFrame = walkDown.getKeyFrame(stateTimer, true);
                if (currentFrame.isFlipX()) currentFrame.flip(true, false);
            }
        }
    }

    @Override
    public void draw(Batch batch) {
        if (pendingRespawn) {
            return;
        }
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }

    public void onPlayerCollision() {
        if (body != null) {
            world.destroyBody(body);
            body = null;
        }
        pendingRespawn = true;
    }
}
