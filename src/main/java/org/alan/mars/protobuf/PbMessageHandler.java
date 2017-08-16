/**
 * Copyright Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 * <p>
 * 2017年2月8日
 */
package org.alan.mars.protobuf;

import org.alan.mars.net.MarsSession;
import org.alan.mars.protobuf.PbMessage.TXMessage;

/**
 *
 * pb类型消息处理器接口
 *
 * @scene 1.0
 *
 * @author Alan
 *
 */
public interface PbMessageHandler extends MessageHandler{

    /**
     * 处理器可处理的消息类型值
     *
     * @return
     */
    int getMessageType();

    /**
     * 消息处理方法
     *
     * @param session
     * @param msg
     */
    @Override
    void handle(MarsSession session, TXMessage msg);
}
