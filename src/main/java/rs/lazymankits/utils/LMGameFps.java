package rs.lazymankits.utils;

public class LMGameFps {
    public static final int INTERVAL_FPS;
    public static final long PERIOD;
    public static long MAX_INTERVAL;
    private double currFps;
    private long interval;
    private long totalTime;
    private long count;

    static {
        INTERVAL_FPS = 8;
        PERIOD = (long) (1.0 / INTERVAL_FPS * 1000000000);
        MAX_INTERVAL = 1000000000L;
    }
    
    public LMGameFps() {
        currFps = 0;
        interval = 0L;
        totalTime = 0L;
        count = 0L;
    }

    public void calculate() {
        count++;
        interval += PERIOD;
        if (interval > MAX_INTERVAL) {
            long currTime = System.nanoTime();
            currFps = ((double) count / (currTime - totalTime)) * MAX_INTERVAL;
            count = 0L;
            interval = 0L;
            totalTime = currTime;
        }
    }

    public void setCurrFps(double currFps) {
        this.currFps = currFps;
    }

    public double getCurrFps() {
        return currFps;
    }
}