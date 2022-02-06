package hasan.mohamed.shehata.myapplication.servicesandnotifications;

public class RemainingTime {
    private int hours;
    private int minutes;
    private int seconds;

    public RemainingTime(int hours, int minutes, int seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    @Override
    public String toString() {
        return
                 String.valueOf(hours) + "h " +
                ", " + String.valueOf(minutes) + "m " +
                ", " + String.valueOf(seconds) + "s" +
                "Left";
    }
}
