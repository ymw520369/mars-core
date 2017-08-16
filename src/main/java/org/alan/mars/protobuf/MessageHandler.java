package org.alan.mars.protobuf;

import org.alan.mars.net.MarsSession;
import org.alan.mars.protobuf.PbMessage.TXMessage;

/**
 * Created on 2017/8/2.
 *
 * @author Alan
 * @since 1.0
 */
public interface MessageHandler {
    /**
     * 消息处理方法
     *
     * @param session
     * @param msg
     */
    void handle(MarsSession session, TXMessage msg);
}
