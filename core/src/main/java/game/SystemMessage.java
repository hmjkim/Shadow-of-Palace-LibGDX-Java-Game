package game;

import com.badlogic.gdx.graphics.Color;

import java.util.Objects;

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


    /**
     * Determines if the SystemMessage is equal to another object.
     *
     * @param object the object to compare
     * @return true if the SystemMessage is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        SystemMessage that = (SystemMessage) object;
        return Float.compare(timer, that.timer) == 0
            && Objects.equals(getText(), that.getText())
            && Objects.equals(getColor(), that.getColor());
    }

    /**
     * Gets the hashcode of the SystemMessage.
     *
     * @return the hashcode of the SystemMessage as an integer
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = Objects.hash(text, color);
        result = prime * result + Float.hashCode(timer);
        result = prime * result + Float.hashCode(displayDuration);
        result = prime * result + Float.hashCode(fadeDuration);
        return result;
    }

    /**
     * Gets the string representation of the SystemMessage.
     *
     * @return the string representation of the SystemMessage
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SystemMessage{");
        sb.append("text='").append(text).append('\'');
        sb.append(", color=").append(color);
        sb.append(", timer=").append(timer);
        sb.append(", displayDuration=").append(displayDuration);
        sb.append(", fadeDuration=").append(fadeDuration);
        sb.append('}');
        return sb.toString();
    }
}

