package game;

import Sprites.PlayerHealth;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameOverScreen implements Screen {

    private final MainGame game;
    private Stage stage;
    private Skin skin;

    public GameOverScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Create a Stage and set it as the input processor
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Load a default skin
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Create a Table to organize our UI elements
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Create a custom LabelStyle for the title
        BitmapFont font = new BitmapFont();
        LabelStyle titleStyle = new LabelStyle(font, Color.RED);
        Label titleLabel = new Label("Game Over", titleStyle);
        titleLabel.setFontScale(2f);

        // Create Restart and Exit buttons using the skin
        TextButton restartButton = new TextButton("Restart", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Add listeners for button clicks
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("GameOverScreen", "Restart button clicked!");
                game.startNewGame();
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Arrange UI elements in the table
        table.add(titleLabel).pad(20).colspan(2);
        table.row();
        table.add(restartButton).pad(10).width(150);
        table.row();
        table.add(exitButton).pad(10).width(150);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
