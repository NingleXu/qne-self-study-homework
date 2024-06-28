package cn.ningle.network.nio;

import cn.ningle.network.nio.channel.SocketChannelReadHandler;
import cn.ningle.network.nio.channel.SocketChannelWriteHandler;
import cn.ningle.network.nio.message.RequestMessage;
import cn.ningle.network.nio.message.ResponseMessage;
import com.google.common.base.Preconditions;

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

    private SocketChannelReadHandler readHandler;

    private SocketChannelWriteHandler writeHandler;


    public static Client getInstance() {
        return new Client();
    }

    private Client() {
    }

    public Client readHandler(SocketChannelReadHandler socketChannelReadHandler) {
        this.readHandler = socketChannelReadHandler;
        return this;
    }

    public Client writeHandler(SocketChannelWriteHandler socketChannelWriteHandler) {
        this.writeHandler = socketChannelWriteHandler;
        return this;
    }

    public Client serverAddress(InetSocketAddress netSocketAddress) {
        this.netSocketAddress = netSocketAddress;
        return this;
    }

    public Client start() {
        argsChecked();
        try {
            socketChannel = SocketChannel.open(netSocketAddress);
            socketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ);
            // 异步监听selector 的结果
            Thread clientWorker = new Thread(() -> {
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
                                read(sc);
                            }
                            iter.remove();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            clientWorker.setDaemon(true);
            clientWorker.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private void read(SocketChannel sc) throws IOException {
        ResponseMessage responseMessage = readHandler.readHandle(sc, ResponseMessage.class);

        // 由于半包黏包处理可能存在null的情况
        if (responseMessage != null) {
            System.out.println(responseMessage);
        }
    }

    public void doRequest(String websiteURL) throws IOException {
        RequestMessage requestMessage = new RequestMessage(websiteURL);
        writeHandler.writeHandler(socketChannel, requestMessage);
    }

    private void argsChecked() {
        Preconditions.checkArgument(null != readHandler, "readHandler is null");
        Preconditions.checkArgument(null != writeHandler, "writeHandler is null");
        Preconditions.checkArgument(null != netSocketAddress, "port is null");
    }

    @Override
    public void close() throws IOException {
        if (null != socketChannel) {
            socketChannel.close();
        }
    }
}
