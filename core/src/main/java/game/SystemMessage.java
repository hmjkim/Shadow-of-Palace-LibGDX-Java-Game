package game;

import com.badlogic.gdx.graphics.Color;

/**
 * Represents a system message that fades out over time.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public class SystemMessage {
    private final String text;
    private final Color color;
    private float timer;
    private final float displayDuration = 2f; // Fully visible
    private final float fadeDuration = 1.5f; // Then fades out

    /**
     * Creates a SystemMessage with the specified text and color.
     *
     * @param text  the message text
     * @param color the color of the message
     */
    public SystemMessage(final String text, final Color color) {
        this.text = text;
        this.color = color;
        this.timer = 0f;
    }

    /**
     * Updates the message timer.
     *
     * @param delta the time in seconds since the last update
     * @return true if the message has expired, false otherwise
     */
    public boolean update(final float delta) {
        timer += delta;
        return timer >= (displayDuration + fadeDuration);
    }

    /**
     * Gets the current alpha value for the message based on the timer.
     *
     * @return the current alpha value as a float
     */
    public float getAlpha() {
        if (timer <= displayDuration) {
            return 1f; // Fully visible
        } else {
            float fadeElapsed = timer - displayDuration;
            float fadeProgress = 1f - (fadeElapsed / fadeDuration);
            return Math.max(0f, fadeProgress);
        }
    }

    /**
     * Gets the message text.
     *
     * @return the message text as a String
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the colour of the message.
     *
     * @return the message color as a Color
     */
    public Color getColor() {
        return color;
    }

}

