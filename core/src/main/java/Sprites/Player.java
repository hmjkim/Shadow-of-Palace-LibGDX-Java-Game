package Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import game.GameScreen;
import game.MainGame;
import com.badlogic.gdx.utils.Array;

/**
 * The Player class represents the player character.
 * It creates a dynamic Box2D body for collision and physics,
 * loads the player image, and updates its sprite position accordingly.
 */
public class Player extends Sprite {
    private State currentState;
    private State previousState;
    private World world;
    private Body body;

    // private final Texture texture;
    // Animation
    private TextureRegion playerStand;
    private Animation  playerWalk;
    private float stateTimer;
    private char direction;

    /**
     * Constructs the player, loads the image, and creates its Box2D body.
     *
     * @param world The Box2D world to which the player's body is added.
     */
    public Player(World world, GameScreen screen) {
        super(screen.getAtlas().findRegion("gwi_in"));
        this.world = world;
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        direction = 'E';
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

        // --- Sprite Sheet Parameters ---
        // These parameters define the dimensions and gaps for your frames.
        int frameWidth = 16;    // Each frame is 16 pixels wide.
        int frameHeight = 24;   // Each frame is 24 pixels tall.
        int gapX = 8;           // 8 pixel gap horizontally between frames.
        int gapY = 8;           // 8 pixel gap vertically between frames (if applicable).
        int startX = 0;         // X offset for the first frame.
        int startY = 0;        // Y offset for the first frame.


        // --- Extract Walking Animation Frames ---
        Array<TextureRegion> frames = new Array<TextureRegion>();
        // Assuming you have 3 walking frames.
        for (int i = 0; i < 3; i++) {
            // Calculate x offset: startX + i * (frameWidth + gapX)
            int frameX = startX + i * (frameWidth + gapX);
            frames.add(new TextureRegion(getTexture(), frameX, frameHeight + gapY, frameWidth, frameHeight));
        }
        playerWalk = new Animation(0.1f, frames);
        frames.clear();

        // --- Define Standing Frame ---
        playerStand = new TextureRegion(getTexture(), frameWidth + gapX, (frameHeight + gapY) * 2, frameWidth, frameHeight);

        // --- Set Sprite Bounds ---
        setBounds(frameWidth + gapX, (frameHeight + gapY) * 2, frameWidth / MainGame.PIXELS_PER_METER, frameHeight / MainGame.PIXELS_PER_METER);
        setRegion(playerStand);

//        Array<TextureRegion> frames = new Array<TextureRegion>();
//        for (int i = 0; i < 3; i++) {
//            frames.add(new TextureRegion(getTexture(), i * 0, 32, 16, 24));
//        }
//        playerWalk = new Animation(0.1f, frames);
//        frames.clear();
//        playerStand = new TextureRegion(getTexture(), 24, 64, 17, 24);
//        setBounds(25, 67, 16/ MainGame.PIXELS_PER_METER, 24 / MainGame.PIXELS_PER_METER);
//        setRegion(playerStand);
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
        shape.setRadius(15 / MainGame.PIXELS_PER_METER);

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
//    public void update(float delta) {
//        // Center the sprite on the Box2D body's position.
//        setPosition(
//            body.getPosition().x - getWidth() / 2,
//            body.getPosition().y - getHeight() / 2
//        );
//        //update sprite with the correct frame depending on players current action
//        setRegion(getFrame(delta));
//    }
    public void update(float delta) {
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
        setRegion(getFrame(delta));
    }

    public TextureRegion getFrame(float delta) {
        //get player current state. (walking, standing)
        currentState = getState();

        TextureRegion region;
        //depending on the state, get corresponding animation keyFrame.
        switch(currentState){
            case WALKING:
                region = (TextureRegion) playerWalk.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            default:
                region = playerStand;
                break;
        }

        //if player is running left and the texture isnt facing left... flip it.
        if((body.getLinearVelocity().x < 0 || direction != 'E') && !region.isFlipX()){
            region.flip(true, false);
            direction = 'W';
        }

        //if player is running right and the texture isnt facing right... flip it.
        else if((body.getLinearVelocity().x > 0 || direction == 'E') && region.isFlipX()){
            region.flip(true, false);
            direction = 'E';
        }
        //return our final adjusted frame
        return region;
    }

    public void setDirection(Direction dir) {
        this.direction = dir;
    }


    public State getState() {
        //if player is positive or negative in the X axis, she is walking
        if(body.getLinearVelocity().x != 0)
            return State.WALKING;
            //if none of these return then she must be standing
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

//    /**
//     * Dispose of the player's texture.
//     */
//    public void dispose() {
//        texture.dispose();
//    }
}
