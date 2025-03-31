package game;

import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.*;

public class CollisionListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        Object userDataA = fixA.getUserData();
        Object userDataB = fixB.getUserData();

        if (userDataA instanceof Sprites.Player && userDataB instanceof Enemy) {
            ((Sprites.Player) userDataA).hitByNPC();
            ((Enemy) userDataB).onPlayerCollision();
        } else if (userDataB instanceof Sprites.Player && userDataA instanceof Enemy) {
            ((Sprites.Player) userDataB).hitByNPC();
            ((Enemy) userDataA).onPlayerCollision();
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
