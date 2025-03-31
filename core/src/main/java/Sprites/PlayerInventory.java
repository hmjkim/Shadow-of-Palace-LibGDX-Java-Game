package Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Manages the player's inventory including ingredients and items.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public class PlayerInventory {
    private int waterCount;
    private int leafCount;
    private int rockCount;

    private int poisonItemCount;
    private int invincibilityItemCount;
    private int heartRecoveryItemCount;

    private final Texture playerInventory;

    private final BitmapFont font;

    private static PlayerInventory instance;

    private PlayerInventory() {
        this.waterCount = 0;
        this.leafCount = 0;
        this.rockCount = 0;
        this.poisonItemCount = 0;
        this.invincibilityItemCount = 0;
        this.heartRecoveryItemCount = 0;

        this.playerInventory = new Texture("inventory/inventory-685x100.png");

        font = new BitmapFont();
        font.getData().setScale(0.84f);
    }

    public static PlayerInventory getInstance() {
        if (instance == null) {
            instance = new PlayerInventory();
        }
        return instance;
    }

    public void addWater() {
        this.waterCount++;
    }

    public void addLeaf() {
        this.leafCount++;
    }

    public void addRock() {
        this.rockCount++;
    }

    public void addPoison() {
        this.poisonItemCount++;
    }

    public void addInvincibility() {
        this.invincibilityItemCount++;
    }

    public void addHeartRecovery() {
        this.heartRecoveryItemCount++;
    }

    public boolean hasEnough(int waterReq, int leafReq, int rockReq) {
        return waterCount >= waterReq && leafCount >= leafReq && rockCount >= rockReq;
    }

    public void removeIngredients(int waterReq, int leafReq, int rockReq) {
        this.waterCount -= waterReq;
        this.leafCount -= leafReq;
        this.rockCount -= rockReq;
    }

    public void drawInventory(SpriteBatch batch, int screenWidth) {
        float x = (screenWidth - playerInventory.getWidth()) / 2f;
        float y = 20;

        batch.draw(playerInventory, x, y);

        font.draw(batch, "Water: " + waterCount, x + 25, y + playerInventory.getHeight() - 70);
        font.draw(batch, "Leaf: " + leafCount, x + 130, y + playerInventory.getHeight() - 70);
        font.draw(batch, "Rock: " + rockCount, x + 225, y + playerInventory.getHeight() - 70);
        font.draw(batch, "Poison: " + poisonItemCount, x + 318, y + playerInventory.getHeight() - 70);
        font.draw(batch, "Hide: " + invincibilityItemCount, x + 423, y + playerInventory.getHeight() - 70);
        font.draw(batch, "Heart: " + heartRecoveryItemCount, x + 516, y + playerInventory.getHeight() - 70);
        font.draw(batch, "Box: " + heartRecoveryItemCount, x + 620, y + playerInventory.getHeight() - 70);
    }

    public void dispose() {
        playerInventory.dispose();
        font.dispose();
    }
}
