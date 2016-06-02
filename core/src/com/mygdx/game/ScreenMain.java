package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by k9sty on 2016-03-12.
 */

public class ScreenMain implements Screen {
	Game game;
	World world;
	Map map;
	OrthographicCamera camera;
	Box2DDebugRenderer debugRenderer;
	TiledMapRenderer tiledMapRenderer;
	SpriteBatch spriteBatch;

	Player player;
	List<Entity> entityList;
	List<Entity> entityBuffer;

	ScreenMain(Game game) {
		this.game = game;

		spriteBatch = new SpriteBatch();
		entityList = new ArrayList<Entity>();
		entityBuffer = new ArrayList<Entity>(); // So updating entities can add entities to the list

		initializeWorld();
		initializeCamera();
		initializePlayer();
		initializeEnemySpawner();
	}

	private void initializeWorld() {
		world = new World(new Vector2(0f, -200f), true);
		// create contact listener in the class itself so i don't need to turn every variable into a static when i call it
		world.setContactListener(new ContactListener() {
			public void beginContact(Contact contact) {
				// K: Sensor code must be here!!
				// Sensor collisions aren't honoured in postSolve
				Fixture fixtureA = contact.getFixtureA();
				Fixture fixtureB = contact.getFixtureB();

				if (fixtureA.isSensor() && fixtureB.isSensor())
					return;

				if (fixtureA == player.footSensor)
					player.isGrounded = true;

				else if (fixtureB == player.footSensor)
					player.isGrounded = true;
			}

			@Override
			public void endContact(Contact contact) {
				Fixture fixtureA = contact.getFixtureA();
				Fixture fixtureB = contact.getFixtureB();

				if (fixtureA.isSensor() && fixtureB.isSensor())
					return;

				if (fixtureA == player.footSensor)
					player.isGrounded = false;

				else if (fixtureB == player.footSensor)
					player.isGrounded = false;
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				Object udata1 = contact.getFixtureA().getUserData();
				Object udata2 = contact.getFixtureB().getUserData();

				// We set userdata to the this pointer in these classes
				Bullet bullet = (udata1 instanceof Bullet) ? (Bullet)udata1
						: (udata2 instanceof Bullet) ? (Bullet)udata2
						: null;

				FastEnemy enemy = (udata1 instanceof FastEnemy) ? (FastEnemy)udata1
						: (udata2 instanceof FastEnemy) ? (FastEnemy)udata2
						: null;

				if (enemy != null && bullet != null && !bullet.hasContacted) { // Enemy colliding with bullet
					enemy.isAlive = false;
				}

				if (bullet != null) {
					bullet.hasContacted = true;
				}
			}
		});

		// pass world and desired map
		map = new Map(world, "debugroom");
	}

	private void initializeCamera() {
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 32 * (19 / 2), 32 * (10 / 2));
		// tile size * first two digits of resolution give you a solid camera, i just divide by 2 for a better view
		// two is a magic number
		camera.update();
		tiledMapRenderer = new OrthogonalTiledMapRenderer(map.getMap(), map.getUnitScale());
		// important: go to getUnitScale function in Map
	}

	private void initializePlayer() {
		player = new Player(world, map.getPlayerSpawnPoint(), entityBuffer);
		entityList.add(player);
	}

	private void initializeEnemySpawner() {
		Vector2[] arEnemySpawnPoints = map.getEnemySpawnPoints();
		for (int i = 0; i < arEnemySpawnPoints.length; i++) {
			entityList.add(new EnemySpawner(world, entityBuffer, player, arEnemySpawnPoints[i], 5));
		}
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		for (Entity entity : entityList)
			entity.update();

		Iterator<Entity> entityBufferIterator = entityBuffer.iterator();
		while (entityBufferIterator.hasNext()) {
			Entity entity = entityBufferIterator.next();
			entityList.add(entity);
			entityBufferIterator.remove();
		}

		world.step(1 / 60f, 6, 2); // Update our world

		clean(); // Remove dead enemies and collided bullets

		camera.position.set(new Vector3(player.getPosition().x, player.getPosition().y, 0f)); // Center the screen on the player
		camera.update(); // Lol idk

		// Rendering things...
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen

		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render(); // Render the map
		debugRenderer.render(world, camera.combined); // Render the outlines on objects

		spriteBatch.setProjectionMatrix(camera.combined);
		// set the projection matrix as the camera so the tile layer on the map lines up with the bodies
		// if this line wasn't here it wouldn't scale down
		spriteBatch.begin();
		for (Entity entity : entityList)
			entity.render(spriteBatch);

		spriteBatch.end();
	}

	private void clean() {
		// We have to remove stuff here instead of in the contact listener because it will crash
		// because of (my guess) a ConcurrentModificationException.

		Iterator<Entity> entityIterator = entityList.iterator();
		while (entityIterator.hasNext()) {
			Entity entity = entityIterator.next();

			// If the entity reports that it should be destroyed, grant its wish
			if (entity.shouldBeDestroyed()) {
				entity.destroy();
				entityIterator.remove();
			}
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}
}
