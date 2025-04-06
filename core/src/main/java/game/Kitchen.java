package game;

import Sprites.PlayerInventory;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Color;

public class Kitchen {
    private final Rectangle kitchenArea;
    private TextureRegion textBox;
    private boolean showPrompt;
    private String message;
    private GameScreen screen;

    public Kitchen(Rectangle kitchenArea, GameScreen screen) {
        this.kitchenArea = kitchenArea;
        this.message = "";
        this.showPrompt = false;
        this.screen = screen;
        this.textBox = new TextureRegion(new Texture("action-box.png"));
    }

    public void draw(Batch batch, OrthographicCamera uiCamera,
                     BitmapFont font, GlyphLayout layout, float plusLength, float plusWidthLength) {
        if (!showPrompt) return;

        float boxWidth = textBox.getRegionWidth() + 200f + plusWidthLength;
        float boxHeight = textBox.getRegionHeight() + plusLength;

        float centerX = kitchenArea.x + kitchenArea.width / 2f;
        float centerY = kitchenArea.y + kitchenArea.height + 0.1f;
        Vector3 tmpVec = new Vector3(centerX, centerY, 0);

        uiCamera.project(tmpVec);

        float screenX = tmpVec.x - boxWidth / 2f;
        float screenY = tmpVec.y + 10f;

        batch.draw(textBox, screenX, screenY, boxWidth, boxHeight);

        layout.setText(font, message);
        float textX = screenX + (boxWidth - layout.width) / 2f;
        float textY = screenY + (boxHeight + layout.height) / 2f;
        font.draw(batch, layout, textX, textY);
    }

    public boolean contains(float worldX, float worldY) {
        return kitchenArea.contains(worldX, worldY);
    }

    public void setShowText(boolean show) {
        this.showPrompt = show;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void handleCraft() {
        MysteryBoxOutcome outcome = MysteryBox.getInstance().craft(PlayerInventory.getInstance());
        if (outcome != null) {
            switch (outcome) {
                case FAILURE:
                    System.out.println("You got nothing");
                    screen.showMessage("You got nothing", Color.RED);
                    break;
                case POISON:
                    System.out.println("You got a Poison item!");
                    screen.showMessage("You got a Poison item!", Color.WHITE);
                    PlayerInventory.getInstance().addPoison();
                    break;
                case INVINCIBILITY:
                    System.out.println("You got a Invincibility item!");
                    screen.showMessage("You got a Invincibility item!", Color.WHITE);
                    PlayerInventory.getInstance().addInvincibility();
                    break;
                case HEART_RECOVERY:
                    System.out.println("You got a Heart Recovery item!");
                    screen.showMessage("You got a Heart Recovery item!", Color.WHITE);
                    PlayerInventory.getInstance().addHeartRecovery();
                    break;
            }
        } else {
            System.out.println("Not enough ingredients!");
            screen.showMessage("Not enough ingredients!", Color.RED);
        }
    }
}
