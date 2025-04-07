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

import java.util.Objects;

/**
 * Displays the end screen overlay for the game.
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public class EndScreen implements Screen {

    private final MainGame game;
    private final Stage stage;
    private final Skin skin;
    private final String titleMessage;
    private final Color titleColor;

    /**
     * Creates an EndScreen with the specified game, title message, and title color.
     *
     * @param game the MainGame instance for managing screen transitions
     * @param titleMessage the title message to display on the end screen
     * @param titleColor the color of the title message
     * @throws IllegalArgumentException if any parameter is null
     */
    public EndScreen(final MainGame game, final String titleMessage, final Color titleColor) {
        if (game == null || titleMessage == null || titleColor == null) {
            throw new IllegalArgumentException("None of the parameters can be null");
        }
        this.game = game;
        this.titleMessage = titleMessage;
        this.titleColor = titleColor;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
    }

    /**
     * Initializes the end screen by creating the UI elements and arranging them in a table.
     */
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

    /**
     * Renders the end screen.
     *
     * @param delta the time in seconds since the last frame
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    /**
     * Resizes the end screen to the new dimensions.
     *
     * @param width the new screen width in pixels
     * @param height the new screen height in pixels
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Pauses the end screen.
     */
    @Override
    public final void pause() {
    }

    /**
     * Resumes the end screen.
     */
    @Override
    public final void resume() {
    }

    /**
     * Hides the end screen.
     */
    @Override
    public final void hide() {
    }

    /**
     * Disposes of resources used by the end screen.
     */
    @Override
    public final void dispose() {
        stage.dispose();
        skin.dispose();
    }

    /**
     * Determines if the EndScreen is equal to another object.
     *
     * @param object the object to compare
     * @return true if the EndScreen is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        EndScreen endScreen = (EndScreen) object;
        return Objects.equals(game, endScreen.game)
            && Objects.equals(stage, endScreen.stage)
            && Objects.equals(skin, endScreen.skin)
            && Objects.equals(titleMessage, endScreen.titleMessage)
            && Objects.equals(titleColor, endScreen.titleColor);
    }

    /**
     * Gets the hashcode of the EndScreen.
     *
     * @return the hashcode of the EndScreen as an integer
     */
    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = Objects.hashCode(game);
        result = prime * result + Objects.hashCode(stage);
        result = prime * result + Objects.hashCode(skin);
        result = prime * result + Objects.hashCode(titleMessage);
        result = prime * result + Objects.hashCode(titleColor);
        return result;
    }

    /**
     * Gets the string representation of the EndScreen.
     *
     * @return the string representation of the EndScreen
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EndScreen{");
        sb.append("game=").append(game);
        sb.append(", stage=").append(stage);
        sb.append(", skin=").append(skin);
        sb.append(", titleMessage='").append(titleMessage).append('\'');
        sb.append(", titleColor=").append(titleColor);
        sb.append('}');
        return sb.toString();
    }
}
