package cn.ningle.network;

import cn.ningle.network.nio.Server;
import cn.ningle.network.nio.message.RequestMessageFormatter;
import cn.ningle.network.nio.message.ResponseMessageFormatter;

/**
 * @author ningle
 * @version : Main.java, v 0.1 2024/06/25 10:35 ningle
 **/
public class ServerBootstrap {
    public static void main(String[] args) {
        Server.getInstance()
                .port(8099)
                .requestFormatter(new RequestMessageFormatter())
                .responseFormatter(new ResponseMessageFormatter())
                .start();
    }
}
