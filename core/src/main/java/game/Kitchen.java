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

import java.util.Objects;

/**
 * Manages the kitchen area and handles crafting interactions.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public class Kitchen {
    private final Rectangle kitchenArea;
    private final TextureRegion textBox;
    private boolean showPrompt;
    private String message;
    private final GameScreen screen;

    /**
     * Constructs a Kitchen object with the specified kitchen area and game screen.
     *
     * @param kitchenArea the rectangle representing the kitchen area in world units
     * @param screen the GameScreen instance used for displaying messages
     * @throws IllegalArgumentException if kitchenArea or screen is null
     */
    public Kitchen(final Rectangle kitchenArea, final GameScreen screen) {
        if (kitchenArea == null || screen == null) {
            throw new IllegalArgumentException("Kitchen area and screen cannot be null");
        }
        this.kitchenArea = kitchenArea;
        this.message = "";
        this.showPrompt = false;
        this.screen = screen;
        this.textBox = new TextureRegion(new Texture("action-box.png"));
    }

    /**
     * Draws the kitchen prompt on the screen if it is visible.
     *
     * @param batch the Batch used for drawing
     * @param uiCamera the OrthographicCamera used for UI projection
     * @param font the BitmapFont used to render the text
     * @param layout the GlyphLayout used to calculate text dimensions
     * @param plusLength additional height to add to the text box
     * @param plusWidthLength additional width to add to the text box
     */
    public void draw(final Batch batch, final OrthographicCamera uiCamera,
                     final BitmapFont font, final GlyphLayout layout,
                     final float plusLength, final float plusWidthLength) {
        if (!showPrompt) {
            return;
        }

        final float extraWidth = 200f;
        float boxWidth = textBox.getRegionWidth() + extraWidth + plusWidthLength;
        float boxHeight = textBox.getRegionHeight() + plusLength;

        float centerX = kitchenArea.x + kitchenArea.width / 2f;
        final float verticalOffset = 0.1f;
        float centerY = kitchenArea.y + kitchenArea.height + verticalOffset;
        Vector3 tmpVec = new Vector3(centerX, centerY, 0);

        uiCamera.project(tmpVec);

        final float screenYOffset = 10f;
        float screenX = tmpVec.x - boxWidth / 2f;
        float screenY = tmpVec.y + screenYOffset;

        batch.draw(textBox, screenX, screenY, boxWidth, boxHeight);

        layout.setText(font, message);
        float textX = screenX + (boxWidth - layout.width) / 2f;
        float textY = screenY + (boxHeight + layout.height) / 2f;
        font.draw(batch, layout, textX, textY);
    }

    /**
     * Checks if the specified world coordinates are within the kitchen area.
     *
     * @param worldX the x-coordinate in world units
     * @param worldY the y-coordinate in world units
     * @return true if the coordinates are inside the kitchen area, false otherwise
     */
    public boolean contains(final float worldX, final float worldY) {
        return kitchenArea.contains(worldX, worldY);
    }

    /**
     * Sets whether the kitchen prompt should be displayed.
     *
     * @param show true to display the prompt, false to hide it
     */
    public void setShowText(final boolean show) {
        this.showPrompt = show;
    }

    /**
     * Sets the message to be displayed on the kitchen prompt.
     *
     * @param message the message text
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * Handles the crafting process by consuming ingredients and updating the game state.
     */
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
                default:
            }
        } else {
            System.out.println("Not enough ingredients!");
            screen.showMessage("Not enough ingredients!", Color.RED);
        }
    }

    /**
     * Determines whether this Kitchen instance is equal to another object.
     *
     * @param object the object to compare
     * @return true if the Kitchen instances are equal, false otherwise
     */
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Kitchen kitchen = (Kitchen) object;
        return showPrompt == kitchen.showPrompt
            && Objects.equals(kitchenArea, kitchen.kitchenArea)
            && Objects.equals(textBox, kitchen.textBox)
            && Objects.equals(message, kitchen.message)
            && Objects.equals(screen, kitchen.screen);
    }

    /**
     * Returns the hash code of this Kitchen.
     *
     * @return the hash code as an integer
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = Objects.hashCode(kitchenArea);
        result = prime * result + Objects.hashCode(textBox);
        result = prime * result + Boolean.hashCode(showPrompt);
        result = prime * result + Objects.hashCode(message);
        result = prime * result + Objects.hashCode(screen);
        return result;
    }

    /**
     * Returns the string representation of this Kitchen.
     *
     * @return description as a String
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Kitchen{");
        sb.append("kitchenArea=").append(kitchenArea);
        sb.append(", textBox=").append(textBox);
        sb.append(", showPrompt=").append(showPrompt);
        sb.append(", message='").append(message).append('\'');
        sb.append(", screen=").append(screen);
        sb.append('}');
        return sb.toString();
    }
}
