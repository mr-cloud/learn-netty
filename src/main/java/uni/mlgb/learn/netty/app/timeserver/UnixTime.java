package uni.mlgb.learn.netty.app.timeserver;

import java.util.Date;

public class UnixTime {
    private long millis;

    public UnixTime(long millis) {
        this.millis = millis;
    }

    public UnixTime(int secs) {
        this.millis = secs * 1000L;
    }

    public Date getTime() {
        return new Date(this.millis);
    }

    public int getSec() {
        return (int) (millis/1000);
    }
}
