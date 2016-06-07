package team893.megaboy;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

/**
 * Created by k9sty on 2015-11-20.
 */
public class Map {
	TiledMap tiledMap;
	Box2DMapObjectParser mapObjectParser;
	BackgroundMusic backgroundMusic;

	public Map(World world, String mapName) {
		TmxMapLoader mapLoader = new TmxMapLoader();
		tiledMap = mapLoader.load("maps/"+mapName+".tmx");
		mapObjectParser = new Box2DMapObjectParser();
		mapObjectParser.load(world, tiledMap);
		mapObjectParser.setUnitScale(0.5f);

		backgroundMusic = new BackgroundMusic(getBackgroundMusicName());
		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(0.1f);
		backgroundMusic.play();
	}

	public TiledMap getMap() {
		return tiledMap;
	}

	public float getUnitScale() {
		// IMPORTANT!!!
		// mapObjectParser's UnitScale is set to a half because of the way box2d works.
		// box2d uses meters, and there is a cap on velocity. i scale everything down so i don't
		// get stuck on the velocity cap and have to apply over 100 000 in a forcetocenter for a basic jump
		return mapObjectParser.getUnitScale();
	}

	public String getBackgroundMusicName() {
		// in the .tmx file, the entire level is wrapped in one object named "level" which contains the name of the background music
		return (String)getMap().getLayers().get("World").getObjects().get("level").getProperties().get("bgm");
	}

	public Vector2 getPlayerSpawnPoint() {
		MapLayer layer = this.getMap().getLayers().get("World");
		RectangleMapObject spawnpoint = (RectangleMapObject) layer.getObjects().get("spawn point");
		return new Vector2(spawnpoint.getRectangle().getX(), spawnpoint.getRectangle().getY());
		// basic spawnpoint, it's a object that the player is relocated to and created
	}

	public Vector2[] getEnemySpawnPoints() {
		MapLayer layer = this.getMap().getLayers().get("Enemy Spawners");
		MapObjects objects = layer.getObjects();
		int nSpawners = objects.getCount();
		Vector2 spawnPoints[] = new Vector2[nSpawners];
		for (int i = 0; i < nSpawners; i++) {
			RectangleMapObject enemyspawn = (RectangleMapObject) objects.get("Espawner" + (i + 1));
			spawnPoints[i] = new Vector2(enemyspawn.getRectangle().getX(), enemyspawn.getRectangle().getY());
		}
		return spawnPoints;
	}
}
