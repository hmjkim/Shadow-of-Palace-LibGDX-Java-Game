package Sprites;

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
public final class PlayerInventory {
    private static PlayerInventory instance;

    private int waterCount;
    private int herbCount;
    private int stoneCount;

    private int poisonItemCount;
    private int invincibilityItemCount;
    private int heartRecoveryItemCount;

    private final Texture playerInventory;

    private final BitmapFont font;

    private PlayerInventory() {
        this.waterCount = 0;
        this.herbCount = 0;
        this.stoneCount = 0;
        this.poisonItemCount = 0;
        this.invincibilityItemCount = 0;
        this.heartRecoveryItemCount = 0;

        this.playerInventory = new Texture("inventory/inventory.png");

        font = new BitmapFont();
        final float fontOffset = 0.84f;
        font.getData().setScale(fontOffset);
    }

    /**
     * Retrieves the singleton instance.
     *
     * @return the PlayerInventory instance
     */
    public static PlayerInventory getInstance() {
        if (instance == null) {
            instance = new PlayerInventory();
        }
        return instance;
    }

    /**
     * Returns the current poison item count.
     *
     * @return the poison item count
     */
    public int getPoisonItemCount() {
        return this.poisonItemCount;
    }

    /**
     * Removes one poison item.
     */
    public void removePoisonItem() {
        this.poisonItemCount--;
    }

    /**
     * Returns the current invincibility item count.
     *
     * @return the invincibility item count
     */
    public int getInvincibilityItemCount() {
        return this.invincibilityItemCount;
    }

    /**
     * Removes one invincibility item.
     */
    public void removeInvincibilityItem() {
        this.invincibilityItemCount--;
    }

    /**
     * Returns the current heart recovery item count.
     *
     * @return the heart recovery item count
     */
    public int getHeartRecoveryItemCount() {
        return this.heartRecoveryItemCount;
    }

    /**
     * Removes one heart recovery item.
     */
    public void removeHeartRecoveryItem() {
        this.heartRecoveryItemCount--;
    }

    /**
     * Adds one water ingredient.
     */
    public void addWater() {
        this.waterCount++;
    }

    /**
     * Adds one herb ingredient.
     */
    public void addHerb() {
        this.herbCount++;
    }

    /**
     * Adds one stone ingredient.
     */
    public void addStone() {
        this.stoneCount++;
    }

    /**
     * Adds one poison item.
     */
    public void addPoison() {
        this.poisonItemCount++;
    }

    /**
     * Adds one invincibility item.
     */
    public void addInvincibility() {
        this.invincibilityItemCount++;
    }

    /**
     * Adds one heart recovery item.
     */
    public void addHeartRecovery() {
        this.heartRecoveryItemCount++;
    }

    /**
     * Returns the water ingredient count.
     *
     * @return the water count
     */
    public int getWaterCount() {
        return this.waterCount;
    }

    /**
     * Returns the herb ingredient count.
     *
     * @return the herb count
     */
    public int getHerbCount() {
        return this.herbCount;
    }

    /**
     * Returns the stone ingredient count.
     *
     * @return the stone count
     */
    public int getStoneCount() {
        return this.stoneCount;
    }

    /**
     * Checks if there are enough ingredients to craft a mystery box.
     *
     * @param waterReq the required water count
     * @param herbReq the required herb count
     * @param stoneReq the required stone count
     * @return true if there are enough ingredients, false otherwise
     */
    public boolean hasEnough(final int waterReq, final int herbReq, final int stoneReq) {
        return waterCount >= waterReq && herbCount >= herbReq && stoneCount >= stoneReq;
    }

    /**
     * Removes specified amounts of ingredients after crafting a mystery box.
     *
     * @param waterReq the water count to remove
     * @param herbReq the herb count to remove
     * @param stoneReq the stone count to remove
     */
    public void removeIngredients(final int waterReq, final int herbReq, final int stoneReq) {
        this.waterCount -= waterReq;
        this.herbCount -= herbReq;
        this.stoneCount -= stoneReq;
    }

    /**
     * Draws the inventory UI.
     *
     * @param batch the SpriteBatch used for drawing
     * @param screenWidth the width of the screen in pixels
     */
    public void drawInventory(final SpriteBatch batch, final int screenWidth) {
        float x = (screenWidth - playerInventory.getWidth()) / 2f;
        final float y = 20;

        batch.draw(playerInventory, x, y);

        final int waterOffset = 25;
        final int leafOffset = 130;
        final int stoneOffset = 225;
        final int poisonOffset = 318;
        final int invincibilityOffset = 423;
        final int heartRecoveryOffset = 516;
        final int allYOffset = 70;

        font.draw(batch, "Water: " + waterCount, x + waterOffset,
            y + playerInventory.getHeight() - allYOffset);
        font.draw(batch, "Leaf: " + herbCount, x + leafOffset,
            y + playerInventory.getHeight() - allYOffset);
        font.draw(batch, "Stone: " + stoneCount, x + stoneOffset,
            y + playerInventory.getHeight() - allYOffset);
        font.draw(batch, "Poison: " + poisonItemCount, x + poisonOffset,
            y + playerInventory.getHeight() - allYOffset);
        font.draw(batch, "Hide: " + invincibilityItemCount, x + invincibilityOffset,
            y + playerInventory.getHeight() - allYOffset);
        font.draw(batch, "Heart: " + heartRecoveryItemCount, x + heartRecoveryOffset,
            y + playerInventory.getHeight() - allYOffset);
    }

    /**
     * Adds an ingredient based on its type.
     *
     * @param type the IngredientType to add
     * @throws IllegalArgumentException if the expected type is not recognized
     */
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
            default:
                throw new IllegalArgumentException("Unexpected type: " + type);
        }
    }

    /**
     * Disposes inventory resources.
     */
    public void dispose() {
        playerInventory.dispose();
        font.dispose();
    }

    /**
     * Sets the herb ingredient count.
     *
     * @param herbCount the new herb count
     */
    public void setHerbCount(final int herbCount) {
        this.herbCount = herbCount;
    }

    /**
     * Sets the stone ingredient count.
     *
     * @param stoneCount the new stone count
     */
    public void setStoneCount(final int stoneCount) {
        this.stoneCount = stoneCount;
    }

    /**
     * Sets the poison item count.
     *
     * @param poisonItemCount the new poison count
     */
    public void setPoisonItemCount(final int poisonItemCount) {
        this.poisonItemCount = poisonItemCount;
    }

    /**
     * Sets the invincibility item count.
     *
     * @param invincibilityItemCount the new invincibility count
     */
    public void setInvincibilityItemCount(final int invincibilityItemCount) {
        this.invincibilityItemCount = invincibilityItemCount;
    }

    /**
     * Sets the heart recovery item count.
     *
     * @param heartRecoveryItemCount the new heart recovery count
     */
    public void setHeartRecoveryItemCount(final int heartRecoveryItemCount) {
        this.heartRecoveryItemCount = heartRecoveryItemCount;
    }

    /**
     * Sets the water ingredient count.
     *
     * @param waterCount the new water count
     */
    public void setWaterCount(final int waterCount) {
        this.waterCount = waterCount;
    }

    /**
     * Resets the inventory counts.
     */
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

        PlayerInventory otherInventory = (PlayerInventory) obj;
        return Objects.equals(this.herbCount, otherInventory.herbCount)
            && Objects.equals(this.stoneCount, otherInventory.stoneCount)
            && Objects.equals(this.waterCount, otherInventory.waterCount)
            && Objects.equals(this.invincibilityItemCount, otherInventory.invincibilityItemCount)
            && Objects.equals(this.heartRecoveryItemCount, otherInventory.heartRecoveryItemCount)
            && Objects.equals(this.poisonItemCount, otherInventory.poisonItemCount)
            && Objects.equals(this.playerInventory, otherInventory.playerInventory)
            && Objects.equals(this.font, otherInventory.font);
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
