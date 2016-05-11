package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by k9sty on 2016-03-12.
 */

public class Entity {
    Body body;
    Fixture bodyFixture, footSensor;
    Animation aniIdle, aniRun;
	World world;

    float elapsedTime;
    boolean bRight;
    boolean isIdle;
    boolean isGrounded;

    Entity(World world, Vector2 position, String spriteLocation) {
        this.world = world;

		elapsedTime = 0f;
		bRight = true;
		isIdle = true;
		isGrounded = false; // Physics will call beginContact if the entity spawns on the ground

        createBody(position, spriteLocation);
        createFootSensor();
    }

    private void createBody(Vector2 position, String spriteLocation) {
        isIdle = true;
        TextureAtlas taRun = new TextureAtlas(Gdx.files.internal(spriteLocation+"/run/run.pack"));
        TextureAtlas taIdle = new TextureAtlas(Gdx.files.internal(spriteLocation+"/idle/idle.pack"));

        // An easier way to populate an animation:
        aniIdle = new Animation(10, taIdle.getRegions());
        aniRun = new Animation(10, taRun.getRegions());
        TextureRegion textureRegion = aniIdle.getKeyFrame(0f, true);
        int width = textureRegion.getRegionWidth();
        int height = textureRegion.getRegionHeight();

        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();

        bodyDef.position.set(new Vector2(position.x / 2f, position.y / 2f));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.fixedRotation = true;
        body = world.createBody(bodyDef);

        shape.setAsBox(width / 4f, height / 4f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 1f;
        body.setSleepingAllowed(false);
        bodyFixture = body.createFixture(fixtureDef);
        shape.dispose();
    }

    private void createFootSensor() {
        PolygonShape shape = new PolygonShape();

        TextureRegion trPlayer = aniIdle.getKeyFrame(0f);
        int width = trPlayer.getRegionWidth();

        shape.setAsBox(width / 8f, 0.2f, new Vector2(body.getLocalCenter().x, body.getLocalCenter().y - 10f), 0f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        footSensor = body.createFixture(fixtureDef);

        shape.dispose();
    }

    Vector3 getPosition() {
        return new Vector3(body.getPosition().x, body.getPosition().y, 0f);
    }

    void draw(SpriteBatch sb) {
        // drawing sprite on main body using default library, not using animatedbox2dsprite because it doesn't loop the animation
        elapsedTime++;
        float x = body.getPosition().x;
        float y = body.getPosition().y;


        // the next two vars should be populated in the body creation, since the values never change.
        // since these values are the same as the main running, they are all good.
        // update - I added the fW and fH population in the createBdyMain function since we only need to do it once.


		TextureRegion textureRegion;

		if(isIdle)
			textureRegion = aniIdle.getKeyFrame(elapsedTime, true);

		else
			textureRegion = aniRun.getKeyFrame(elapsedTime, true);

		int width = textureRegion.getRegionWidth();
		int height = textureRegion.getRegionHeight();

		if(bRight)
			sb.draw(textureRegion, x - width / 4f, y - height / 4f, width / 2f, height / 2f);

		else
			sb.draw(textureRegion, x + width / 4f, y - height / 4f, -width / 2f, height / 2f);
    }

    void stop() {
        // stop movement on release of keycode
        isIdle = true;
        body.setLinearVelocity(0f, body.getLinearVelocity().y);
    }

    void jump() {
        body.applyLinearImpulse(0, 100, body.getPosition().x, body.getPosition().y, true);
    }

}
