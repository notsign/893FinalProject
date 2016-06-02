package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Rueban Rasaselvan on 30/03/2016.
 */

//more types of enemies will be added
public class FastEnemy extends CharacterEntity {
	Player player; // Our own reference to the player
	boolean isAlive;

	FastEnemy(World world, Player player, Vector2 spawnposition) {
		super(world, spawnposition, "player"); // No sprite for enemy yet :c

		this.player = player;

		isAlive = true;

		Filter filter = new Filter();
		filter.categoryBits = 8;
		filter.groupIndex = -2;
		filter.maskBits = 1 | 2 | 4; // Collide with terrain, player and bullets
		fixture.setFilterData(filter);

		Filter footSensorFilter = new Filter();
		filter.maskBits = 1 | 2; // Jump off terrain and player
		footSensor.setFilterData(footSensorFilter);
	}

	public void update() {
		if (player.body.getPosition().x < body.getPosition().x) {
			body.setLinearVelocity(-50f, body.getLinearVelocity().y);
			bRight = false;
			isIdle = false;
		} else if (player.body.getPosition().x > body.getPosition().x) {
			body.setLinearVelocity(50f, body.getLinearVelocity().y);
			bRight = true;
			isIdle = false;
		}
	}

	public boolean shouldBeDestroyed(){
		return !isAlive;
	}
}
