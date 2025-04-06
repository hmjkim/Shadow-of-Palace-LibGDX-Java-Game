package game;

import Sprites.Direction;
import Sprites.FrameRegionDimensions;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

public class Enemy extends Sprite {
    private final World world;
    private Body body;

    private float stateTimer;
    private static final float CONSTANT_SPEED = 1.0f;
    private boolean isChasing;
    private float chaseTimer;
    private final GameScreen screen;
    private static final float CHASE_DURATION = 5f;
    private static final float CHASE_INTERVAL = 15f;

    private Vector2 scatterDirection;
    private final Vector2 spawnPos;

    private boolean pendingRespawn;
    private static final float MIN_RESPAWN_DISTANCE = 5.0f;

    private final FrameRegionDimensions dimensions;

    // Animations
    private TextureRegion currentFrame;
    private Animation<TextureRegion> walkUp;
    private Animation<TextureRegion> walkRight;
    private Animation<TextureRegion> walkDown;
    private Direction direction;



    public Enemy(World world, GameScreen screen, TextureAtlas atlas, Vector2 spawnPos, String regionName, FrameRegionDimensions dimensions) {
        super(atlas.findRegion(regionName));
        this.world = world;
        this.stateTimer = 0f;
        this.chaseTimer = 0f;
        this.screen = screen;
        this.isChasing = false;
        this.scatterDirection = getRandomDirection();
        this.spawnPos = spawnPos;
        this.pendingRespawn = false;
        this.direction = Direction.SOUTH;
        this.dimensions = dimensions;

        // Enemy animation setup
        setupAnimation(atlas.findRegion(regionName));


        // Set sprite size using dynamic dimensions and converting to world units
        setSize(dimensions.getFrameWidth() / MainGame.PIXELS_PER_METER,
            dimensions.getFrameHeight() / MainGame.PIXELS_PER_METER);
        createBody(spawnPos);
        createBody(spawnPos);
    }

    private void setupAnimation(TextureRegion region) {
        int columns = 3;

        Array<TextureRegion> framesUp = new Array<>();
        Array<TextureRegion> frameRight = new Array<>();
        Array<TextureRegion> framesDown = new Array<>();

        // Loop through each column to extract the frames based on dynamic dimensions
        for (int col = 0; col < columns; col++) {
            int x = region.getRegionX() + col * dimensions.getFrameWidth();

            // Row 0: Up
            int yUp = region.getRegionY();
            framesUp.add(new TextureRegion(region.getTexture(), x, yUp, dimensions.getFrameWidth(), dimensions.getFrameHeight()));

            System.out.println("width" + dimensions.getFrameWidth() + ", height" + dimensions.getFrameHeight());
            // Row 1: Right
            int yRight = region.getRegionY() + dimensions.getFrameHeight();
            frameRight.add(new TextureRegion(region.getTexture(), x, yRight, dimensions.getFrameWidth(), dimensions.getFrameHeight()));

            // Row 2: Down
            int yDown = region.getRegionY() + dimensions.getFrameHeight() * 2;
            framesDown.add(new TextureRegion(region.getTexture(), x, yDown, dimensions.getFrameWidth(), dimensions.getFrameHeight()));
        }

        walkUp = new Animation<>(0.15f, framesUp, Animation.PlayMode.LOOP);
        walkRight = new Animation<>(0.15f, frameRight, Animation.PlayMode.LOOP);
        walkDown = new Animation<>(0.15f, framesDown, Animation.PlayMode.LOOP);

        // Set a default frame (standing down)
        currentFrame = walkDown.getKeyFrame(0);
    }


    private void createBody(Vector2 spawnPos) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(spawnPos);
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

    private Vector2 getRandomDirection() {
        float angle = MathUtils.random(360);
        return new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).nor();
    }

    private Vector2 chooseFreeDirection() {
        List<Vector2> freeDirections = new ArrayList<>();
        int numSamples = 4;
        final float rayDistance = 1.0f;
        Vector2 start = body.getPosition();

        for (int i = 0; i < numSamples; i++) {
            float angle = MathUtils.random(360);
            Vector2 candidate = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).nor();

            Vector2 end = start.cpy().add(candidate.cpy().scl(rayDistance));

            final boolean[] blocked = {false};
            RayCastCallback callback = (fixture, point, normal, fraction) -> {
                if (fixture.getBody().getType() == BodyDef.BodyType.StaticBody) {
                    blocked[0] = true;
                    return 0;
                }
                return 1;
            };
            world.rayCast(callback, start, end);
            if (!blocked[0]) {
                freeDirections.add(candidate);
            }
        }
        if (!freeDirections.isEmpty()) {
            return freeDirections.get(MathUtils.random(freeDirections.size() - 1));
        }
        return getRandomDirection();
    }

    public void update(float delta, Vector2 playerPosition) {
        if (pendingRespawn) {
            if (playerPosition.dst(spawnPos) >= MIN_RESPAWN_DISTANCE) {
                createBody(spawnPos);
                pendingRespawn = false;
                stateTimer = 0f;
                isChasing = false;
                scatterDirection = chooseFreeDirection();
            } else {
                return;
            }
        }

        stateTimer += delta;

        if (!isChasing && stateTimer >= CHASE_INTERVAL) {
            isChasing = true;
            chaseTimer = 0f;
        }
        Vector2 direction = new Vector2();

        if (isChasing) {
            chaseTimer += delta;
            direction.set(playerPosition).sub(body.getPosition());
            if (direction.len() < 0.001f) {
                direction.set(scatterDirection);
            } else {
                direction.nor();
            }
            body.setLinearVelocity(direction.scl(CONSTANT_SPEED));
            if (chaseTimer >= CHASE_DURATION) {
                isChasing = false;
                stateTimer = 0f;
                scatterDirection = chooseFreeDirection();
            }
        } else {
            if (stateTimer >= 3f) {
                scatterDirection = chooseFreeDirection();
                stateTimer = 0f;
            }
            direction.set(scatterDirection);
            body.setLinearVelocity(direction.scl(CONSTANT_SPEED));
        }

        setPosition(body.getPosition().x - getWidth() / 2,
            body.getPosition().y - getHeight() / 2);
    }

    public void onPlayerCollision() {
        if (screen.getPlayer().isInvincible()) {
            return;
        }

        if (body != null && !pendingRespawn) {
            world.destroyBody(body);
            body = null;
            pendingRespawn = true;
        }
    }

    @Override
    public void draw(Batch batch) {
        if (pendingRespawn) {
            return;
        }
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }
}

