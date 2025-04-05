package Ingredients;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import game.MainGame;
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

    public void checkPlayerCollision(final Vector2 playerPosition, final PlayerInventory inventory) {
        for (int i = ingredients.size - 1; i >= 0; i--) {
            Ingredient ingredient = ingredients.get(i);
            if (ingredient.getBoundingRectangle().contains(playerPosition)) {
                // Add the ingredient to the inventory
                inventory.addIngredient(ingredient.getType());
                System.out.println("Added " + ingredient.getType() + " to the inventory.");
                // Remove the ingredient from the manager
                ingredients.removeIndex(i);
            }
        }
    }

    public Array<Ingredient> getIngredients() {
        return ingredients;
    }
}
