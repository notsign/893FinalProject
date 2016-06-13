package team893.megaboy.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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

import team893.megaboy.MainGame;
import team893.megaboy.entities.Bullet;
import team893.megaboy.entities.EnemySpawner;
import team893.megaboy.entities.FastEnemy;
import team893.megaboy.entities.Entity;
import team893.megaboy.entities.Player;
import team893.megaboy.Map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Kevin on 03/06/2016.
 */
public class GameScreen implements Screen {
	MainGame game;
	World world;
	Map map;
	OrthographicCamera camera;
	Box2DDebugRenderer debugRenderer;
	TiledMapRenderer tiledMapRenderer;
	SpriteBatch spriteBatch;

	Player player;
	List<Entity> entityList;
	List<Entity> entityBuffer;

	public GameScreen(MainGame game) {
		this.game = game;

		spriteBatch = new SpriteBatch();
		entityList = new ArrayList<Entity>();
		entityBuffer = new ArrayList<Entity>(); // So entities that are updating can add entities to the list

		initializeWorld();
		initializeCamera();
		initializePlayer();
		initializeEnemySpawner();
	}

	private void initializeWorld() {
		world = new World(new Vector2(0f, -200f), true);

		// Collision detection!!
		// TODO: Should this be delegated?
		world.setContactListener(new ContactListener() {
			public void beginContact(Contact contact) {
				// Sensor collisions aren't honoured in postSolve, so it has to be here
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
				// We set fixture userdata to the 'this' reference in these classes
				// Basically, we check if the reference is to a Bullet or an Enemy and apply
				// collision depending

				Object udata1 = contact.getFixtureA().getUserData();
				Object udata2 = contact.getFixtureB().getUserData();

				if (udata1 instanceof Player && udata2 instanceof FastEnemy
						|| udata1 instanceof FastEnemy && udata2 instanceof Player)
					player.health--;

				Bullet bullet = (udata1 instanceof Bullet) ? (Bullet)udata1
						: (udata2 instanceof Bullet) ? (Bullet)udata2
						: null;

				FastEnemy enemy = (udata1 instanceof FastEnemy) ? (FastEnemy)udata1
						: (udata2 instanceof FastEnemy) ? (FastEnemy)udata2
						: null;

				// Enemy colliding with bullet
				if (enemy != null && bullet != null && !bullet.hasContacted) {
					enemy.isAlive = false;
				}

				// Bullet colliding with something
				if (bullet != null) {
					bullet.hasContacted = true;
				}
			}
		});

		map = new Map(world, "debugroom");
	}

	private void initializeCamera() {
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 32 * (19 / 2), 32 * (10 / 2));
		// Tile size * first two digits of resolution give you a solid camera, i just divide by 2 for a better view
		// Two is a magic number
		camera.update();
		tiledMapRenderer = new OrthogonalTiledMapRenderer(map.getMap(), map.getUnitScale());
	}

	private void initializePlayer() {
		player = new Player(world, map.getPlayerSpawnPoint(), entityBuffer);
		entityList.add(player);
	}

	private void initializeEnemySpawner() {
		Vector2[] arEnemySpawnPoints = map.getEnemySpawnPoints();
		for (Vector2 arEnemySpawnPoint : arEnemySpawnPoints) {
			entityList.add(new EnemySpawner(world, entityBuffer, player, arEnemySpawnPoint, 5));
		}
	}

	@Override
	public void render(float delta) {
		// Let all our entities update
		for (Entity entity : entityList) {
			entity.update();
		}

		// Add the entities added by the updating entities to the main list
		Iterator<Entity> entityBufferIterator = entityBuffer.iterator();
		while (entityBufferIterator.hasNext()) {
			Entity entity = entityBufferIterator.next();
			entityList.add(entity);
			entityBufferIterator.remove();
		}

		world.step(1 / 60f, 6, 2); // Update our world

		clean(); // Remove dead enemies and collided bullets

		camera.position.set(new Vector3(player.getPosition().x, player.getPosition().y, 0f));
		camera.update();

		// Render stuff below
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen

		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render(); // Render the map
		debugRenderer.render(world, camera.combined); // Render the debug outlines on objects

		// Set the projection matrix to the camera's so the sprites line up with the camera
		spriteBatch.setProjectionMatrix(camera.combined);

		spriteBatch.begin();

		for (Entity entity : entityList) {
			entity.render(spriteBatch);
		}

		spriteBatch.end();
	}

	private void clean() {
		// We have to remove stuff here instead of in the contact listener because it will crash
		// because of (my guess) a ConcurrentModificationException.

		if (player.shouldBeDestroyed()) {
			player.destroy();
			game.setScreen(MainGame.ScreenId.MENU);
			return; // At this point this screen is abandoned
		}

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
	public void show() {
		map.backgroundMusic.play();
	}

	@Override
	public void hide() {
		map.backgroundMusic.pause();
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
	public void dispose() {

	}
}
