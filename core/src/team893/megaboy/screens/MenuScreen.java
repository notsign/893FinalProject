package team893.megaboy.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import team893.megaboy.MainGame;

/**
 * Created by Kevin on 09/06/2016.
 */

class TxtBtnBaseStyle extends TextButton.TextButtonStyle {
	Skin skin = new Skin();
	TextureAtlas taAtlas;

	public TxtBtnBaseStyle() {
		BitmapFont font = new BitmapFont();
		skin.add("default", font);
		taAtlas = new TextureAtlas(Gdx.files.internal("images/UpButton.pack"));
		skin.addRegions(taAtlas);
		this.up = skin.getDrawable("ArrowUp");
		this.down = skin.getDrawable("PressedArrowUp");
		this.font = skin.getFont("default");
	}
}

public class MenuScreen implements Screen, InputProcessor {
	MainGame game;
	Stage stage;
	TextButton tbStart;

	public MenuScreen(MainGame _game) {
		this.game = _game;
		stage = new Stage();

		tbStart = new TextButton("Start da gam duyd", new TxtBtnBaseStyle());
		tbStart.setPosition(0f, 0f);
		tbStart.setSize(200f, 50f);
		tbStart.addListener(new InputListener() {//http://gamedev.stackexchange.com/questions/60123/registering-inputlistener-in-libgdx
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				game.setScreen(MainGame.ScreenId.GAME);
				return true;
			}
		});

		stage.addActor(tbStart);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
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
}