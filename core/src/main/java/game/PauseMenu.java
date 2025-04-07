package game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

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

        table = new Table();
        table.setFillParent(true);
        table.center();

        // Add a title label for the pause menu
        Label titleLabel = new Label("Paused", skin);
        titleLabel.setFontScale(2);
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
}
