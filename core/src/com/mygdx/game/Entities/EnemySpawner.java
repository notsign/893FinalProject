package com.mygdx.game.Entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import java.util.List;

/**
 * Created by Rueban Rasaselvan on 03/04/2016.
 */

public class EnemySpawner implements Entity {
	World world;
	Vector2 position;
	List<Entity> entityBuffer; // Reference to the main entityList
	int spawnTimer;
	int maxEnemies;
	com.mygdx.game.Entities.Player player;

	public EnemySpawner(World world, List<Entity> entityBuffer, com.mygdx.game.Entities.Player player, Vector2 position, int maxEnemies) {
		this.world = world;
		this.entityBuffer = entityBuffer;
		this.player = player;
		this.position = position;
		this.maxEnemies = maxEnemies;
		spawnTimer = 0;
	}

	void createEnemy() {
		entityBuffer.add(new com.mygdx.game.Entities.FastEnemy(world, player, position));
	}

	public void render(SpriteBatch spriteBatch) {

	}

	public void update() {
		float playerDist = position.dst(player.getPosition());
		// Only if the player enters within the region of radius 500 from the spawner block
		// then the enemies will spawn.
		spawnTimer++;
		if (playerDist <= 500f) {
			//every second a new enemy is spawned
			if (spawnTimer >= 60) {
				spawnTimer = 0;
				createEnemy();
			}
		}
	}

	public boolean shouldBeDestroyed(){
		return false; //lol
	}

	public void destroy(){
		//lol
	}

	public Body getBody(){
		return null;
	}
}
