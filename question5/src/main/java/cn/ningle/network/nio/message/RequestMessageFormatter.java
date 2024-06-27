package cn.ningle.network.nio.message;

import com.alibaba.fastjson2.JSON;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

/**
 * @author ningle
 * @version : RequestMessageFormatter.java, v 0.1 2024/06/27 11:45 ningle
 **/
public class RequestMessageFormatter implements MessageFormatter<RequestMessage> {
    @Override
    public RequestMessage decode(ByteBuffer byteBuffer) throws CharacterCodingException {
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        Charset charset = StandardCharsets.UTF_8;
        CharsetDecoder decoder = charset.newDecoder();
        byteBuffer.flip();
        CoderResult result = decoder.decode(byteBuffer, charBuffer, true);
        if (result.isError()) {
            result.throwException();
        }
        charBuffer.flip();
        return JSON.parseObject(charBuffer.array(), RequestMessage.class);
    }

    @Override
    public ByteBuffer encode(RequestMessage requestMessage) {
        byte[] jsonBytes = JSON.toJSONBytes(requestMessage);
        return ByteBuffer.wrap(jsonBytes);
    }
}
