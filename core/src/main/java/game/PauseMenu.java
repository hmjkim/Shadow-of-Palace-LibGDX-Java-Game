package game;

import Helper.FontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.Objects;

/**
 * Displays the pause menu overlay for the game.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public class PauseMenu {

    // Class-level constants for layout.
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_PADDING = 10;
    private static final int TITLE_FONT_SIZE = 48;
    private static final float TITLE_PAD_BOTTOM = 20f;
    private static final float FADE_IN_DURATION = 0.5f;
    private static final float OVERLAY_OPACITY = 0.6f;

    private final Stage stage;
    private final Table table;
    private final Skin skin;

    /**
     * Callback interface for pause menu actions.
     */
    public interface PauseScreenListener {
        /**
         * Resumes the game.
         */
        void onResume();

        /**
         * Restarts the game.
         */
        void onRestart();

        /**
         * Saves the current game state.
         */
        void onSave();

        /**
         * Exits the game.
         */
        void onExit();
    }

    /**
     * Creates a PauseMenu with the specified listener.
     *
     * @param listener the listener for pause menu actions
     * @throws IllegalArgumentException if the listener is null
     */
    public PauseMenu(final PauseScreenListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        // Initialize stage with an ExtendViewport using the current screen dimensions.
        stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Define local constants for overlay creation.
        final int pixmapWidth = 1;
        final int pixmapHeight = 1;
        final float overlayRed = 0f;
        final float overlayGreen = 0f;
        final float overlayBlue = 0f;

        // Create a dark transparent overlay.
        final Pixmap pixmap = new Pixmap(pixmapWidth, pixmapHeight, Pixmap.Format.RGBA8888);
        pixmap.setColor(overlayRed, overlayGreen, overlayBlue, OVERLAY_OPACITY);
        pixmap.fill();
        final Texture overlayTexture = new Texture(pixmap);
        pixmap.dispose(); // Release pixmap resources.

        final Image overlay = new Image(new TextureRegionDrawable(overlayTexture));
        overlay.setFillParent(true); // Cover the entire screen.

        table = new Table();
        table.setFillParent(true);
        table.center();

        // Create a title label for the pause menu.
        final BitmapFont titleFont = FontManager.generateFont(TITLE_FONT_SIZE, Color.WHITE);
        final Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = titleFont;
        final Label titleLabel = new Label("Paused", titleStyle);

        table.add(titleLabel).padBottom(TITLE_PAD_BOTTOM).colspan(2);
        table.row();

        // Create buttons.
        final TextButton resumeButton = new TextButton("Resume", skin);
        final TextButton restartButton = new TextButton("Restart", skin);
        final TextButton saveButton = new TextButton("Save", skin);
        final TextButton exitButton = new TextButton("Exit", skin);

        // Create buttons
        TextButton resumeButton = new TextButton("Resume", skin);
        TextButton restartButton = new TextButton("Restart", skin);
        TextButton saveButton = new TextButton("Save", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Add listeners for each button
        resumeButton.addListener(new ClickListener(){
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                listener.onResume();
            }
        });

        restartButton.addListener(new ClickListener(){
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                listener.onRestart();
            }
        });

        saveButton.addListener(new ClickListener(){
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {
                listener.onSave();
            }
        });

        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.onExit();
            }
        });

        // Layout the buttons in the table.
        table.add(resumeButton).pad(BUTTON_PADDING).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        table.row();
        table.add(restartButton).pad(BUTTON_PADDING).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        table.row();
        table.add(saveButton).pad(BUTTON_PADDING).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        table.row();
        table.add(exitButton).pad(BUTTON_PADDING).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);

        stage.addActor(overlay);
        stage.addActor(table);
    }

    /**
     * Gets the Stage used by the pause menu.
     *
     * @return the Stage instance
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Sets the visibility of the pause menu.
     *
     * @param visible true to display the pause menu, false to hide it
     */
    public void setVisible(final boolean visible) {

        table.setVisible(visible);
        if (visible) {
            // Optionally add a fade-in effect
            table.getColor().a = 0f;
            table.addAction(Actions.fadeIn(FADE_IN_DURATION));
        }
        System.out.println("Pause Menu: Visible " + visible);
    }

    /**
     * Disposes of resources used by the pause menu.
     */
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    /**
     * Updates the pause menu stage.
     *
     * @param delta the time in seconds since the last update
     */
    public void act(final float delta) {
        stage.act(delta);
    }

    /**
     * Draws the pause menu stage.
     */
    public void draw() {
        stage.draw();
    }

    /**
     * Determines if the PauseMenu is equal to another object.
     *
     * @param object the object to compare
     * @return true if the PauseMenu is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        PauseMenu pauseMenu = (PauseMenu) object;
        return Objects.equals(getStage(), pauseMenu.getStage())
            && Objects.equals(table, pauseMenu.table)
            && Objects.equals(skin, pauseMenu.skin);
    }

    /**
     * Gets the hashcode of the PauseMenu.
     *
     * @return the hashcode of the PauseMenu as an integer
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = Objects.hashCode(getStage());
        result = prime * result + Objects.hashCode(table);
        result = prime * result + Objects.hashCode(skin);
        return result;
    }

    /**
     * Gets the string representation of the PauseMenu.
     *
     * @return the string representation of the PauseMenu
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PauseMenu{");
        sb.append("stage=").append(stage);
        sb.append(", table=").append(table);
        sb.append(", skin=").append(skin);
        sb.append('}');
        return sb.toString();
    }
}
