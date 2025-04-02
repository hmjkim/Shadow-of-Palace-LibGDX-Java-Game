package game;

import com.badlogic.gdx.graphics.Color;

public class SystemMessage {
    private final String text;
    private final Color color;

    private float timer;
    private final float displayDuration = 2f; // Fully visible
    private final float fadeDuration = 1.5f; // Then fades out

    public SystemMessage(String text, Color color) {
        this.text = text;
        this.color = color;
        this.timer = 0f;
    }

    public boolean update(float delta) {
        timer += delta;
        return timer >= (displayDuration + fadeDuration);
    }

    public float getAlpha() {
        if (timer <= displayDuration) {
            return 1f; // Fully visible
        } else {
            float fadeElapsed = timer - displayDuration;
            float fadeProgress = 1f - (fadeElapsed / fadeDuration);
            return Math.max(0f, fadeProgress);
        }
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }
}

