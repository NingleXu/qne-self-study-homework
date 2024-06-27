package cn.ningle.network.nio;

import cn.ningle.network.nio.message.RequestMessageFormatter;
import cn.ningle.network.nio.message.ResponseMessageFormatter;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author ningle
 * @version : Server.java, v 0.1 2024/06/26 20:01 ningle
 **/
public class Server {

    private InetSocketAddress netSocketAddress;

    private ClientEventWorker clientEventWorker;

    private RequestMessageFormatter requestMessageFormatter;

    private ResponseMessageFormatter responseMessageFormatter;

    public static Server getInstance() {
        return new Server();
    }

    private Server() {
    }

    public Server port(int port) {
        netSocketAddress = new InetSocketAddress(port);
        return this;
    }

    public Server requestFormatter(RequestMessageFormatter requestMessageFormatter) {
        this.requestMessageFormatter = requestMessageFormatter;
        return this;
    }

    public Server responseFormatter(ResponseMessageFormatter responseMessageFormatter) {
        this.responseMessageFormatter = responseMessageFormatter;
        return this;
    }


    public Server start() {
        argsChecked();
        creatWorker();

        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            // 绑定启动端口
            ssc.bind(netSocketAddress);
            // 设置非阻塞
            ssc.configureBlocking(false);
            // 开启selector
            try (Selector s = Selector.open()) {
                // 对于ssc注册accept事件
                ssc.register(s, SelectionKey.OP_ACCEPT);
                for (; ; ) {
                    // 阻塞等待有事件触发
                    s.select();
                    Iterator<SelectionKey> it = s.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey sk = it.next();
                        // 处理客户端连接
                        if (sk.isAcceptable()) {
                            SocketChannel sc = ssc.accept();
                            sc.configureBlocking(false);
                            clientEventWorker.register(sc);
                        }
                        it.remove();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("ServerSocketChannel error", e);
        }

    }

    private void creatWorker() {
        clientEventWorker = new ClientEventWorker(requestMessageFormatter, responseMessageFormatter);
    }

    private void argsChecked() {
        Preconditions.checkArgument(null != requestMessageFormatter, "requestFormatter is null");
        Preconditions.checkArgument(null != responseMessageFormatter, "responseFormatter is null");
        Preconditions.checkArgument(null != netSocketAddress, "port is null");
    }

}
