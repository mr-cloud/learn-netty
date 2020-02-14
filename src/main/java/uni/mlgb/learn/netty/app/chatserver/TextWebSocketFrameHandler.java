package uni.mlgb.learn.netty.app.chatserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * - listen on handshake completion signal
 * - broadcast hello and words
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private ChannelGroup channelGroup;

    public TextWebSocketFrameHandler(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    /**
     * say hello
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            ctx.pipeline().remove(HttpReqHandler.class);
            this.channelGroup.add(ctx.channel());
            this.channelGroup.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined."));
        } else
            super.userEventTriggered(ctx, evt);
    }

    /**
     * broadcasting
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        this.channelGroup.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + ": " + msg.text()));
    }
}
