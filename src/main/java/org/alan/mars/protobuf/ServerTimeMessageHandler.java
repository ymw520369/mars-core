/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package org.alan.mars.protobuf;

import org.alan.mars.net.MarsSession;
import org.springframework.stereotype.Component;

/**
 * Created on 2017/4/21.
 *
 * @author Alan
 * @since 1.0
 */
@Component
@ServerHandler
public class ServerTimeMessageHandler implements PbMessageHandler {
    @Override
    public int getMessageType() {
        return PbMessage.ReqServerTime.getDefaultInstance().getMessageType();
    }

    @Override
    public void handle(MarsSession session, PbMessage.TXMessage msg) {
        PbMessage.ResServerTime rpm = PbMessage.ResServerTime.newBuilder().setTime(System.currentTimeMillis())
                .build();
        session.send(rpm);
    }
}
