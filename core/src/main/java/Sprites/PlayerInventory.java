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

    private Texture waterIcon;
    private Texture leafIcon;
    private Texture rockIcon;
    private Texture poisonIcon;
    private Texture invincibilityIcon;
    private Texture heartRecoveryIcon;

    private BitmapFont font;

    private static PlayerInventory instance;

    private PlayerInventory() {
        this.waterCount = 0;
        this.leafCount = 0;
        this.rockCount = 0;
        this.poisonItemCount = 0;
        this.invincibilityItemCount = 0;
        this.heartRecoveryItemCount = 0;

        this.waterIcon = new Texture("water.png");
        this.leafIcon = new Texture("leaf.png");
        this.rockIcon = new Texture("rock.png");
        this.poisonIcon = new Texture("poison.png");
        this.invincibilityIcon = new Texture("invincibility.png");
        this.heartRecoveryIcon = new Texture("recovery.png");

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

        float x = screenWidth - padding - iconSize;
        float y = screenHeight - padding - iconSize;

        batch.draw(waterIcon, x, y, iconSize, iconSize);
        font.draw(batch, String.valueOf(waterCount), x - 20, y + iconSize / 2);

        x -= (iconSize + padding);
        batch.draw(leafIcon, x, y, iconSize, iconSize);
        font.draw(batch, String.valueOf(leafCount), x - 20, y + iconSize / 2);

        x -= (iconSize + padding);
        batch.draw(rockIcon, x, y, iconSize, iconSize);
        font.draw(batch, String.valueOf(rockCount), x - 20, y + iconSize / 2);

        x = screenWidth - padding - iconSize;
        y -= (iconSize + padding);

        batch.draw(poisonIcon, x, y, iconSize, iconSize);
        font.draw(batch, String.valueOf(poisonItemCount), x - 20, y + iconSize / 2);

        x -= (iconSize + padding);
        batch.draw(invincibilityIcon, x, y, iconSize, iconSize);
        font.draw(batch, String.valueOf(invincibilityItemCount), x - 20, y + iconSize / 2);

        x -= (iconSize + padding);
        batch.draw(heartRecoveryIcon, x, y, iconSize, iconSize);
        font.draw(batch, String.valueOf(heartRecoveryItemCount), x - 20, y + iconSize / 2);
    }

    public void dispose() {
        waterIcon.dispose();
        leafIcon.dispose();
        rockIcon.dispose();
        poisonIcon.dispose();
        invincibilityIcon.dispose();
        heartRecoveryIcon.dispose();
        font.dispose();
    }
}
