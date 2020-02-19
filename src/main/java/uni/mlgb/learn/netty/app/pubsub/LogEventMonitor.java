package uni.mlgb.learn.netty.app.pubsub;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import uni.akilis.helper.LoggerX;

import java.net.InetSocketAddress;

public class LogEventMonitor {
    private final int port;
    private final NioEventLoopGroup group;

    public LogEventMonitor(int port) {
        this.port = port;
        this.group = new NioEventLoopGroup();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Args: --port <port number on which monitor>");
        }

        new LogEventMonitor(Integer.parseInt(args[0])).start();
    }

    private void start() {
        Bootstrap bootstrap = new Bootstrap().group(this.group).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new LogEventDecoder()).addLast(new Captain());
                    }
                });
        ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(this.port)).syncUninterruptibly();  // TOKNOW need port?
        LoggerX.println("Bound address", channelFuture.channel().localAddress());
        channelFuture.channel().closeFuture().syncUninterruptibly();
    }
}
