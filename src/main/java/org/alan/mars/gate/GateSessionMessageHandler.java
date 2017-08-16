/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package org.alan.mars.gate;

import org.alan.mars.cluster.ClusterHandler;
import org.alan.mars.cluster.ClusterMessageHandler;
import org.alan.mars.cluster.ClusterSystem;
import org.alan.mars.cluster.pb.SessionMessage;
import org.alan.mars.protobuf.PbMessage;
import com.google.protobuf.ByteString;
import org.alan.mars.net.Connect;
import org.alan.mars.protobuf.PbMessage.TXMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created on 2017/4/11.
 *
 * @author Alan
 * @since 1.0
 */
@ClusterHandler
public class GateSessionMessageHandler implements ClusterMessageHandler<TXMessage> {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ClusterSystem clusterSystem;

    @Override
    public void onReceive(Connect connect, PbMessage.TXMessage message) {
        if (message.getCmd() == SessionMessage.SessionQuit.getDefaultInstance().getCmd()) {
            sessionQuit(connect, message.getDataMessage());
        }
    }

    public void sessionQuit(Connect connect, ByteString bs) {
        try {
            SessionMessage.SessionQuit sessionQuit = SessionMessage.SessionQuit.parseFrom(bs);
            String sessionId = sessionQuit.getSessionId();
            log.debug("session quit,sessionId={},address={}", sessionId);
            GateSession gateSession = GateSession.gateSessionMap.remove(sessionId);
            if (gateSession != null) {
                gateSession.close();
            }
        } catch (Exception e) {
            log.warn("session quit...", e);
        }
    }

    @Override
    public int getMessageType() {
        return SessionMessage.SessionQuit.getDefaultInstance().getMessageType();
    }
}
