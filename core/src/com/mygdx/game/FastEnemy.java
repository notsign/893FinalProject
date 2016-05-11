package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Rueban Rasaselvan on 30/03/2016.
 */

//more types of enemies will be added
public class FastEnemy extends Entity {
    FastEnemy(World world, Vector2 spawnposition) {
        super(world, spawnposition, "player"); // No sprite for enemy yet :c

		Filter filter = new Filter();
		filter.categoryBits = 8;
		filter.maskBits = 1 | 2;
		bodyFixture.setFilterData(filter);
    }

    void move(float fPlayerX) {
        if (body.getLinearVelocity().x == 0) {
            body.applyLinearImpulse(0, 50, body.getPosition().x, body.getPosition().y, true);
            isIdle = true;
            bRight = false;
        }
        if (fPlayerX < body.getPosition().x) {
            body.setLinearVelocity(-50, body.getLinearVelocity().y);
            bRight = false;
            isIdle = false;
        } else if (fPlayerX > body.getPosition().x) {
            body.setLinearVelocity(50, body.getLinearVelocity().y);
            bRight = true;
            isIdle = false;
        }
    }
}
