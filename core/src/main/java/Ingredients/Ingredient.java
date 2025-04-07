package Ingredients;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import game.MainGame;

import java.util.Objects;

/**
 * Represents an ingredient sprite.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public class Ingredient extends Sprite {
    private final IngredientType ingredientType;

    /**
     * Constructs an Ingredient with the specified region, ingredientType.
     *
     * @param region the texture region for the sprite
     * @param ingredientType the type of ingredient
     */
    public Ingredient(final TextureAtlas.AtlasRegion region, final IngredientType ingredientType) {
        super(region);  // sets the sprite to the given region
        this.ingredientType = ingredientType;
        // Set the size based on the region's dimensions converted to world units
        setSize(region.getRegionWidth() / MainGame.PIXELS_PER_METER,
            region.getRegionHeight() / MainGame.PIXELS_PER_METER);
    }

    /**
     * Returns the ingredient type.
     *
     * @return the ingredient type
     */
    public IngredientType getType() {
        return ingredientType;
    }

    /**
     * Determines if the Ingredient is equal to another object.
     *
     * @param obj the object to compare
     * @return true if the Ingredient is equal to the other object, false otherwise
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

        Ingredient ingredient = (Ingredient) obj;
        return super.equals(obj) && Objects.equals(this.ingredientType, ingredient.ingredientType);
    }

    /**
     * Gets the hashcode of the Ingredient.
     *
     * @return the hashcode of the Ingredient as an integer
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        final int prime = 12;
        result = prime * result + Objects.hashCode(this.ingredientType);
        return result;
    }

    /**
     * Gets the string representation of the Ingredient.
     *
     * @return the string representation of the Ingredient
     */
    @Override
    public String toString() {
        final StringBuilder sb;
        sb = new StringBuilder("Ingredient {");
        sb.append("regionHeight='").append(this.getRegionHeight()).append('\'');
        sb.append("regionWidth='").append(this.getRegionWidth()).append('\'');
        sb.append("regionHeightY='").append(this.getRegionY()).append('\'');
        sb.append("regionHeightX='").append(this.getRegionX()).append('\'');
        sb.append("ingredientType='").append(this.getType());
        sb.append('}');
        return super.toString() + sb;
    }
}
