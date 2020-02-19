package uni.mlgb.learn.netty.app.pubsub;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import uni.akilis.helper.LoggerX;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        InetSocketAddress recipient = msg.recipient();
        LoggerX.println("recipient", recipient.toString());

        ByteBuf buf = msg.content();
        String log = buf.toString(StandardCharsets.UTF_8);
        LoggerX.println("Decode content", log);
        try {
            int splitPos = log.indexOf(LogEvent.LOG_SEPARATOR);
            out.add(new LogEvent(msg.sender(), log.substring(splitPos + LogEvent.LOG_SEPARATOR.length()), log.substring(0, splitPos)));
        } catch (Exception e) {
            e.printStackTrace();
            LoggerX.error("Unparsable log", log);
        }
    }
}
