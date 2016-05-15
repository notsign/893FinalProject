package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

/**
 * Created by Rueban Rasaselvan on 03/04/2016.
 */

// K: For what it's doing, this should really be called something along the lines of 'EnemyManager'

public class EnemySpawner {
	World world;
	Vector2 position;
	//can be set to spawn any number of enemies
	//not able to set custom number of enemies for each spawner yet
	public ArrayList<FastEnemy> fastEnemies;
	int elapsedtime = 0;

	EnemySpawner(World world, Vector2 position) {
		this.world = world;
		this.position = position;

		fastEnemies = new ArrayList<FastEnemy>();
	}

	void createEnemy() {
		fastEnemies.add(new FastEnemy(world, position));
	}

	void draw(SpriteBatch spriteBatch) {
		//only the enemies that have already been spawned in are updated
		for (FastEnemy fastEnemy : fastEnemies) {
			fastEnemy.draw(spriteBatch);
		}
	}

	public void update(Vector2 playerpos) {
		float playerDist = playerpos.dst(position);
		//only if the player enters within the region of radius 500 from the spawner block
		//then the enemies will spawn.
		elapsedtime++;
		if (playerDist <= 500f) {
			//every 5 seconds a new enemy is spawned
			if (elapsedtime >= 50) {
				elapsedtime = 0;
				if (fastEnemies.size() < 5)
					createEnemy();
			}
		}

		for (FastEnemy fastEnemy : fastEnemies) {
			fastEnemy.move(playerpos.x);
		}
	}
}


