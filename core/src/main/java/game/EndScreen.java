package game;

import Helper.FontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class EndScreen implements Screen {

    private final MainGame game;
    private Stage stage;
    private Skin skin;
    private String titleMessage;
    private Color titleColor;

    public EndScreen(MainGame game, String titleMessage, Color titleColor) {
        this.game = game;
        // Create a Stage and set it as the input processor
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        // Load a default skin
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        this.titleMessage = titleMessage;
        this.titleColor = titleColor;
    }

    @Override
    public void show() {

        // Create a Table to organize our UI elements
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Create title label using FontManager
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = FontManager.generateFont(50, titleColor);
        Label titleLabel = new Label(titleMessage, titleStyle);
        titleLabel.setFontScale(2f);

        // Create Restart and Exit buttons using the skin
        TextButton restartButton = new TextButton("Restart", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Add listeners for button clicks
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("EndScreen", "Restart button clicked!");
                game.startNewGame();
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        titleLabel.setAlignment(Align.center);
        // Arrange UI elements in the table
        table.add(titleLabel).pad(20);
        table.row();
        table.add(restartButton).pad(10).width(150).height(50);
        table.row();
        table.add(exitButton).pad(10).width(150).height(50);
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
