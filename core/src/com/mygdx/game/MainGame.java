package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.Screens.GameScreen;

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
