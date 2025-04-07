package game;

import Sprites.PlayerInventory;
import com.badlogic.gdx.math.MathUtils;


/**
 * Crafts outcomes based on a recipe of required ingredients.
 *
 *  @author Zoey Lee, Heather Kim
 *  @version 2025
 */
public final class MysteryBox {
    private static MysteryBox instance;
    private int waterRequired;
    private int leafRequired;
    private int rockRequired;

    /*
     * Creates a MysteryBox with the specified ingredient requirements.
     */
    private MysteryBox(final int waterRequired, final int leafRequired, final int rockRequired) {
        this.waterRequired = waterRequired;
        this.leafRequired = leafRequired;
        this.rockRequired = rockRequired;
    }

    /**
     * Gets the singleton instance of MysteryBox.
     *
     * @return the singleton instance of MysteryBox
     */
    public static MysteryBox getInstance() {
        if (instance == null) {
            final int waterRequired = 5;
            final int leafRequired = 5;
            final int rockRequired = 5;
            instance = new MysteryBox(waterRequired, leafRequired, rockRequired);
        }
        return instance;
    }

    /**
     * Crafts an outcome based on the ingredients in the provided player inventory.
     * @param inventory the player's inventory
     * @return a MysteryBoxOutcome representing the result of crafting,
     * or null if there are not enough ingredients
     */
    public MysteryBoxOutcome craft(final PlayerInventory inventory) {
        if (!inventory.hasEnough(this.waterRequired, this.leafRequired, this.rockRequired)) {
            return null;
        }
        inventory.removeIngredients(this.waterRequired, this.leafRequired, this.rockRequired);

        float randomValue = MathUtils.random();
        final float failureThreshold = 0.35f;
        final float poisonThreshold = 0.6f;
        final float invincibilityThreshold = 0.85f;
        if (randomValue < failureThreshold) {
            return MysteryBoxOutcome.FAILURE; //35%
        } else if (randomValue < poisonThreshold) {
            return MysteryBoxOutcome.POISON; //25%
        } else if (randomValue < invincibilityThreshold) {
            return MysteryBoxOutcome.INVINCIBILITY; //25%
        } else {
            return MysteryBoxOutcome.HEART_RECOVERY; //15%
        }
    }

    /**
     * Gets the required water count.
     *
     * @return the water required as an integer
     */
    public int getWaterRequired() {
        return waterRequired;
    }

    /**
     * Gets the required leaf count.
     *
     * @return the leaf required as an integer
     */
    public int getLeafRequired() {
        return leafRequired;
    }

    /**
     * Gets the required rock count.
     *
     * @return the rock required as an integer
     */
    public int getRockRequired() {
        return rockRequired;
    }

    /**
     * Updates the recipe by incrementing the required ingredient amounts by one.
     */
    public void updateRecipe() {
        this.waterRequired++;
        this.leafRequired++;
        this.rockRequired++;
    }

    /**
     * Gets the string representation of the MysteryBox.
     *
     * @return the string representation of the MysteryBox
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MysteryBox{");
        sb.append("waterRequired=").append(waterRequired);
        sb.append(", leafRequired=").append(leafRequired);
        sb.append(", rockRequired=").append(rockRequired);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Determines if the MysteryBox is equal to another object.
     *
     * @param object the object to compare
     * @return true if the MysteryBox is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        MysteryBox that = (MysteryBox) object;
        return getWaterRequired() == that.getWaterRequired()
            && getLeafRequired() == that.getLeafRequired()
            && getRockRequired() == that.getRockRequired();
    }

    /**
     * Gets the hashcode of the MysteryBox.
     *
     * @return the hashcode of the MysteryBox as an integer
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = Integer.hashCode(getLeafRequired());
        result = prime * result + Integer.hashCode(getRockRequired());
        result = prime * result + Integer.hashCode(getWaterRequired());
        return result;
    }
}
