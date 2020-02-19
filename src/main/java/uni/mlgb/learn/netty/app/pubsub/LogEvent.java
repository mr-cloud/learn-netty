package uni.mlgb.learn.netty.app.pubsub;

import java.net.InetSocketAddress;

public class LogEvent {
    private InetSocketAddress src;
    public String msg;
    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public String dt;
    public static String LOG_SEPARATOR = ": ";

    public LogEvent(String msg, String dt) {
        this.msg = msg;
        this.dt = dt;
    }

    public LogEvent(InetSocketAddress src, String msg, String dt) {
        this(msg, dt);
        this.src = src;
    }

    @Override
    public String toString() {
        return "LogEvent{" +
                "src=" + src +
                ", msg='" + msg + '\'' +
                ", dt='" + dt + '\'' +
                '}';
    }
}
