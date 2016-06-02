package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by k9sty on 2016-03-12.
 * K: Doesn't matter, I did most of it :)
 */

public class Player extends CharacterEntity {
	final static int MAX_HEALTH = 3;
	int health;
	int bulletCooldown;
	List<Entity> entityBuffer;

	Player(World world, Vector2 position, List<Entity> entityBuffer) {
		super(world, position, "player");

		this.entityBuffer = entityBuffer;

		health = MAX_HEALTH;
		bulletCooldown = 0;

		Filter filter = new Filter();
		filter.categoryBits = 2;
		filter.maskBits = 1 | 8; // Collide with terrain and enemy
		fixture.setFilterData(filter);

		Filter footSensorFilter = new Filter();
		footSensorFilter.maskBits = 1 | 8; // Jump off terrain and enemy
		footSensor.setFilterData(footSensorFilter);
	}

	public void update() {
		if (Gdx.app.getType() == Application.ApplicationType.Android) {
			if (Gdx.input.isTouched()) {
				int screenHeight = Gdx.graphics.getHeight();
				int screenWidth = Gdx.graphics.getWidth();
				int touchX = Gdx.input.getX();
				int touchY = Gdx.input.getY(); // Don't get near this guy ;}

				if (touchX > screenWidth - (screenWidth / 3) && touchY > screenHeight - (screenHeight / 3f)) { // Bottom right, move right
					body.setLinearVelocity(100f, body.getLinearVelocity().y);
					bRight = true;
					isIdle = false;
				} else if (touchX < (screenWidth / 3f) && touchY > screenHeight - (screenHeight / 3f)) { // Bottom left, move left
					body.setLinearVelocity(-100f, body.getLinearVelocity().y);
					bRight = false;
					isIdle = false;
				} else if (isGrounded && touchY > screenHeight - (screenHeight / 3)) { // Bottom middle, jump
					jump();
				} else if (touchX > screenWidth - (screenWidth / 3) && touchY < (screenHeight / 3f)) { // Top right, shoot right
					entityBuffer.add(new Bullet(world, this, body.getPosition(), true));
				} else if (touchX < (screenWidth / 3) && touchY < (screenHeight / 3f)) { // Top right, shoot right
					entityBuffer.add(new Bullet(world, this, body.getPosition(), false));
				} else { // Not tapping anywhere important
					isIdle = true;
					stop();
				}
			} else { // Not tapping anywhere
				isIdle = true;
				stop();
			}
		} else if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
			if (isGrounded && Gdx.input.isKeyPressed(Input.Keys.Z))
				jump();

			if (bulletCooldown <= 0 && Gdx.input.isKeyPressed(Input.Keys.X)) {
				entityBuffer.add(new Bullet(world, this, body.getPosition(), bRight));
				bulletCooldown = 30; // 1/2 second cooldown
			} else {
				bulletCooldown--;
			}

			if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				bRight = false;
				isIdle = false;
				body.setLinearVelocity(-100, body.getLinearVelocity().y);
			} else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				bRight = true;
				isIdle = false;
				body.setLinearVelocity(100, body.getLinearVelocity().y);
			} else {
				isIdle = true;
				stop();
			}
		}
	}

	public boolean shouldBeDestroyed() {
		return health <= 0;
	}

	public void jump() {
		jump(500f);
	}
}
