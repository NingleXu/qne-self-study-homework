package cn.ningle.network.nio.channel;

import java.nio.ByteBuffer;

import static cn.ningle.network.nio.utils.ByteBufferUtil.debugAll;

public class LengthFieldBasedFrameDecoder implements Decoder {

    private final int lengthFieldLength;
    private ByteBuffer cache;

    public LengthFieldBasedFrameDecoder(int lengthFieldLength) {
        if (lengthFieldLength != 1 && lengthFieldLength != 2 && lengthFieldLength != 4 && lengthFieldLength != 8) {
            throw new IllegalArgumentException("lengthFieldLength must be 1, 2, 4, or 8 bytes.");
        }
        this.lengthFieldLength = lengthFieldLength;
        this.cache = ByteBuffer.allocate(0);
    }

    @Override
    public ByteBuffer decode(ByteBuffer buffer) {
        // 将新数据合并到缓存中
        ByteBuffer combinedBuffer = ByteBuffer.allocate(cache.remaining() + buffer.remaining());
        combinedBuffer.put(cache);
        combinedBuffer.put(buffer);

        cache = combinedBuffer;
        cache.flip();
        // 检查是否有足够的数据来读取长度字段
        if (cache.remaining() < lengthFieldLength) {
            return null;
        }

        // 读取长度字段
        cache.mark();
        long messageLength;
        switch (lengthFieldLength) {
            case 1:
                messageLength = cache.get();
                break;
            case 2:
                messageLength = cache.getShort();
                break;
            case 4:
                messageLength = cache.getInt();
                break;
            case 8:
                messageLength = cache.getLong();
                break;
            default:
                throw new IllegalArgumentException("Unsupported lengthFieldLength: " + lengthFieldLength);
        }

        // 检查是否有足够的数据读取整个消息
        if (cache.remaining() < messageLength) {
            cache.reset();
            return null;
        }

        // 提取消息
        ByteBuffer messageBuffer = ByteBuffer.allocate((int) messageLength);
        cache.get(messageBuffer.array(), 0, (int) messageLength);

        // 更新缓存，移除已经处理的数据
        ByteBuffer newCache = ByteBuffer.allocate(cache.remaining());
        newCache.put(cache);
        newCache.flip();
        cache = newCache;

        return messageBuffer;
    }
}
