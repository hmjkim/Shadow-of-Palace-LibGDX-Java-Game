package Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import game.MainGame;

/**
 * The Player class represents the player character.
 * It creates a dynamic Box2D body for collision and physics,
 * loads the player image, and updates its sprite position accordingly.
 */
public class Player extends Sprite {
    private World world;
    private Body body;
    private final Texture texture;

    /**
     * Constructs the player, loads the image, and creates its Box2D body.
     *
     * @param world The Box2D world to which the player's body is added.
     */
    public Player(World world) {
        this.world = world;
        // --- Load Player Image ---
        // Load the player's texture. Ensure "player.png" is in your assets folder.
        texture = new Texture("player.png");
        // Set the texture as the sprite region.
        setRegion(texture);
        // Set sprite size based on conversion from pixels to Box2D world units.
        setSize(texture.getWidth() / MainGame.PIXELS_PER_METER, texture.getHeight() / MainGame.PIXELS_PER_METER);
        // Set the origin for rotations and scaling.
        setOriginCenter();
        // --- Create the Player's Physics Body ---
        definePlayer();
    }

    /**
     * Creates the Box2D dynamic body for the player, including collision shape and fixture.
     * Sets the starting position to (100, 100) pixels (converted to world units).
     */
    private void definePlayer() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        // Set the initial position (convert from pixels to world units).
        bdef.position.set(100 / MainGame.PIXELS_PER_METER, 100 / MainGame.PIXELS_PER_METER);
        body = world.createBody(bdef);

        // --- Define Collision Shape ---
        // Create a circular shape for the player's collision.
        CircleShape shape = new CircleShape();
        shape.setRadius(10 / MainGame.PIXELS_PER_METER);

        // Create a fixture definition with density and friction.
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1f;
        fdef.friction = 0.5f;
        body.createFixture(fdef);
        // Dispose the shape after use.
        shape.dispose();

        // --- Add Linear Damping ---
        // This helps the player slow down when no input is given.
        body.setLinearDamping(3f);
    }

    /**
     * Updates the sprite's position to follow its Box2D body.
     *
     * @param delta The time elapsed since the last frame.
     */
    public void update(float delta) {
        // Center the sprite on the Box2D body's position.
        setPosition(
            body.getPosition().x - getWidth() / 2,
            body.getPosition().y - getHeight() / 2
        );
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

    /**
     * Dispose of the player's texture.
     */
    public void dispose() {
        texture.dispose();
    }
}
