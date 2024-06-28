package cn.ningle.network;

import cn.ningle.network.nio.channel.LengthFieldBaseFrameEncoder;
import cn.ningle.network.nio.channel.LengthFieldBasedFrameDecoder;
import cn.ningle.network.nio.message.RequestMessage;
import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

/**
 * @author ningle
 * @version : TestLengthFiledBaseFrame.java, v 0.1 2024/06/28 12:00 ningle
 **/

public class TestLengthFiledBaseFrame {

    @Test
    public void test1() {
        RequestMessage requestMessage = new RequestMessage("https://xuziheng.cn");

        ByteBuffer byteBuffer = ByteBuffer.wrap(JSON.toJSONBytes(requestMessage));

        ByteBuffer buffer = new LengthFieldBaseFrameEncoder(4).encode(byteBuffer);

        ByteBuffer decodeBuffer = new LengthFieldBasedFrameDecoder(4).decode(buffer);

        RequestMessage requestMessage1 = JSON.parseObject(decodeBuffer.array(), RequestMessage.class);

        assert requestMessage1.getRequestURL().equals(requestMessage.getRequestURL());
    }

}
