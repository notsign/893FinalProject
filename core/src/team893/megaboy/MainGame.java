package team893.megaboy;

import com.badlogic.gdx.Game;

import team893.megaboy.screens.GameScreen;
import team893.megaboy.screens.MenuScreen;

public class MainGame extends Game {
	public enum ScreenId {
		MENU, GAME
	}

	public void setScreen(ScreenId screenId) {
		switch (screenId) {
			case MENU:
				super.setScreen(new MenuScreen(this));
				break;
			case GAME:
				super.setScreen(new GameScreen(this));
				break;
		}
	}

	@Override
	public void create() {
		setScreen(ScreenId.MENU);
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
