package game;

import Helper.FontManager;
import Manager.GameSaveManager;
import Manager.GameState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Represents the main menu screen for the game.
 * This screen displays the game title along with Start, Load, and Exit options.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public class MainMenuScreen implements Screen {
    private final MainGame game;
    private final Stage stage;
    private Skin skin;

    /**
     * Constructs a MainMenuScreen with the specified MainGame instance.
     *
     * @param game the MainGame instance
     */
    public MainMenuScreen(final MainGame game) {
        this.game = game;
        stage = new Stage();
    }

    /**
     * Initializes the main menu screen by setting the input processor,
     * loading the UI skin, creating the layout table, and adding the title and buttons.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // Load a default skin
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = FontManager.generateFont(64, Color.WHITE);
        Label titleLabel = new Label("Shadow of Palace", titleStyle);
        titleLabel.setFontScale(1.2f);

        table.add(titleLabel).padBottom(40).colspan(1);
        table.row();

        // Create "Start" and "Exit" buttons
        TextButton startButton = new TextButton("Start", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Add listeners to the buttons
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Switch to the game screen when "Start" is clicked
                game.setScreen(new GameScreen(game));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Exit the application when "Exit" is clicked
                Gdx.app.exit();
            }
        });

        // Add buttons to the table with some padding
        table.add(startButton).pad(10).width(150).height(50);
        table.row();

        GameState loadedState = GameSaveManager.loadGame();
        if (loadedState != null) {
            // Create a Load button
            TextButton loadButton = new TextButton("Load", skin);
            table.add(loadButton).pad(10).width(150).height(50);
            table.row();

            loadButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(new GameScreen(game, loadedState)); // new game with saved state
                }
            });
        }
        table.add(exitButton).pad(10).width(150).height(50);
    }

    /**
     * Renders the main menu screen.
     *
     * @param delta the time in seconds
     */
    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update and draw the stage
        stage.act(delta);
        stage.draw();
    }

    /**
     * Resizes the main menu screen.
     *
     * @param width  the new screen width in pixels
     * @param height the new screen height in pixels
     */
    @Override
    public void resize(int width, int height) {
        // Update the viewport when the screen size changes
        stage.getViewport().update(width, height, true);
    }

    /**
     * Pauses the game.
     */
    @Override
    public void pause() {  }

    /**
     * Resumes the game.
     */
    @Override
    public void resume() {  }

    /**
     * Hides this screen when it is no longer current.
     */
    @Override
    public void hide() {  }

    /**
     * Disposes of resources used by the main menu screen.
     */
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
