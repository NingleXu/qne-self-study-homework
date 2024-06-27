package cn.ningle.network.nio.message;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

/**
 * @author ningle
 * @version : MessageFormatter.java, v 0.1 2024/06/27 11:43 ningle
 **/
public interface MessageFormatter<T> {

    T decode(ByteBuffer byteBuffer) throws CharacterCodingException;

    ByteBuffer encode(T t);

}
