package uni.mlgb.learn.netty.app.pubsub;

public class LogEvent {
    public String msg;
    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public String dt;
    public static String LOG_SEPARATOR = ": ";

    public LogEvent(String line, String dt) {
        this.msg = line;
        this.dt = dt;
    }

    @Override
    public String toString() {
        return "LogEvent{" +
                "msg='" + msg + '\'' +
                ", dt='" + dt + '\'' +
                '}';
    }
}
