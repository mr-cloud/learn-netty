package uni.mlgb.learn.netty.app.chatserver;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * serve HTML page
 */
public class HttpReqHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String wsUri;

    public HttpReqHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        if (this.wsUri.equals(msg.uri())) {
            ctx.fireChannelRead(msg.retain());
        }
        else {
            // ctx write a file
            // consider HTTP 100, keep alive, ssl
            if (HttpUtil.is100ContinueExpected(msg)) {
                send100Response(ctx);
            }
            RandomAccessFile file = new RandomAccessFile(new File(Thread.currentThread().getContextClassLoader().getResource("index.html").getFile()), "r");
            HttpResponse response = new DefaultHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
            response.headers().set(
                HttpHeaderNames.CONTENT_TYPE, "text/html"
            );
            boolean isKeepAlive = HttpUtil.isKeepAlive(msg);
            if (isKeepAlive) {
                HttpUtil.setKeepAlive(response, true);
                HttpUtil.setContentLength(response, file.length());
            }
            ctx.write(response);
            if (ctx.pipeline().get(SslHandler.class) == null) {  // zero-copy transfer
                ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!isKeepAlive)
                future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void send100Response(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
