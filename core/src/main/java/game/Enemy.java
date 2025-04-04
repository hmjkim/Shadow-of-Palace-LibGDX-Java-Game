package game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.MathUtils;

public class Enemy extends Sprite {
    private final World world;
    private Body body;

    private float stateTimer;
    private static final float CONSTANT_SPEED = 1.0f;
    private boolean isChasing;
    private float chaseTimer;
    private static final float CHASE_DURATION = 5f;
    private static final float CHASE_INTERVAL = 15f;

    private Vector2 scatterDirection;
    private final Vector2 spawnPos;

    private boolean pendingRespawn;
    private static final float MIN_RESPAWN_DISTANCE = 5.0f;

    public Enemy(World world, Texture texture, Vector2 spawnPos) {
        super(texture);
        this.world = world;
        this.stateTimer = 0f;
        this.chaseTimer = 0f;
        this.isChasing = false;
        this.scatterDirection = getRandomDirection();
        this.spawnPos = spawnPos;
        this.pendingRespawn = false;

        setSize(getTexture().getWidth() / MainGame.PIXELS_PER_METER,
            getTexture().getHeight() / MainGame.PIXELS_PER_METER);
        createBody(spawnPos);
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

    public void update(float delta, Vector2 playerPosition) {
        if (pendingRespawn) {
            if (playerPosition.dst(spawnPos) >= MIN_RESPAWN_DISTANCE) {
                createBody(spawnPos);
                pendingRespawn = false;
                stateTimer = 0f;
                isChasing = false;
                scatterDirection = getRandomDirection();
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
            body.setLinearVelocity(direction.scl(1.0f));
            if (chaseTimer >= CHASE_DURATION) {
                isChasing = false;
                stateTimer = 0f;
                scatterDirection = getRandomDirection();
            }
        } else {
            if (stateTimer >= 3f) {
                scatterDirection = getRandomDirection();
                stateTimer = 0f;
            }
            direction.set(scatterDirection);
            body.setLinearVelocity(direction.scl(1.0f));
        }

        setPosition(body.getPosition().x - getWidth() / 2,
            body.getPosition().y - getHeight() / 2);
    }

    public void onPlayerCollision() {
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
        super.draw(batch);
    }
}

