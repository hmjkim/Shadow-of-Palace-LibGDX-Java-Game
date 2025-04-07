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
        // with extended viewport
        stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Create a dark transparent overlay
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.6f); // black with 60% opacity
        pixmap.fill();
        Texture overlayTexture = new Texture(pixmap);
        pixmap.dispose(); // done with pixmap

        Image overlay = new Image(new TextureRegionDrawable(overlayTexture));
        overlay.setFillParent(true); // cover the whole screen

        table = new Table();
        table.setFillParent(true);
        table.center();

        // Add a title label for the pause menu
        BitmapFont titleFont = FontManager.generateFont(48, Color.WHITE); // size & color

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = titleFont;

        Label titleLabel = new Label("Paused", titleStyle);

        table.add(titleLabel).padBottom(20).colspan(2);
        table.row();

        // Enable debug lines on the table
        // table.setDebug(true);

        // Create buttons
        TextButton resumeButton = new TextButton("Resume", skin);
        TextButton restartButton = new TextButton("Restart", skin);
        TextButton saveButton = new TextButton("Save", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Add listeners for each button
        resumeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.onResume();
            }
        });

        restartButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.onRestart();
            }
        });

        saveButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.onSave();
            }
        });

        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.onExit();
            }
        });

        // Layout the buttons in the table
        table.add(resumeButton).pad(10).width(150).height(50);
        table.row();
        table.add(restartButton).pad(10).width(150).height(50);
        table.row();
        table.add(saveButton).pad(10).width(150).height(50);
        table.row();
        table.add(exitButton).pad(10).width(150).height(50);

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
            table.addAction(Actions.fadeIn(0.5f));
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
