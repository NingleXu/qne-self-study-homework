package cn.ningle.network.nio;

import cn.ningle.network.nio.channel.DefaultSocketChannelReader;
import cn.ningle.network.nio.channel.DefaultSocketChannelWriter;
import cn.ningle.network.nio.channel.SocketChannelReader;
import cn.ningle.network.nio.channel.SocketChannelWriter;
import cn.ningle.network.nio.message.RequestMessage;
import cn.ningle.network.nio.message.RequestMessageFormatter;
import cn.ningle.network.nio.message.ResponseMessage;
import cn.ningle.network.nio.message.ResponseMessageFormatter;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author ningle
 * @version : Client.java, v 0.1 2024/06/26 20:01 ningle
 **/
public class Client implements Closeable {

    private InetSocketAddress netSocketAddress;

    private SocketChannel socketChannel;

    private RequestMessageFormatter requestMessageFormatter;

    private ResponseMessageFormatter responseMessageFormatter;

    private final SocketChannelReader socketChannelReader = new DefaultSocketChannelReader();

    private final SocketChannelWriter socketChannelWriter = new DefaultSocketChannelWriter();

    public static Client getInstance() {
        return new Client();
    }

    private Client() {
    }

    public Client requestFormatter(RequestMessageFormatter requestMessageFormatter) {
        this.requestMessageFormatter = requestMessageFormatter;
        return this;
    }

    public Client responseFormatter(ResponseMessageFormatter responseMessageFormatter) {
        this.responseMessageFormatter = responseMessageFormatter;
        return this;
    }

    public Client serverAddress(InetSocketAddress netSocketAddress) {
        this.netSocketAddress = netSocketAddress;
        return this;
    }

    public Client start() {
        try {
            socketChannel = SocketChannel.open(netSocketAddress);
            socketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ);
            // 异步监听selector 的结果
            new Thread(() -> {
                // 监听读事件
                for (; ; ) {
                    try {
                        selector.select();

                        Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

                        while (iter.hasNext()) {
                            SelectionKey key = iter.next();
                            //处理读写
                            if (key.isReadable()) {
                                SocketChannel sc = (SocketChannel) key.channel();
                                readHandler(sc);
                            }
                            iter.remove();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private void readHandler(SocketChannel sc) throws IOException {
        ResponseMessage responseMessage = responseMessageFormatter.decode(socketChannelReader.read(sc));
        System.out.println(responseMessage);
    }

    public void doRequest(String websiteURL) throws IOException {
        RequestMessage requestMessage = new RequestMessage(websiteURL);
        socketChannel.write(requestMessageFormatter.encode(requestMessage));
    }

    @Override
    public void close() throws IOException {
        if (null != socketChannel) {
            socketChannel.close();
        }
    }
}
