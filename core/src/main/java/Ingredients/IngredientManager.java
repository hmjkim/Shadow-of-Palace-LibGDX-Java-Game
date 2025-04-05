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

    // receives the walkable positions and loads the atlas
    public IngredientManager(final List<Vector2> walkablePositions) {
        this.ingredients = new Array<>();
        this.walkablePositions = walkablePositions;
        // Load the ingredients atlas
        ingredientAtlas = new TextureAtlas("ingredients/ingredients.atlas");
    }

    public void spawnIngredients(final int count) {
        ingredients.clear();
        // Make a copy of walkable positions and shuffle them
        List<Vector2> positions = new ArrayList<>(walkablePositions);
        Collections.shuffle(positions);

        // Spawn ingredients at distinct positions from the shuffled list
        for (int i = 0; i < count && i < positions.size(); i++) {
            Vector2 position = positions.get(i);
            IngredientType type;
            TextureAtlas.AtlasRegion region;
            int rand = MathUtils.random(2);
            if (rand == 0) {
                type = IngredientType.WATER;
                region = ingredientAtlas.findRegion("water");
            } else if (rand == 1) {
                type = IngredientType.STONE;
                region = ingredientAtlas.findRegion("stone");
            } else {
                type = IngredientType.HERB;
                region = ingredientAtlas.findRegion("herb");
            }
            // Create ingredient objects based on the random type
            Ingredient ingredient = new Ingredient(region, type);
            ingredient.setPosition(position.x - ingredient.getWidth() / 2, position.y - ingredient.getHeight() / 2);
            ingredients.add(ingredient);
        }
    }

    // Render all ingredients
    public void render(final SpriteBatch batch) {
        for (Ingredient ingredient : ingredients) {
            ingredient.draw(batch);
        }
    }

//    private Ingredient getNearbyIngredient(Vector2 playerPosition, float threshold) {
//        for (Ingredient ingredient : ingredients) {
//            // Calculate center of the ingredient
//            Vector2 center = new Vector2(
//                ingredient.getX() + ingredient.getWidth() / 2,
//                ingredient.getY() + ingredient.getHeight() / 2
//            );
//            if (center.dst(playerPosition) < threshold) {
//                return ingredient;
//            }
//        }
//        return null;
//    }

    public void collectNearbyIngredient(final Vector2 playerPosition, final float threshold, final PlayerInventory inventory) {
        for (int i = ingredients.size - 1; i >= 0; i--) {
            Ingredient ingredient = ingredients.get(i);
            Vector2 center = new Vector2(
                ingredient.getX() + ingredient.getWidth() / 2,
                ingredient.getY() + ingredient.getHeight() / 2
            );
            if (center.dst(playerPosition) < threshold) {
                inventory.addIngredient(ingredient.getType());
                System.out.println("Added " + ingredient.getType() + " to the inventory.");
                ingredients.removeIndex(i);
                return;  // Ingredient collected
            }
        }
    }


    public Array<Ingredient> getIngredients() {
        return ingredients;
    }
}
