package game;

import Sprites.Player;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

import java.util.Objects;

/**
 * Handles collision events between player and enemy objects.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public class CollisionListener implements ContactListener {
    private final GameScreen screen;

    /**
     * Constructs a Student with the specified screen.
     *
     * @param screen the game screen to notify for enemy removal actions
     */
    public CollisionListener(final GameScreen screen) {
        this.screen = screen;
    }

    /**
     * Detects the beginning of a collision.
     *
     * @param contact the collision contact between two fixtures
     */
    @Override
    public void beginContact(final Contact contact) {
        Object userDataA = contact.getFixtureA().getUserData();
        Object userDataB = contact.getFixtureB().getUserData();

        if (userDataA instanceof Sprites.Player && userDataB instanceof Enemy) {
            Player player = (Player) userDataA;
            if (!player.isInvincible()) {
                player.hitByNPC();
                screen.scheduleEnemyRemoval((Enemy) userDataB);
                ((Enemy) userDataB).onPlayerCollision();
            }
        } else if (userDataB instanceof Sprites.Player && userDataA instanceof Enemy) {
            Player player = (Player) userDataB;
            if (!player.isInvincible()) {
                player.hitByNPC();
                screen.scheduleEnemyRemoval((Enemy) userDataA);
                ((Enemy) userDataA).onPlayerCollision();
            }
        }
    }

    /**
     * Processes the end of a collision.
     *
     * @param contact the collision contact between two fixtures
     */
    @Override
    public void endContact(final Contact contact) {
    }

    /**
     * Evaluates the collision before it is solved.
     *
     * @param contact the collision contact between two fixtures
     * @param oldManifold the previous collision manifold
     */
    @Override
    public void preSolve(final Contact contact, final Manifold oldManifold) {
        Object userDataA = contact.getFixtureA().getUserData();
        Object userDataB = contact.getFixtureB().getUserData();

        if (userDataA instanceof Sprites.Player && userDataB instanceof Enemy) {
            Sprites.Player player = (Sprites.Player) userDataA;
            if (player.isInvincible()) {
                contact.setEnabled(false);
            }
        } else if (userDataB instanceof Sprites.Player && userDataA instanceof Enemy) {
            Sprites.Player player = (Sprites.Player) userDataB;
            if (player.isInvincible()) {
                contact.setEnabled(false);
            }
        }
    }

    /**
     * Processes the collision after it is solved.
     *
     * @param contact the collision contact between two fixtures
     * @param impulse the impulse applied during collision resolution
     */
    @Override
    public void postSolve(final Contact contact, final ContactImpulse impulse) {
    }

    /**
     * Determines if the CollisionListener is equal to another object.
     *
     * @param obj the object to compare
     * @return true if the CollisionListener is equal to the other object, false otherwise
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

        CollisionListener collisionListener = (CollisionListener) obj;
        return Objects.equals(this.screen, collisionListener.screen);
    }

    /**
     * Gets the hashcode of the CollisionListener.
     *
     * @return the hashcode of the CollisionListener as an integer
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(this.screen);
    }

    /**
     * Gets the string representation of the CollisionListener.
     *
     * @return the string representation of the CollisionListener
     */
    @Override
    public String toString() {
        final StringBuilder sb;
        sb = new StringBuilder("CollisionListener{");
        sb.append("screen='").append(this.screen);
        sb.append('}');
        return sb.toString();
    }
}
