package cn.ningle.network.nio.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author ningle
 * @version : DefaultSocketChannelWriter.java, v 0.1 2024/06/27 13:07 ningle
 **/
public class DefaultSocketChannelWriter implements SocketChannelWriter{
    @Override
    public void write(ByteBuffer byteBuffer, SocketChannel sc) throws IOException {
        sc.write(byteBuffer);
    }
}
