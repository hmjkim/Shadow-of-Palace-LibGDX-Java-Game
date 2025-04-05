package Ingredients;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import game.MainGame;

public class Ingredient extends Sprite {
    private final IngredientType ingredientType;


    public Ingredient(final TextureAtlas.AtlasRegion region, final IngredientType ingredientType) {
        super(region);  // sets the sprite to the given region
        this.ingredientType = ingredientType;
        // Set the size based on the region's dimensions converted to world units
        setSize(region.getRegionWidth() / MainGame.PIXELS_PER_METER,
            region.getRegionHeight() / MainGame.PIXELS_PER_METER);
    }

    public IngredientType getType() {
        return ingredientType;
    }
}
