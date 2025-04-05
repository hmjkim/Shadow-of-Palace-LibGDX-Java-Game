package Ingredients;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Sprites.PlayerInventory;

public class IngredientManager {
    private final Array<Ingredient> ingredients;
    private final List<Vector2> walkablePositions; // Precomputed positions from GameScreen
    private final TextureAtlas ingredientAtlas;

    // Receives the walkable positions and loads the atlas
    public IngredientManager(final List<Vector2> walkablePositions) {
        this.ingredients = new Array<>();
        this.walkablePositions = walkablePositions;
        // Load the ingredients atlas
        ingredientAtlas = new TextureAtlas("ingredients/ingredients.atlas");
    }

    // Spawns a fixed number of ingredients spread out over distinct walkable positions
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

    // Replenishes ingredients to reach the original count if any are missing
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

    // Collects a nearby ingredient if the player is within the given threshold
    public void collectNearbyIngredient(final Vector2 playerPosition, final float threshold, final PlayerInventory inventory) {
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

    // Randomly picks one position from the precomputed list of walkable positions
    public Vector2 getRandomWalkablePosition() {
        if (walkablePositions.isEmpty()) {
            // Fallback if no walkable tile found
            return new Vector2(0, 0);
        }
        int randomIndex = MathUtils.random(walkablePositions.size() - 1);
        return walkablePositions.get(randomIndex);
    }

    // Renders all ingredients
    public void render(final SpriteBatch batch) {
        for (Ingredient ingredient : ingredients) {
            ingredient.draw(batch);
        }
    }

    public Array<Ingredient> getIngredients() {
        return ingredients;
    }

    // Private helper that creates a random ingredient at the given position,
    // centers the sprite on that position, and returns the Ingredient.
    private Ingredient createRandomIngredientAt(Vector2 position) {
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
        ingredient.setPosition(position.x - ingredient.getWidth() / 2, position.y - ingredient.getHeight() / 2);
        return ingredient;
    }
}
