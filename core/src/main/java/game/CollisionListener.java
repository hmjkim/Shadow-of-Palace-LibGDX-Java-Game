package game;

import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.*;

public class CollisionListener implements ContactListener {
    private GameScreen screen;

    public CollisionListener(GameScreen screen) {
        this.screen = screen;
    }
    @Override
    public void beginContact(Contact contact) {
        Object userDataA = contact.getFixtureA().getUserData();
        Object userDataB = contact.getFixtureB().getUserData();

        if (userDataA instanceof Sprites.Player && userDataB instanceof Enemy) {
            ((Sprites.Player) userDataA).hitByNPC();
            screen.scheduleEnemyRemoval((Enemy) userDataB);
        } else if (userDataB instanceof Sprites.Player && userDataA instanceof Enemy) {
            ((Sprites.Player) userDataB).hitByNPC();
            screen.scheduleEnemyRemoval((Enemy) userDataA);
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
