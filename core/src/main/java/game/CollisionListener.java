package game;

import Sprites.Player;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.*;

public class CollisionListener implements ContactListener {
    private final GameScreen screen;

    public CollisionListener(GameScreen screen) {
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
                ((Enemy) userDataA).onPlayerCollision();
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
}
