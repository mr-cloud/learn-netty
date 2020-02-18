package uni.mlgb.learn.netty.app.pubsub;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.net.InetSocketAddress;
import java.util.List;

public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {
    private InetSocketAddress recipient;

    public LogEventEncoder(InetSocketAddress receipt) {
        this.recipient = receipt;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, LogEvent msg, List<Object> out) {
        ByteBuf buf = ctx.alloc().buffer();
        buf.writeBytes(msg.dt.getBytes()).writeBytes(LogEvent.LOG_SEPARATOR.getBytes()).writeBytes(msg.msg.getBytes());
        out.add(new DatagramPacket(buf, recipient));
    }
}
