package uni.mlgb.learn.netty.app.pubsub;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import uni.akilis.helper.LoggerX;

import java.util.Date;

public class Captain extends SimpleChannelInboundHandler<LogEvent> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogEvent msg) {
        LoggerX.println(new Date().toString(), msg.toString());
    }
}
