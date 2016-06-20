package team893.megaboy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import team893.megaboy.MainGame;

/**
 * Created by Kevin on 20/06/2016.
 */
public class GameOverScreen implements Screen, InputProcessor {
	MainGame game;
	Stage stage;
	TextButton tbMainMenu;
	TextButton tbRespawn;

	public GameOverScreen(MainGame _game) {
		this.game = _game;
		stage = new Stage();

		tbMainMenu = new TextButton("Main Menu", new TxtBtnBaseStyle());
		tbMainMenu.setSize(200f, 50f);
		tbMainMenu.setPosition(tbMainMenu.getWidth() / 2, tbMainMenu.getHeight() / 2);
		tbMainMenu.addListener(new InputListener() {//http://gamedev.stackexchange.com/questions/60123/registering-inputlistener-in-libgdx
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				game.setScreen(MainGame.ScreenId.MENU);
				return true;
			}
		});

		tbRespawn = new TextButton("Respawn", new TxtBtnBaseStyle());
		tbRespawn.setSize(200f, 50f);
		tbRespawn.setPosition(Gdx.graphics.getWidth() - tbRespawn.getWidth() * 1.5f, tbRespawn.getHeight() / 2);
		tbRespawn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				game.setScreen(MainGame.ScreenId.GAME);
				return true;
			}
		});

		stage.addActor(tbMainMenu);
		stage.addActor(tbRespawn);
	}

	@Override
	public boolean keyDown(int keycode) {
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

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen

		stage.act();
		stage.draw();
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
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void dispose() {

	}
}
