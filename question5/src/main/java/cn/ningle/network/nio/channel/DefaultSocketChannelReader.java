package cn.ningle.network.nio.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author ningle
 * @version : DefaultSocketChannelReader.java, v 0.1 2024/06/27 11:56 ningle
 **/
public class DefaultSocketChannelReader implements SocketChannelReader {
    @Override
    public ByteBuffer read(SocketChannel sc) throws IOException {
        // todo 考虑半包黏包
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        sc.read(byteBuffer);
        byteBuffer.flip();
        return byteBuffer.compact();
    }
}
