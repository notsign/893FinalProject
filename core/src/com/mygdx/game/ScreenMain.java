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

/**
 * Created by k9sty on 2016-03-12.
 */

public class ScreenMain implements Screen, InputProcessor {
	Game game;
	World world;
	Map map;
	OrthographicCamera camera;
	Box2DDebugRenderer debugRenderer;
	TiledMapRenderer tiledMapRenderer;
	Player player;
	SpriteBatch spriteBatch;
	EnemySpawner[] arSpawner;
	ArrayList<Bullet> bullets;

	ScreenMain(Game game) {
		this.game = game;

		spriteBatch = new SpriteBatch();
		bullets = new ArrayList<Bullet>();

		initializeWorld();
		initializeCamera();
		initializePlayer();
		initializeEnemySpawner();
	}

	private void initializeWorld() {
		world = new World(new Vector2(0f, -200f), true);
		// create contact listener in the class itself so i don't need to turn every variable into a static when i call it
		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				// Unlike presolve, beginContact is called for sensors. If you want to move the
				// other hit detection code to presolve, go ahead, just leave the sensor code
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
				Fixture fa = contact.getFixtureA();
				Fixture fb = contact.getFixtureB();

				Fixture enemyFixture = (fa.getFilterData().groupIndex == -2) ? fa : (fb.getFilterData().groupIndex == -2) ? fb : null;
				Fixture bulletFixture = (fa.getFilterData().groupIndex == -1) ? fa : (fb.getFilterData().groupIndex == -1) ? fb : null;

				if (enemyFixture != null && bulletFixture != null) { // An enemy and a bullet collided
					// Find the enemy that owns this fixture
					for (FastEnemy fastEnemy : arSpawner[0].fastEnemies) {
						if (fastEnemy.body.equals(enemyFixture.getBody())) {
							fastEnemy.isAlive = false;
							break;
						}
					}
				}

				if (bulletFixture != null) { // A bullet hit something
					// Find the bullet that owns the fixture
					for (Bullet bullet : bullets) {
						if (bullet.body.equals(bulletFixture.getBody())) {
							bullet.hasContacted = true;
							break;
						}
					}
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
		player = new Player(world, map.getPlayerSpawnPoint());
	}

	private void initializeEnemySpawner() {
		Vector2[] arEnemySpawnPoints = map.getEnemySpawnPoints();
		arSpawner = new EnemySpawner[map.nSpawners];
		for (int i = 0; i < arSpawner.length; i++) {
			arSpawner[i] = new EnemySpawner(world, arEnemySpawnPoints[i]);
		}

	}


	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render(float delta) {
		// Update all our stuff before rendering
		player.bulletCooldown--;
		player.move();

		for (EnemySpawner enemySpawner: arSpawner)
			enemySpawner.update(player.getPosition());

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
		player.draw(spriteBatch);
		for(EnemySpawner enemySpawner : arSpawner)
			enemySpawner.draw(spriteBatch);

		spriteBatch.end();
	}

	private void clean() {
		// We have to remove stuff here instead of in the contact listener because it will crash
		// because (my guess) of a ConcurrentModificationException.

		Iterator<Bullet> bulletIterator = bullets.iterator();
		while (bulletIterator.hasNext()) {
			Bullet b = bulletIterator.next();
			if (b.hasContacted) {
				world.destroyBody(b.body);
				bulletIterator.remove();
			}
		}

		for (EnemySpawner enemySpawner : arSpawner) {
			Iterator<FastEnemy> enemyIterator = enemySpawner.fastEnemies.iterator();
			while (enemyIterator.hasNext()) {
				FastEnemy enemy = enemyIterator.next();
				if (!enemy.isAlive) {
					world.destroyBody(enemy.body);
					enemyIterator.remove();
				}
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


	@Override
	public boolean keyDown(int keycode) {
		// TODO: Should this be moved to player class as well?
		if (keycode == Input.Keys.X && player.bulletCooldown <= 0) {
			bullets.add(new Bullet(world, player.getPosition(), player.bRight));
			player.bulletCooldown = 30;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
