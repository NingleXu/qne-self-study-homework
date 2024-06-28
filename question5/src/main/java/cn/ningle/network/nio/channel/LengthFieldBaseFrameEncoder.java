package cn.ningle.network.nio.channel;

import java.nio.ByteBuffer;

public class LengthFieldBaseFrameEncoder implements Encoder {

    private final int lengthFieldLength;

    public LengthFieldBaseFrameEncoder(int lengthFieldLength) {
        if (lengthFieldLength != 1 && lengthFieldLength != 2 && lengthFieldLength != 4 && lengthFieldLength != 8) {
            throw new IllegalArgumentException("lengthFieldLength must be 1, 2, 4, or 8 bytes.");
        }
        this.lengthFieldLength = lengthFieldLength;
    }

    @Override
    public ByteBuffer encode(ByteBuffer buffer) {
        // 获取数据长度
        int dataLength = buffer.remaining();

        // 创建新的 ByteBuffer，容量为原始数据的长度加上长度字段的长度
        ByteBuffer newBuffer = ByteBuffer.allocate(lengthFieldLength + dataLength);

        // 根据长度字段的长度，将长度写入新缓冲区
        switch (lengthFieldLength) {
            case 1:
                newBuffer.put((byte) dataLength);
                break;
            case 2:
                newBuffer.putShort((short) dataLength);
                break;
            case 4:
                newBuffer.putInt(dataLength);
                break;
            case 8:
                newBuffer.putLong(dataLength);
                break;
            default:
                throw new IllegalArgumentException("Unsupported lengthFieldLength: " + lengthFieldLength);
        }

        // 写入原始数据
        newBuffer.put(buffer);

        // 切换缓冲区为读模式
        newBuffer.flip();

        return newBuffer;
    }
}
