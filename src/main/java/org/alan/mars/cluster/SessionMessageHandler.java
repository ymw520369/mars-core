/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package org.alan.mars.cluster;

import org.alan.mars.net.NetAddress;
import org.alan.mars.protobuf.PbMessage;
import org.alan.mars.protobuf.PbMessageDispatcher;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.alan.mars.net.Connect;
import org.alan.mars.net.MarsSession;
import org.alan.mars.net.SessionListener;
import org.alan.mars.cluster.pb.SessionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created on 2017/4/6.
 *
 * @author Alan
 * @since 1.0
 */
@ClusterHandler
public class SessionMessageHandler implements ClusterMessageHandler<PbMessage.TXMessage> {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ClusterSystem clusterSystem;
    @Autowired
    private PbMessageDispatcher messageDispatcher;
    @Autowired
    private SessionListener sessionListener;

    @Override
    public void onReceive(Connect connect, PbMessage.TXMessage message) {
        if (message.getCmd() == SessionMessage.SessionEnter.getDefaultInstance().getCmd()) {
            sessionEnter(connect, message.getDataMessage());
        } else if (message.getCmd() == SessionMessage.SessionQuit.getDefaultInstance().getCmd()) {
            sessionQuit(connect, message.getDataMessage());
        }
    }

    public void sessionEnter(Connect connect, ByteString bs) {
        try {
            SessionMessage.SessionEnter sessionEnter = SessionMessage.SessionEnter.parseFrom(bs);
            String sessionId = sessionEnter.getSessionId();
            SessionMessage.Address address = sessionEnter.getAddress();
            NetAddress netAddress = new NetAddress(address.getHost(), address.getPort());
            log.debug("session enter,sessionId={},address={}", sessionId, netAddress);
            ClusterSession clusterSession = new ClusterSession(sessionId, netAddress, connect);
            clusterSession.setHandler(messageDispatcher);
            clusterSession.setSessionListener(sessionListener);
            clusterSystem.sessionMap().put(clusterSession.sessionId(), clusterSession);
            //clusterSession.onCreate();
        } catch (Exception e) {
            log.warn("session enter error...", e);
        }
    }

    public void sessionQuit(Connect connect, ByteString bs) {
        try {
            SessionMessage.SessionQuit sessionQuit = SessionMessage.SessionQuit.parseFrom(bs);
            String sessionId = sessionQuit.getSessionId();
            log.debug("session quit,sessionId={},address={}", sessionId);
            MarsSession marsSession = (MarsSession) clusterSystem.sessionMap().remove(sessionId);
            if (marsSession != null) {
                //marsSession.onClose();
                marsSession.setSessionListener(null);
                marsSession.setHandler(null);
                marsSession.setReference(null);
            }
        } catch (InvalidProtocolBufferException e) {
            log.warn("session quit...", e);
        }
    }

    @Override
    public int getMessageType() {
        return SessionMessage.SessionQuit.getDefaultInstance().getMessageType();
    }
}
