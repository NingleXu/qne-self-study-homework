package cn.ningle.network.nio.channel;

import java.nio.ByteBuffer;

/**
 * @author ningle
 * @version : Decoder.java, v 0.1 2024/06/28 11:00 ningle
 **/
public interface Decoder {

    ByteBuffer decode(ByteBuffer buffer);

}
