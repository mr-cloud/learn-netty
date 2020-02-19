package uni.mlgb.learn.netty.app.pubsub;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import uni.akilis.helper.LoggerX;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

// FIXME cannot broadcast message
public class LogEventBroadcaster {
    private final String logFilePath;
    private final int port;
    private NioEventLoopGroup group;
    private Channel channel;

    public LogEventBroadcaster(int port, String logFilePath) {
        this.port = port;
        this.logFilePath = logFilePath;
        this.group = new NioEventLoopGroup();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Args: --port <port number on which broadcast> --file <file path>");
        }

        new LogEventBroadcaster(Integer.parseInt(args[0]), args[1]).start();
    }

    private void start() {
        Bootstrap bootstrap = new Bootstrap()
                .group(this.group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                // TOKNOW 广播地址
                .handler(new LogEventEncoder(new InetSocketAddress("255.255.255.255", this.port)));
        this.channel = bootstrap.bind(0).syncUninterruptibly().channel();
        LoggerX.println("Bound address", this.channel.localAddress());
        try {
            RandomAccessFile raf = new RandomAccessFile(logFilePath, "r");
            long pos = raf.length();
            raf.seek(pos);
            while (true) {
                if (raf.length() > pos) {  // something new to read
                    String line = null;
                    while ((line = raf.readLine()) != null) {
                        int splitPos = line.indexOf(LogEvent.LOG_SEPARATOR);
                        try {
                            LoggerX.println("Read a line", line);
                            this.channel.write(new LogEvent(line.substring(splitPos + LogEvent.LOG_SEPARATOR.length()), line.substring(0, splitPos)));
                        } catch (Exception e) {
                            e.printStackTrace();
                            LoggerX.error("Unparsable log", line);
                        }
                    }
                    pos = raf.getFilePointer();
                } else {
                    TimeUnit.SECONDS.sleep(1);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            this.channel.close();
            this.group.shutdownGracefully();
        }
    }
}
