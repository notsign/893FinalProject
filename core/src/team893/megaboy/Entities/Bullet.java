package team893.megaboy.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

public class Bullet implements Entity {
	Body body;
	Fixture fixture;
	World world;
	Entity owner;

	public boolean hasContacted;

	protected Bullet(World world, Entity owner, Vector2 position, boolean facingRight) {
		this.world = world;
		this.owner = owner;

		createBody(position);
		createFixture();
		setVelocity(facingRight);

		hasContacted = false;

		Filter filter = new Filter();
		filter.categoryBits = 4;
		filter.groupIndex = -1;
		filter.maskBits = 1 | 8; // Ground and enemy
		fixture.setFilterData(filter);
	}

	private void createBody(Vector2 playerpos) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(playerpos);
		bodyDef.fixedRotation = true;
		bodyDef.allowSleep = false;
		bodyDef.gravityScale = 0f;
		bodyDef.type = BodyDef.BodyType.DynamicBody;

		body = world.createBody(bodyDef);
	}

	private void createFixture() {
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(2f, 2f);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.filter.groupIndex = -1;
		// group index explanation:
		// Collision groups let you specify an integral group index.
		// You can have all fixtures with the same group index always collide (positive index)
		// or never collide (negative index).

		fixture = body.createFixture(fixtureDef);
		shape.dispose();

		fixture.setUserData(this);
	}

	private void setVelocity(boolean facingRight) {
		if (facingRight) {
			body.setLinearVelocity(1000, 0);
		} else {
			body.setLinearVelocity(-1000, 0);
		}
	}

	public void update(){

	}

	public void render(SpriteBatch spriteBatch){

	}

	public boolean shouldBeDestroyed(){
		return hasContacted;
	}

	public Body getBody(){
		return body;
	}

	public void destroy(){
		world.destroyBody(body);
	}
}
