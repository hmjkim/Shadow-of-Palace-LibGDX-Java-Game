package game;

import Sprites.PlayerInventory;
import com.badlogic.gdx.math.MathUtils;

public class MysteryBox {
    private static MysteryBox instance;
    private int waterRequired;
    private int leafRequired;
    private int rockRequired;

    private MysteryBox(final int waterRequired, final int leafRequired, final int rockRequired) {
        this.waterRequired = waterRequired;
        this.leafRequired = leafRequired;
        this.rockRequired = rockRequired;
    }

    public static MysteryBox getInstance() {
        if (instance == null) {
            instance = new MysteryBox(5, 5, 5);
        }
        return instance;
    }

    public MysteryBoxOutcome craft(PlayerInventory inventory) {
        if (!inventory.hasEnough(this.waterRequired, this.leafRequired, this.rockRequired)) {
            return null;
        }
        inventory.removeIngredients(this.waterRequired, this.leafRequired, this.rockRequired);

        float randomValue = MathUtils.random();
        if (randomValue < 0.35f) {
            return MysteryBoxOutcome.FAILURE; //35%
        } else if (randomValue < 0.60f) {
            return MysteryBoxOutcome.POISON; //25%
        } else if (randomValue < 0.85f) {
            return MysteryBoxOutcome.INVINCIBILITY; //25%
        } else {
            return MysteryBoxOutcome.HEART_RECOVERY; //15%
        }
    }

    public int getWaterRequired() {
        return waterRequired;
    }

    public int getLeafRequired() {
        return leafRequired;
    }

    public int getRockRequired() {
        return rockRequired;
    }

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
    public boolean equals(Object object) {
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
