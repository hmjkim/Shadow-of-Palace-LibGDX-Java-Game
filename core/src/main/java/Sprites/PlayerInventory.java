package Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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

        this.playerInventory = new Texture("inventory.jpg");

        font = new BitmapFont();
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

    public void drawInventory(SpriteBatch batch, int screenWidth, int screenHeight) {
        int iconSize = 32;
        int padding = 10;

        float x = (screenWidth - playerInventory.getWidth()) / 2f;
        float y = 20;

        font.draw(batch, "Water: " + waterCount, x + 20, y + playerInventory.getHeight() - 10);
        font.draw(batch, "Leaf: " + leafCount, x + 140, y + playerInventory.getHeight() - 10);
        font.draw(batch, "Rock: " + rockCount, x + 260, y + playerInventory.getHeight() - 10);
        font.draw(batch, "Poison: " + poisonItemCount, x + 380, y + playerInventory.getHeight() - 10);
        font.draw(batch, "Invincibility: " + invincibilityItemCount, x + 520, y + playerInventory.getHeight() - 10);
        font.draw(batch, "Heart: " + heartRecoveryItemCount, x + 700, y + playerInventory.getHeight() - 10);
    }

    public void dispose() {
        playerInventory.dispose();
        font.dispose();
    }
}
