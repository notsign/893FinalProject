package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Kevin on 13/05/2016.
 */

// TODO: Extend off entity somehow

public class Bullet {
	Body body;
	Fixture bodyFixture;
	World world;

	boolean hasContacted;

	protected Bullet(World world, Vector2 playerpos, boolean facingRight) {
		this.world = world;
		setBodyDef(playerpos);
		setFixtureDef();
		setVelocity(facingRight);

		body.setUserData("bullet");

		hasContacted = false;

		Filter filter = new Filter();
		filter.categoryBits = 4;
		filter.groupIndex = -1;
		filter.maskBits = 1 | 8; // Ground and enemy
		bodyFixture.setFilterData(filter);
	}

	private void setBodyDef(Vector2 playerpos) {
		BodyDef bulletdef = new BodyDef();
		bulletdef.position.set(playerpos);
		bulletdef.type = BodyDef.BodyType.DynamicBody;
		//bulletdef.bullet = true;
		body = world.createBody(bulletdef);
		body.setGravityScale(0f);
		body.setSleepingAllowed(false);
		body.setFixedRotation(true);
	}

	private void setFixtureDef() {
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(2f, 2f);
		FixtureDef bulletfdef = new FixtureDef();
		bulletfdef.shape = shape;
		bulletfdef.filter.groupIndex = -1;
		body.setUserData("bullet");
		// group index explanation:
		// Collision groups let you specify an integral group index.
		// You can have all fixtures with the same group index always collide (positive index)
		// or never collide (negative index).

		// basically you put everything that you want together in a group, and decide whether or not you want
		// them all to collide or not by using a positive or negative hexadecimal or integer
		// works similar to a category bit but far more general
		bodyFixture = body.createFixture(bulletfdef);
		shape.dispose();
	}

	private void setVelocity(boolean facingRight) {
		if (facingRight) {
			body.setLinearVelocity(500000, 0);
		} else {
			body.setLinearVelocity(-500000, 0);
		}
	}
}
