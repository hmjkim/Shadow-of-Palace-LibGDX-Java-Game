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


public class PauseMenu {
    private Stage stage;
    private Table table;
    private Skin skin;

    // Callback interfaces for the buttons
    public interface PauseScreenListener {
        void onResume();
        void onRestart();
        void onSave();
        void onExit();
    }

    public PauseMenu(PauseScreenListener listener) {
        // with extendedviewport
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

    public Stage getStage() {
        return stage;
    }

    public void setVisible(boolean visible) {

        table.setVisible(visible);
        if (visible) {
            // Optionally add a fade-in effect
            table.getColor().a = 0f;
            table.addAction(Actions.fadeIn(0.5f));
        }
        System.out.println("Pause Menu: Visible " + visible);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public void act(float delta) {
        stage.act(delta);
    }

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
