package Sprites;

import Ingredients.Ingredient;
import Ingredients.IngredientType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Objects;

/**
 * Manages the player's inventory including ingredients and items.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public class PlayerInventory {
    private int waterCount;
    private int herbCount;
    private int stoneCount;

    private int poisonItemCount;
    private int invincibilityItemCount;
    private int heartRecoveryItemCount;

    private final Texture playerInventory;

    private final BitmapFont font;

    private static PlayerInventory instance;

    private PlayerInventory() {
        this.waterCount = 0;
        this.herbCount = 0;
        this.stoneCount = 0;
        this.poisonItemCount = 0;
        this.invincibilityItemCount = 0;
        this.heartRecoveryItemCount = 0;

        this.playerInventory = new Texture("inventory/inventory.png");

        font = new BitmapFont();
        font.getData().setScale(0.84f);
    }

    public static PlayerInventory getInstance() {
        if (instance == null) {
            instance = new PlayerInventory();
        }
        return instance;
    }

    public int getPoisonItemCount() {
        return this.poisonItemCount;
    }

    public void removePoisonItem() {
        this.poisonItemCount--;
    }

    public int getInvincibilityItemCount() {
        return this.invincibilityItemCount;
    }

    public void removeInvincibilityItem() {
        this.invincibilityItemCount--;
    }

    public int getHeartRecoveryItemCount() {
        return this.heartRecoveryItemCount;
    }

    public void removeHeartRecoveryItem() {
        this.heartRecoveryItemCount--;
    }

    public void addWater() {
        this.waterCount++;
    }

    public void addHerb() {
        this.herbCount++;
    }

    public void addStone() {
        this.stoneCount++;
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

    public int getWaterCount() {
        return this.waterCount;
    }

    public int getHerbCount() {
        return this.herbCount;
    }

    public int getStoneCount() {
        return this.stoneCount;
    }

    public boolean hasEnough(int waterReq, int herbReq, int stoneReq) {
        return waterCount >= waterReq && herbCount >= herbReq && stoneCount >= stoneReq;
    }

    public void removeIngredients(int waterReq, int herbReq, int stoneReq) {
        this.waterCount -= waterReq;
        this.herbCount -= herbReq;
        this.stoneCount -= stoneReq;
    }

    public void drawInventory(SpriteBatch batch, int screenWidth) {
        float x = (screenWidth - playerInventory.getWidth()) / 2f;
        float y = 20;

        batch.draw(playerInventory, x, y);

        font.draw(batch, "Water: " + waterCount, x + 25, y + playerInventory.getHeight() - 70);
        font.draw(batch, "Leaf: " + herbCount, x + 130, y + playerInventory.getHeight() - 70);
        font.draw(batch, "Stone: " + stoneCount, x + 225, y + playerInventory.getHeight() - 70);
        font.draw(batch, "Poison: " + poisonItemCount, x + 318, y + playerInventory.getHeight() - 70);
        font.draw(batch, "Hide: " + invincibilityItemCount, x + 423, y + playerInventory.getHeight() - 70);
        font.draw(batch, "Heart: " + heartRecoveryItemCount, x + 516, y + playerInventory.getHeight() - 70);}

    public void addIngredient(final IngredientType type) {
        switch (type) {
            case WATER:
                addWater();
                break;
            case STONE:
                addStone();
                break;
            case HERB:
                addHerb();
                break;
        }
    }

    public void dispose() {
        playerInventory.dispose();
        font.dispose();
    }

    public void setHerbCount(int herbCount) {
        this.herbCount = herbCount;
    }

    public void setStoneCount(int stoneCount) {
        this.stoneCount = stoneCount;
    }

    public void setPoisonItemCount(int poisonItemCount) {
        this.poisonItemCount = poisonItemCount;
    }

    public void setInvincibilityItemCount(int invincibilityItemCount) {
        this.invincibilityItemCount = invincibilityItemCount;
    }

    public void setHeartRecoveryItemCount(int heartRecoveryItemCount) {
        this.heartRecoveryItemCount = heartRecoveryItemCount;
    }

    public void setWaterCount(final int waterCount) {
        this.waterCount = waterCount;
    }

    public void reset() {
        waterCount = 0;
        herbCount = 0;
        stoneCount = 0;
        poisonItemCount = 0;
        invincibilityItemCount = 0;
        heartRecoveryItemCount = 0;
    }

    /**
     * Determines if the PlayerInventory is equal to another object.
     *
     * @param obj the object to compare
     * @return true if the PlayerInventory is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj.getClass() == this.getClass())) {
            return false;
        }

        PlayerInventory playerInventory = (PlayerInventory) obj;
        return Objects.equals(this.herbCount, playerInventory.herbCount)
            && Objects.equals(this.stoneCount, playerInventory.stoneCount)
            && Objects.equals(this.waterCount, playerInventory.waterCount)
            && Objects.equals(this.invincibilityItemCount, playerInventory.invincibilityItemCount)
            && Objects.equals(this.heartRecoveryItemCount, playerInventory.heartRecoveryItemCount)
            && Objects.equals(this.poisonItemCount, playerInventory.poisonItemCount)
            && Objects.equals(this.playerInventory, playerInventory.playerInventory)
            && Objects.equals(this.font, playerInventory.font);
    }

    /**
     * Gets the hashcode of the PlayerInventory.
     *
     * @return the hashcode of the PlayerInventory as an integer
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(this.waterCount);
        final int prime = 18;
        result = prime * result + Objects.hashCode(this.playerInventory);
        result = prime * result + Objects.hashCode(this.herbCount);
        result = prime * result + Objects.hashCode(this.stoneCount);
        result = prime * result + Objects.hashCode(this.poisonItemCount);
        result = prime * result + Objects.hashCode(this.invincibilityItemCount);
        result = prime * result + Objects.hashCode(this.heartRecoveryItemCount);
        result = prime * result + Objects.hashCode(this.font);
        return result;
    }

    /**
     * Gets the string representation of the PlayerInventory.
     *
     * @return the string representation of the PlayerInventory
     */
    @Override
    public String toString() {
        final StringBuilder sb;
        sb = new StringBuilder("PlayerInventory {");
        sb.append("waterCount='").append(this.waterCount).append('\'');
        sb.append("stoneCount='").append(this.stoneCount).append('\'');
        sb.append("herbCount='").append(this.herbCount).append('\'');
        sb.append("poisonItemCount='").append(this.poisonItemCount).append('\'');
        sb.append("invincibilityItemCount='").append(this.invincibilityItemCount).append('\'');
        sb.append("heartRecoveryItemCount='").append(this.heartRecoveryItemCount).append('\'');
        sb.append("playerInventory='").append(this.playerInventory).append('\'');
        sb.append("font='").append(this.font);
        sb.append('}');
        return sb.toString();
    }
}
