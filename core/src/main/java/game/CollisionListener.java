package game;

import Sprites.Player;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

import java.util.Objects;

///**
// * Handles collision events between player and enemy objects.
// *
// * @author Zoey Lee, Heather Kim
// * @version 2025
// */
public class CollisionListener implements ContactListener {
    private final GameScreen screen;

    public CollisionListener(final GameScreen screen) {
        this.screen = screen;
    }

    @Override
    public void beginContact(Contact contact) {
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

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
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

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
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
