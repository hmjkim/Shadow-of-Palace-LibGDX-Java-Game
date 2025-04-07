package Helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/**
 * Manages fonts.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public class FontManager {

    private static BitmapFont defaultFont;

    /**
     * Initializes the default font.
     */
    // Load the font once when the game starts
    public static void initialize() {
        if (defaultFont == null) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/upheaval.ttf"));
            FreeTypeFontParameter parameter = new FreeTypeFontParameter();
            parameter.size = 24; // Set the adjusted font size
            defaultFont = generator.generateFont(parameter); // Generate the font
            generator.dispose(); // Dispose the generator after creating the font
        }
    }

    /**
     * Return the default font for use in the game.
     *
     * @return the default BitmapFont
     */
    public static BitmapFont getDefaultFont() {
        if (defaultFont == null) {
            initialize(); // Initialize the font if it wasn't done yet
        }
        return defaultFont;
    }

    /**
     * Generates a new font.
     *
     * @param size an int representing the desired font size
     * @param color a Color representing the desired font color
     * @return a newly generated BitmapFont
     */
    public static BitmapFont generateFont(final int size, final Color color) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/upheaval.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = color;

        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

    /**
     * Dispose of the font when the game ends.
     */
    public static void dispose() {
        if (defaultFont != null) {
            defaultFont.dispose();
            defaultFont = null;
        }
    }
}
