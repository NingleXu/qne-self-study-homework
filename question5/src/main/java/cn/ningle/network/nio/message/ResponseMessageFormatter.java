package cn.ningle.network.nio.message;

import com.alibaba.fastjson2.JSON;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

/**
 * @author ningle
 * @version : ResponseMessageFormatter.java, v 0.1 2024/06/27 11:46 ningle
 **/
public class ResponseMessageFormatter implements MessageFormatter<ResponseMessage> {

    @Override
    public ResponseMessage decode(ByteBuffer byteBuffer) throws CharacterCodingException {
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        Charset charset = StandardCharsets.UTF_8;
        CharsetDecoder decoder = charset.newDecoder();
        byteBuffer.flip();
        CoderResult result = decoder.decode(byteBuffer, charBuffer, true);
        if (result.isError()) {
            result.throwException();
        }
        charBuffer.flip();
        return JSON.parseObject(charBuffer.array(), ResponseMessage.class);
    }

    @Override
    public ByteBuffer encode(ResponseMessage responseMessage) {
        byte[] jsonBytes = JSON.toJSONBytes(responseMessage);
        return ByteBuffer.wrap(jsonBytes);
    }

}
