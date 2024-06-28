package cn.ningle.network.nio.channel;

import java.nio.ByteBuffer;

/**
 * @author ningle
 * @version : Encoder.java, v 0.1 2024/06/28 11:20 ningle
 **/
public interface Encoder {

    ByteBuffer encode(ByteBuffer buffer);

}
