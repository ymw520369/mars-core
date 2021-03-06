package org.alan.mars.protostuff;

/**
 * 消息基础类定义
 * Created on 2017/7/27.
 *
 * @author Alan
 * @since 1.0
 */
public class MarsMessage {
    /* 消息类型 */
    public int messageType;
    /* 子命令字*/
    public int cmd;
    /* 数据*/
    public byte[] data;

    public MarsMessage(int messageType, int cmd, byte[] data) {
        this.messageType = messageType;
        this.cmd = cmd;
        this.data = data;
    }
}
