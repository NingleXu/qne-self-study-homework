package cn.ningle.network;

import cn.ningle.network.nio.Client;
import cn.ningle.network.nio.channel.LengthFieldBaseFrameEncoder;
import cn.ningle.network.nio.channel.LengthFieldBasedFrameDecoder;
import cn.ningle.network.nio.channel.SocketChannelReadHandler;
import cn.ningle.network.nio.channel.SocketChannelWriteHandler;
import cn.ningle.network.nio.message.MessageJSONCodec;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @author ningle
 * @version : ClientBootstrap.java, v 0.1 2024/06/25 10:37 ningle
 **/
public class ClientBootstrap {
    public static void main(String[] args) {
        try (Client client = Client.getInstance()
                .readHandler(new SocketChannelReadHandler(
                        new MessageJSONCodec(),
                        new LengthFieldBasedFrameDecoder(4)
                ))
                .writeHandler(new SocketChannelWriteHandler(
                        new MessageJSONCodec(),
                        new LengthFieldBaseFrameEncoder(4)
                ))
                .serverAddress(new InetSocketAddress("127.0.0.1", 8099)
                ).start()) {
            Scanner sc = new Scanner(System.in);
            String webSizeURL;
            while (true) {
                webSizeURL = sc.nextLine();
                if (webSizeURL.equals("exit")) {
                    break;
                }
                client.doRequest(webSizeURL);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
