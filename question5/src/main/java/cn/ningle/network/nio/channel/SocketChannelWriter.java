package cn.ningle.network.nio.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author ningle
 * @version : SocketChannelWriter.java, v 0.1 2024/06/27 11:13 ningle
 **/
public interface SocketChannelWriter {

    void write(ByteBuffer byteBuffer, SocketChannel sc) throws IOException;

}
