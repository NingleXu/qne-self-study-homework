package cn.ningle.network.nio.message;

import com.alibaba.fastjson2.JSON;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

/**
 * @author ningle
 * @version : MessageJSONCodec.java, v 0.1 2024/06/28 11:08 ningle
 **/
public class MessageJSONCodec implements MessageCodec {
    @Override
    public <T> T decode(ByteBuffer byteBuffer, Class<T> clazz) throws CharacterCodingException {
        CharBuffer charBuffer = CharBuffer.allocate(byteBuffer.remaining());
        Charset charset = StandardCharsets.UTF_8;
        CharsetDecoder decoder = charset.newDecoder();
        CoderResult result = decoder.decode(byteBuffer, charBuffer, true);
        if (result.isError()) {
            result.throwException();
        }
        charBuffer.flip();
        return JSON.parseObject(charBuffer.toString(), clazz);
    }

    @Override
    public <T> ByteBuffer encode(T t) {
        byte[] jsonBytes = JSON.toJSONBytes(t);
        return ByteBuffer.wrap(jsonBytes);
    }
}
