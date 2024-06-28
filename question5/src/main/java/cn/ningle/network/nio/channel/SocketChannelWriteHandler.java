package cn.ningle.network.nio.channel;

import cn.ningle.network.nio.message.MessageCodec;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author ningle
 * @version : SocketChannelWriteHandler.java, v 0.1 2024/06/28 11:18 ningle
 **/
public class SocketChannelWriteHandler {
    private final MessageCodec messageCodec;

    private final Encoder encoder;

    public SocketChannelWriteHandler(MessageCodec messageCodec, Encoder encoder) {
        this.messageCodec = messageCodec;
        this.encoder = encoder;
    }

    public <T> void writeHandler(SocketChannel sc, T t) throws IOException {
        ByteBuffer buffer = messageCodec.encode(t);
        sc.write(encoder.encode(buffer));
    }
}
