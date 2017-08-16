/**
 * Copyright Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 * <p>
 * 2017年2月8日
 */
package org.alan.mars.net;

import com.google.protobuf.GeneratedMessage;
import org.alan.mars.protobuf.MessageHandler;
import org.alan.mars.protobuf.PbMessage.TXMessage;
import org.alan.mars.protobuf.PbMessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户session
 *
 * @author Alan
 * @scene 1.0
 */
public class MarsSession extends Session<GeneratedMessage, TXMessage> implements Inbox<TXMessage> {

    protected Logger log = LoggerFactory.getLogger(getClass());

    protected MessageHandler handler;

    public MarsSession(String id, NetAddress netAddress, Connect connect) {
        super(id, connect, netAddress);
    }

    public void setHandler(MessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public void send(GeneratedMessage gmsg) {
        TXMessage msg = null;
        try {
            msg = PbMessageHelper.genTXMessage(gmsg);
        } catch (Exception e) {
            log.warn("", e);
        }
        boolean result = connect.write(msg);
        if (log.isDebugEnabled()) {
            log.debug("消息发送结果 cmd={},result={},address={},size={}", msg.getCmd(), result,
                    connect.address(), msg.getSerializedSize());
        }
    }

    @Override
    public void onReceive(Connect connect, TXMessage message) {
        handler.handle(this, message);
    }
}
