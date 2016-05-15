package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Rueban Rasaselvan on 30/03/2016.
 */

//more types of enemies will be added
public class FastEnemy extends Entity {
	boolean isAlive;

    FastEnemy(World world, Vector2 spawnposition) {
        super(world, spawnposition, "player"); // No sprite for enemy yet :c

        body.setUserData("fast_enemy");
		isAlive = true;

		Filter filter = new Filter();
		filter.categoryBits = 8;
		filter.groupIndex = -2;
		filter.maskBits = 1 | 2 | 4; // Ground, player and bullets
		fixture.setFilterData(filter);
    }

    void move(float fPlayerX) {
		// Causes enemies to jump all the time, not worth fixing as Don will update with his enemy
		// AI soon.
        //if (Math.abs(body.getLinearVelocity().x) < 0.1f) {
        //    jump();
        //    isIdle = true;
        //}

        if (fPlayerX < body.getPosition().x) {
            body.setLinearVelocity(-50f, body.getLinearVelocity().y);
            bRight = false;
            isIdle = false;
        } else if (fPlayerX > body.getPosition().x) {
            body.setLinearVelocity(50f, body.getLinearVelocity().y);
            bRight = true;
            isIdle = false;
        }
    }
}
