package cn.ningle.network;

import cn.ningle.network.nio.Server;
import cn.ningle.network.nio.channel.LengthFieldBaseFrameEncoder;
import cn.ningle.network.nio.channel.LengthFieldBasedFrameDecoder;
import cn.ningle.network.nio.channel.SocketChannelReadHandler;
import cn.ningle.network.nio.channel.SocketChannelWriteHandler;
import cn.ningle.network.nio.message.MessageJSONCodec;

/**
 * @author ningle
 * @version : Main.java, v 0.1 2024/06/25 10:35 ningle
 **/
public class ServerBootstrap {
    public static void main(String[] args) {
        Server.getInstance()
                .port(8099)
                .readHandler(new SocketChannelReadHandler(
                        new MessageJSONCodec(),
                        new LengthFieldBasedFrameDecoder(4)
                ))
                .writeHandler(new SocketChannelWriteHandler(
                        new MessageJSONCodec(),
                        new LengthFieldBaseFrameEncoder(4)
                ))
                .start();
    }
}
