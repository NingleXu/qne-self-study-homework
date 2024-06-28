package cn.ningle.network.nio.message;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

/**
 * @author ningle
 * @version : MessageCodec.java, v 0.1 2024/06/27 11:43 ningle
 **/
public interface MessageCodec {

    <T> T decode(ByteBuffer byteBuffer, Class<T> clazz) throws CharacterCodingException;

    <T> ByteBuffer encode(T t);

}
