package Ingredients;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import Sprites.PlayerInventory;
import game.TimeManager;

/**
 * Manages ingredients.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public class IngredientManager {
    private final Array<Ingredient> ingredients;
    private final List<Vector2> walkablePositions; // Precomputed positions from GameScreen
    private final TextureAtlas ingredientAtlas;

    /**
     * Constructs a IngredientManager with the specified walkablePositions.
     *
     * @param walkablePositions the precomputed walkable positions from GameScreen
     */
    // Receives the walkable positions and loads the atlas
    public IngredientManager(final List<Vector2> walkablePositions) {
        this.ingredients = new Array<>();
        this.walkablePositions = walkablePositions;
        // Load the ingredients atlas
        ingredientAtlas = new TextureAtlas("ingredients/ingredients.atlas");
    }

    /**
     * Spawns a fixed number of ingredients spread out over distinct walkable positions.
     *
     * @param count an int representing the number of ingredients to spawn
     */
    public void spawnIngredients(final int count) {
        ingredients.clear();
        // Make a copy of walkable positions and shuffle them
        List<Vector2> shuffledPositions = new ArrayList<>(walkablePositions);
        Collections.shuffle(shuffledPositions);

        // Spawn ingredients at distinct positions from the shuffled list
        for (int index = 0; index < count && index < shuffledPositions.size(); index++) {
            Vector2 position = shuffledPositions.get(index);
            Ingredient ingredient = createRandomIngredientAt(position);
            ingredients.add(ingredient);
        }
    }

    /**
     * Replenishes ingredients to reach the original count if any are missing.
     *
     * @param originalCount an int representing the original desired number of ingredients
     */
    public void resupplyIngredients(final int originalCount) {
        int missingCount = originalCount - ingredients.size;
        if (missingCount <= 0) {
            return;
        }

        // Make a copy of walkable positions and shuffle them for unique positions
        List<Vector2> shuffledPositions = new ArrayList<>(walkablePositions);
        Collections.shuffle(shuffledPositions);

        // Spawn missing ingredients using distinct positions from the shuffled list
        for (int index = 0; index < missingCount && index < shuffledPositions.size(); index++) {
            Vector2 position = shuffledPositions.get(index);
            Ingredient ingredient = createRandomIngredientAt(position);
            ingredients.add(ingredient);
            System.out.println("Repopulated " + ingredient.getType() + " at position ["
                + (position.x - ingredient.getWidth() / 2) + ", "
                + (position.y - ingredient.getHeight() / 2) + "]");
        }
    }

    /**
     * Collects a nearby ingredient if the player is within the given threshold.
     *
     * @param playerPosition a Vector2 representing the player's current position
     * @param threshold a float representing the distance threshold for collection
     * @param inventory a PlayerInventory representing the player's inventory to add the ingredient
     */
    public void collectNearbyIngredient(final Vector2 playerPosition, final float threshold,
                                        final PlayerInventory inventory) {
        for (int index = ingredients.size - 1; index >= 0; index--) {
            Ingredient ingredient = ingredients.get(index);
            Vector2 ingredientCenter = new Vector2(
                ingredient.getX() + ingredient.getWidth() / 2,
                ingredient.getY() + ingredient.getHeight() / 2
            );
            if (ingredientCenter.dst(playerPosition) < threshold) {
                inventory.addIngredient(ingredient.getType());
                System.out.println("Added " + ingredient.getType() + " to the inventory.");
                ingredients.removeIndex(index);
                return;  // Ingredient collected
            }
        }
    }

    /**
     * Renders all ingredients.
     *
     * @param batch the SpriteBatch used for rendering
     */
    public void render(final SpriteBatch batch) {
        // Only render ingredients if it's day time
        if (!TimeManager.getInstance().getIsDayTime()) {
            return;
        }
        for (Ingredient ingredient : ingredients) {
            ingredient.draw(batch);
        }
    }

    /**
     * Returns the ingredients array.
     *
     * @return the Array of active ingredients
     */
    public Array<Ingredient> getIngredients() {
        return ingredients;
    }

    // Private helper that creates a random ingredient at the given position,
    // centers the sprite on that position, and returns the Ingredient.
    private Ingredient createRandomIngredientAt(final Vector2 position) {
        IngredientType ingredientType;
        TextureAtlas.AtlasRegion region;
        int randomValue = MathUtils.random(2);
        if (randomValue == 0) {
            ingredientType = IngredientType.WATER;
            region = ingredientAtlas.findRegion("water");
        } else if (randomValue == 1) {
            ingredientType = IngredientType.STONE;
            region = ingredientAtlas.findRegion("stone");
        } else {
            ingredientType = IngredientType.HERB;
            region = ingredientAtlas.findRegion("herb");
        }
        Ingredient ingredient = new Ingredient(region, ingredientType);
        ingredient.setOriginCenter();
        ingredient.setPosition(position.x - ingredient.getWidth() / 2,
            position.y - ingredient.getHeight() / 2);
        return ingredient;
    }

    /**
     * Determines if the IngredientManager is equal to another object.
     *
     * @param obj the object to compare
     * @return true if the IngredientManager is equal to the other object, false otherwise
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

        IngredientManager ingredientManager = (IngredientManager) obj;
        return Objects.equals(this.ingredients, ingredientManager.ingredients)
            && Objects.equals(this.walkablePositions, ingredientManager.walkablePositions)
            && Objects.equals(this.ingredientAtlas, ingredientManager.ingredientAtlas);
    }

    /**
     * Gets the hashcode of the IngredientManager.
     *
     * @return the hashcode of the IngredientManager as an integer
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(this.ingredients);
        final int prime = 13;
        result = prime * result + Objects.hashCode(this.walkablePositions);
        result = prime * result + Objects.hashCode(this.ingredientAtlas);
        return result;
    }

    /**
     * Gets the string representation of the IngredientManager.
     *
     * @return the string representation of the IngredientManager
     */
    @Override
    public String toString() {
        final StringBuilder sb;
        sb = new StringBuilder("IngredientManager {");
        sb.append("ingredients='").append(this.ingredients).append('\'');
        sb.append("walkablePositions='").append(this.walkablePositions).append('\'');
        sb.append("ingredientAtlas='").append(this.ingredientAtlas);
        sb.append('}');
        return sb.toString();
    }
}
