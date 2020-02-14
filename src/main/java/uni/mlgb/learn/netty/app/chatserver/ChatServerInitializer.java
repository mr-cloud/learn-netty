package uni.mlgb.learn.netty.app.chatserver;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import uni.mlgb.learn.netty.util.ssl.BogusSslContextFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

public class ChatServerInitializer extends ChannelInitializer {
    private ChannelGroup channelGroup;

    public ChatServerInitializer(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline().addLast(new HttpServerCodec())
                .addLast(new ChunkedWriteHandler())  // allows to write a file content. processes chunk file when transfer html page as chunk within SSL
                .addLast(new HttpObjectAggregator(64 * 1024))
                .addLast(new HttpReqHandler("/ws"))
                .addLast(new WebSocketServerProtocolHandler("/ws"))  // process WebSocket handshake automatically. handles Ping/Pong/Close frames.
                .addLast(new TextWebSocketFrameHandler(this.channelGroup));
        // FIXME support SSL: certification is still considered as invalid by client.
        /*try {
            SSLContext context = BogusSslContextFactory.getInstance(true);
            SSLEngine sslEngine = context.createSSLEngine();
            sslEngine.setUseClientMode(false);
            ch.pipeline().addFirst(new SslHandler(sslEngine));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }*/
    }
}
