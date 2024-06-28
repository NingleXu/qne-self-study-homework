package cn.ningle.network.nio.channel;

import cn.ningle.network.nio.message.MessageCodec;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author ningle
 * @version : SocketChannelReadHandler.java, v 0.1 2024/06/28 10:47 ningle
 **/
public class SocketChannelReadHandler {

    private final MessageCodec messageCodec;

    private final Decoder decoder;

    public SocketChannelReadHandler(MessageCodec messageCodec, Decoder decoder) {
        this.messageCodec = messageCodec;
        this.decoder = decoder;
    }

    public <T> T readHandle(SocketChannel sc, Class<T> clazz) throws IOException {
        // 从SocketChannel 中读取数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        sc.read(byteBuffer);
        byteBuffer.flip();
        // maybe cache
        ByteBuffer decodeByteBuffer = decoder.decode(byteBuffer);

        if (null == decodeByteBuffer) {
            return null;
        }

        return messageCodec.decode(decodeByteBuffer, clazz);
    }


}
