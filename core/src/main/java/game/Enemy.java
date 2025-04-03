package game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.MathUtils;

public class Enemy extends Sprite {
    private World world;
    private Body body;
    private GameScreen screen;

    private float stateTimer;
    private static final float CHASEINTERVAL = 15f;
    private boolean isChasing;
    private float chaseTimer;
    private static final float CHASEDURATION = 5f;

    private Vector2 scatterDirection;
    private final Vector2 spawnPos;

    public Enemy(World world, GameScreen screen, Texture texture, Vector2 spawnPos) {
        super(texture);
        this.world = world;
        this.screen = screen;
        this.stateTimer = 0f;
        this.chaseTimer = 0f;
        this.isChasing = false;
        this.scatterDirection = getRandomDirection();
        this.spawnPos = spawnPos;

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
        body.createFixture(fixtureDef).setUserData(this);
        shape.dispose();
    }

    private Vector2 getRandomValidSpawn() {
        float mapWidth = screen.getMapWidth();
        float mapHeight = screen.getMapHeight();
        Vector2 pos = new Vector2();
        boolean valid = false;
        int attempts = 0;
        while (!valid && attempts < 100) {
            pos.x = MathUtils.random(0, mapWidth);
            pos.y = MathUtils.random(0, mapHeight);
            if (screen.isPositionValid(pos.x, pos.y)) {
                valid = true;
            }
            attempts++;
        }
        if (!valid) {
            pos.set(mapWidth / 2, mapHeight / 2);
        }
        return pos;
    }

    public Body getBody() {
        return body;
    }

    private Vector2 getRandomDirection() {
        float angle = MathUtils.random(360);
        return new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).nor();
    }

    public void update(float delta, Vector2 playerPosition) {
        stateTimer += delta;

        if (!isChasing && stateTimer >= CHASEINTERVAL) {
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

            if (chaseTimer >= CHASEDURATION) {
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
        world.destroyBody(body);
        createBody(this.spawnPos);
        stateTimer = 0f;
        isChasing = false;
        scatterDirection = getRandomDirection();
    }
}

