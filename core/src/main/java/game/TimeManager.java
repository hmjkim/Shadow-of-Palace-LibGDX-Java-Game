package game;

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
            instance = new TimeManager(300f, 300f); //each time 5min
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
}
