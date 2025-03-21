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

    public void updateRecipe() {
        this.waterRequired++;
        this.leafRequired++;
        this.rockRequired++;
    }
}
