package game;

import Manager.GameSaveManager;
import Manager.GameState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.util.Objects;

/**
 * Displays the main menu screen for the game.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public class MainMenuScreen implements Screen {

    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_PADDING = 10;
    private static final int TITLE_PADDING = 40;
    private static final int FONT_SIZE = 110;

    private final MainGame game;
    private final Stage stage;
    private Skin skin;

    /**
     * Creates a MainMenuScreen with the specified MainGame instance.
     *
     * @param game the MainGame instance
     * @throws IllegalArgumentException if the game parameter is null
     */
    public MainMenuScreen(final MainGame game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }
        this.game = game;
        this.stage = new Stage();
    }

    /**
     * Initializes the main menu screen by setting the input processor, loading the UI skin,
     * creating the layout table, and adding the title and buttons.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // Load a default skin
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Add background
        Texture backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        final Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("font/upheaval.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = FONT_SIZE;
        parameter.color = Color.valueOf("fefcde");

        final int offset = 8;
        final float opacity = 0.75f;
        parameter.shadowOffsetX = offset;
        parameter.shadowOffsetY = offset;
        parameter.shadowColor = new Color(0, 0, 0, opacity);

        BitmapFont fontWithShadow = generator.generateFont(parameter);
        generator.dispose();

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = fontWithShadow;

        Label titleLabel = new Label("SHADOW \nOF\n PALACE", style);

        // Set the label alignment to center
        titleLabel.setAlignment(Align.center);

        stage.addActor(titleLabel);

        table.add(titleLabel).padTop(TITLE_PADDING).padBottom(TITLE_PADDING).colspan(1);
        table.row();

        // Create "Start" and "Exit" buttons
        final TextButton startButton = new TextButton("Start", skin);
        final TextButton exitButton = new TextButton("Exit", skin);

        // Add listener to the Start button to switch to the game screen
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        // Add listener to the Exit button to exit the application
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                Gdx.app.exit();
            }
        });

        // Add buttons to the table with padding
        table.add(startButton).pad(BUTTON_PADDING).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        table.row();

        final GameState loadedState = GameSaveManager.loadGame();
        if (loadedState != null) {
            // Create a Load button if a saved game state is available.
            final TextButton loadButton = new TextButton("Load", skin);
            table.add(loadButton).pad(BUTTON_PADDING).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
            table.row();

            loadButton.addListener(new ClickListener() {
                @Override
                public void clicked(final InputEvent event, final float x, final float y) {
                    game.setScreen(new GameScreen(game, loadedState));
                }
            });
        }
        table.add(exitButton).pad(BUTTON_PADDING).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
    }

    /**
     * Renders the main menu screen.
     *
     * @param delta the time in seconds
     */
    @Override
    public void render(final float delta) {
        // Clear the screen.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update and draw the stage
        stage.act(delta);
        stage.draw();
    }

    /**
     * Resizes the main menu screen.
     *
     * @param width the new screen width in pixels
     * @param height the new screen height in pixels
     */
    @Override
    public void resize(final int width, final int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Pauses the game.
     */
    @Override
    public void pause() {
    }

    /**
     * Resumes the game.
     */
    @Override
    public void resume() {
    }

    /**
     * Hides this screen when it is no longer current.
     */
    @Override
    public void hide() {
    }

    /**
     * Disposes of resources used by the main menu screen.
     */
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    /**
     * Determines if the MainMenuScreen is equal to another object.
     *
     * @param object the object to compare
     * @return true if the MainMenuScreen is equal to the other object, false otherwise
     */
    @Override
    public final boolean equals(final Object object) {
        if (object == null) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (!(object.getClass() == this.getClass())) {
            return false;
        }
        MainMenuScreen that = (MainMenuScreen) object;
        return Objects.equals(this.game, that.game)
            && Objects.equals(this.stage, that.stage)
            && Objects.equals(this.skin, that.skin);
    }

    /**
     * Gets the hashcode of the MainMenuScreen.
     *
     * @return the hashcode of the MainMenuScreen as an integer
     */
    @Override
    public final int hashCode() {
        final int prime = 17;
        int result = Objects.hashCode(this.game);
        result = prime * result + Objects.hashCode(this.stage);
        result = prime * result + Objects.hashCode(this.skin);
        return result;
    }

    /**
     * Gets the string representation of the MainMenuScreen.
     *
     * @return the string representation of the MainMenuScreen
     */
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder("MainMenuScreen {");
        sb.append("game='").append(this.game).append('\'');
        sb.append(", stage='").append(this.stage).append('\'');
        sb.append(", skin='").append(this.skin).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
