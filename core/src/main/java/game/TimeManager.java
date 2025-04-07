package game;

import java.util.Objects;

/**
 * Manages the game time cycle between day and night.
 *
 * @author Zoey Lee, Heather Kim
 * @version 2025
 */
public final class TimeManager {
    private static TimeManager instance; // Singleton instance of TimeManager

    private float timeElapsed; // Accumulates elapsed time since the last transition
    private boolean isDayTime; // True if it is currently day time; false if it is night
    private final float dayDuration; // Duration of the day period in seconds
    private final float nightDuration; // Duration of the night period in seconds

    private TimeManager(final float dayDuration, final float nightDuration) {
        this.dayDuration = dayDuration;
        this.nightDuration = nightDuration;
        this.isDayTime = true;
        this.timeElapsed = 0f;
    }

    /**
     * Retrieves the singleton instance of TimeManager.
     *
     * @return the TimeManager instance
     */
    public static TimeManager getInstance() {
        final float dayDuration = 90;
        final float nightDuration = 90;
        if (instance == null) {
            instance = new TimeManager(dayDuration, nightDuration); // each period lasts 3 minutes
        }
        return instance;
    }

    /**
     * Updates the time cycle by adding the delta time.
     *
     * @param delta a float representing the elapsed time in seconds since the last update
     */
    public void update(final float delta) {
        timeElapsed += delta;

        // Switch to night if it is day and time has elapsed
        if (isDayTime && timeElapsed >= dayDuration) {
            isDayTime = false;
            timeElapsed = 0;
            System.out.println("Switching to Night");
        } else if (!isDayTime && timeElapsed >= nightDuration) {
            isDayTime = true;
            timeElapsed = 0;
            System.out.println("Switching to Day");
            // Switch to day if it is night and time has elapsed
        }
    }

    /**
     * Returns whether it is currently day time.
     *
     * @return true if it is day time, false otherwise
     */
    public boolean getIsDayTime() {
        return this.isDayTime;
    }

    /**
     * Resets the time cycle to the default state.
     */
    public void reset() {
        isDayTime = true; // or whatever your default is
        timeElapsed = 0f; // Clear the elapsed time
    }

    /**
     * Determines if the TimeManager is equal to another object.
     *
     * @param obj the object to compare
     * @return true if the TimeManager is equal to the other object, false otherwise
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

        TimeManager timeManager = (TimeManager) obj;
        return Objects.equals(this.timeElapsed, timeManager.timeElapsed)
            && Objects.equals(this.isDayTime, timeManager.isDayTime)
            && Objects.equals(this.dayDuration, timeManager.dayDuration)
            && Objects.equals(this.nightDuration, timeManager.nightDuration);
    }

    /**
     * Gets the hashcode of the TimeManager.
     *
     * @return the hashcode of the TimeManager as an integer
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(this.timeElapsed);
        final int prime = 11;
        result = prime * result + Objects.hashCode(this.isDayTime);
        result = prime * result + Objects.hashCode(this.dayDuration);
        result = prime * result + Objects.hashCode(this.nightDuration);
        return result;
    }

    /**
     * Gets the string representation of the TimeManager.
     *
     * @return the string representation of the TimeManager
     */
    @Override
    public String toString() {
        final StringBuilder sb;
        sb = new StringBuilder("TimeManager {");
        sb.append("timeElapsed='").append(this.timeElapsed).append('\'');
        sb.append("isDayTime='").append(this.isDayTime).append('\'');
        sb.append("dayDuration='").append(this.dayDuration).append('\'');
        sb.append("nightDuration='").append(this.nightDuration);
        sb.append('}');
        return sb.toString();
    }
}
