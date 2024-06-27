package cn.ningle.network.nio;

import cn.ningle.network.nio.channel.DefaultSocketChannelReader;
import cn.ningle.network.nio.channel.DefaultSocketChannelWriter;
import cn.ningle.network.nio.channel.SocketChannelReader;
import cn.ningle.network.nio.channel.SocketChannelWriter;
import cn.ningle.network.nio.http.HttpRequestHandler;
import cn.ningle.network.nio.message.RequestMessage;
import cn.ningle.network.nio.message.RequestMessageFormatter;
import cn.ningle.network.nio.message.ResponseMessage;
import cn.ningle.network.nio.message.ResponseMessageFormatter;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author ningle
 * @version : ClientEventHandler.java, v 0.1 2024/06/26 21:28 ningle
 **/
public class ClientEventWorker implements Runnable {

    /**
     * 专门用于select客户端事件的selector
     */
    private Selector clientEventSelector;

    private final RequestMessageFormatter requestMessageFormatter;

    private final ResponseMessageFormatter responseMessageFormatter;

    private final SocketChannelReader socketChannelReader = new DefaultSocketChannelReader();

    private final SocketChannelWriter socketChannelWriter = new DefaultSocketChannelWriter();

    private final ThreadPoolExecutor RequestMessageHandlerExecutors = new ThreadPoolExecutor(
            10,
            20,
            1000,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(100),
            new ThreadPoolExecutor.AbortPolicy()
    );

    /**
     * Worker是否初始化
     */
    private final AtomicBoolean isInit = new AtomicBoolean(false);

    public ClientEventWorker(RequestMessageFormatter requestMessageFormatter, ResponseMessageFormatter responseMessageFormatter) {
        this.requestMessageFormatter = requestMessageFormatter;
        this.responseMessageFormatter = responseMessageFormatter;
    }


    public void register(SocketChannel sc) throws IOException {
        // 初始化worker
        if (isInit.compareAndSet(false, true)) {
            // 监听与派发客户端事件的线程
            Thread clientEventHandleThread = new Thread(this);
            clientEventSelector = Selector.open();
            clientEventHandleThread.start();
        }
        sc.register(clientEventSelector, SelectionKey.OP_READ, null);
        clientEventSelector.wakeup();
    }

    @Override
    public void run() {
        // 监听读事件
        for (; ; ) {
            try {
                clientEventSelector.select(100);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Iterator<SelectionKey> iter = clientEventSelector.selectedKeys().iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (!key.isValid()) {
                    continue;
                }
                //处理读写
                if (key.isReadable()) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    try {
                        readHandler(sc);
                    } catch (IOException e) {
                        key.cancel();
                        try {
                            sc.close();
                        } catch (IOException ex) {
                            System.out.println("异常..." + ex.getMessage());
                            throw new RuntimeException(ex);
                        }
                        System.out.println("error: " + e.getMessage());
                    }
                }

            }
        }
    }

    void readHandler(SocketChannel sc) throws IOException {

        try {
            RequestMessage requestMessage = requestMessageFormatter.decode(socketChannelReader.read(sc));

            // RequestMessageHandlerExecutors线程池执行处理 RequestMessage
            RequestMessageHandlerExecutors.submit(() -> {
                // HttpRequestHandler的requestExecutors 处理执行http
                CompletableFuture<ResponseMessage> completableFuture =
                        CompletableFuture.supplyAsync(() -> HttpRequestHandler.doRequest(requestMessage), HttpRequestHandler.REQUEST_EXECUTORS);

                // 超时处理
                ResponseMessage responseMessage;
                try {
                    responseMessage = completableFuture.get(3000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | ExecutionException e) {
                    responseMessage = ResponseMessage.error(requestMessage.getRequestURL(),
                            "server handle request error,reason:" + e.getCause());
                } catch (TimeoutException e) {
                    responseMessage = ResponseMessage.error(requestMessage.getRequestURL(),
                            "server handle request timeout");
                }

                // do response
                try {
                    socketChannelWriter.write(responseMessageFormatter.encode(responseMessage), sc);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new IOException(e);
        }

    }


}
