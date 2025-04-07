package game;

import java.util.Objects;

public class TimeManager {
    private static TimeManager instance;

    private float timeElapsed;
    private boolean isDayTime;
    private final float dayDuration;
    private final float nightDuration;

    private TimeManager(float dayDuration, float nightDuration) {
        this.dayDuration = dayDuration;
        this.nightDuration = nightDuration;
        this.isDayTime = true;
        this.timeElapsed = 0f;
    }

    public static TimeManager getInstance() {
        if (instance == null) {
            instance = new TimeManager(180f, 180f); //each time 3min
        }
        return instance;
    }

    public void update(float delta) {
        timeElapsed += delta;
        if (isDayTime && timeElapsed >= dayDuration) {
            isDayTime = false;
            timeElapsed = 0;
            System.out.println("Switching to Night");
        } else if (!isDayTime && timeElapsed >= nightDuration) {
            isDayTime = true;
            timeElapsed = 0;
            System.out.println("Switching to Day");
        }
    }

    public boolean getIsDayTime() {
        return this.isDayTime;
    }

    public void reset() {
        isDayTime = true; // or whatever your default is
        timeElapsed = 0f;
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
        return Objects.equals(this.timeElapsed, timeManager.timeElapsed) &&
            Objects.equals(this.isDayTime, timeManager.isDayTime) &&
            Objects.equals(this.dayDuration, timeManager.dayDuration) &&
            Objects.equals(this.nightDuration, timeManager.nightDuration);
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
