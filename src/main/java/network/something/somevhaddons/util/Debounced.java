package network.something.somevhaddons.util;

import java.time.LocalDateTime;

public class Debounced {

    protected final int delayInSeconds;
    protected LocalDateTime lastCall = LocalDateTime.now();
    protected boolean skipTimeCheck = true;

    public Debounced(int delayInSeconds) {
        this.delayInSeconds = delayInSeconds;
    }

    public void run(Runnable runnable) {
        if (shouldCall()) {
            runnable.run();
        }
    }

    protected boolean shouldCall() {
        var now = LocalDateTime.now();
        if (skipTimeCheck || now.minusSeconds(delayInSeconds).isAfter(lastCall)) {
            lastCall = now;
            skipTimeCheck = false;
            return true;
        }
        return false;
    }

    /**
     * run will perform on next call
     */
    public void skipDelay() {
        skipTimeCheck = true;
    }

}
