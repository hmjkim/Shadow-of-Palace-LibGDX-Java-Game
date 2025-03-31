package game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Enemy extends Sprite {
    private final World world;
    private Body body;
    private float stateTimer;
    private final GameScreen screen;

    public Enemy(World world, GameScreen screen, String imageFile) {
        this.screen = screen;
        Texture npc = new Texture(imageFile);
        super.setTexture(npc);
        this.world = world;
        stateTimer = 0f;

        setSize(npc.getWidth() / MainGame.PIXELS_PER_METER,
            npc.getHeight() / MainGame.PIXELS_PER_METER);
        createNPC();
    }

    private void createNPC() {
        BodyDef bodydef = new BodyDef();
        bodydef.type = BodyDef.BodyType.DynamicBody;
        float mapWidth = screen.getMapWidth();
        float mapHeight = screen.getMapHeight();
        float randomX = (float)(Math.random() * mapWidth);
        float randomY = (float)(Math.random() * mapHeight);
        bodydef.position.set(randomX, randomY);
        body = world.createBody(bodydef);

        CircleShape shape = new CircleShape();
        shape.setRadius(10 / MainGame.PIXELS_PER_METER);

        FixtureDef fixturedef = new FixtureDef();
        fixturedef.shape = shape;
        fixturedef.density = 1f;
        fixturedef.friction = 0.5f;
        body.createFixture(fixturedef).setUserData(this);
        shape.dispose();
    }

    public void update(float delta) {
        stateTimer += delta;
        if (stateTimer >= 2f) {
            float randomAngle = (float) (Math.random() * 360);
            Vector2 velocity = new Vector2((float) Math.cos(Math.toRadians(randomAngle)),
                (float) Math.sin(Math.toRadians(randomAngle)));
            velocity.scl(1f);
            body.setLinearVelocity(velocity);
            stateTimer = 0;
        }
        setPosition(body.getPosition().x - getWidth() / 2,
            body.getPosition().y - getHeight() / 2);
    }

    public Body getBody() {
        return body;
    }

    public void onPlayerCollision() {
        world.destroyBody(body);
        createNPC();
    }
}
