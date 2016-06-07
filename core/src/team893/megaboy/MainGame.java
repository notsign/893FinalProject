package team893.megaboy;

import com.badlogic.gdx.Game;

import team893.megaboy.screens.GameScreen;

public class MainGame extends Game {

	@Override
	public void create() {
		setScreen(new GameScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void render() {
		super.render();
	}
}
