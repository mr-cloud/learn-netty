package uni.mlgb.learn.netty.app.chatserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;
import uni.akilis.helper.LoggerX;

/**
 * Chat room based on WebSocket protocol.
 * 1. serve a HTML page as chat room UI by HTTP
 * 2. upgrade to WebSocket via HTTP hand shake.
 * 3. broadcast words from client by WebSocket.
 */
public class ChatServer {
    public static void main(String[] args) {
        if (args.length != 1) {
            LoggerX.error("Argument port is needed!");
            return;
        }
        new ChatServer(Integer.parseInt(args[0])).start();
    }

    private final int port;
    private ChannelGroup channelGroup;
    private EventLoopGroup group;
    private Channel channel;

    public ChatServer(int port) {
        this.port = port;
        this.channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
        LoggerX.println("#processor", Runtime.getRuntime().availableProcessors());
        this.group = new NioEventLoopGroup();


    }

    private void dispose() {
        if (this.channel != null) {
            this.channel.close();
        }
        this.channelGroup.close();
        this.group.shutdownGracefully();
    }

    private void start() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(this.group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChatServerInitializer(this.channelGroup));
        ChannelFuture future = bootstrap.bind(this.port).syncUninterruptibly();
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                dispose();
            }
        });
        this.channel = future.channel();
        this.channel.closeFuture().syncUninterruptibly();
    }


}
