/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package org.alan.mars.cluster;

import org.alan.mars.protobuf.PbMessage;
import com.google.protobuf.GeneratedMessage;
import org.alan.mars.net.Connect;
import org.alan.mars.net.MarsSession;
import org.alan.mars.net.NetAddress;
import org.alan.mars.protobuf.PbMessageHelper;
import org.alan.mars.cluster.pb.SessionMessage;

/**
 * Created on 2017/4/11.
 *
 * @author Alan
 * @since 1.0
 */
public class ClusterSession extends MarsSession {
    public ClusterSession(String id, NetAddress netAddress, Connect connect) {
        super(id, netAddress, connect);
    }

    @Override
    public void send(GeneratedMessage message) {
        PbMessage.ClusterMessage msg = null;
        try {
            msg = PbMessageHelper.genClusterMessage(sessionId, message);
        } catch (Exception e) {
            log.warn("message send error,sessionId is {}", sessionId, e);
        }
        //boolean result = connect.write(msg);
//        if (log.isDebugEnabled()) {
//            log.debug("消息发送结果 cmd={},result={},address={},size={}", msg.getMsg().getCmd(), result,
//                    address,msg.getSerializedSize());
//        }
    }

    @Override
    public void close() {
        SessionMessage.SessionQuit sessionQuit = SessionMessage.SessionQuit.newBuilder().setSessionId(sessionId()).build();
        try {
            PbMessage.ClusterMessage clusterMessage = PbMessageHelper.genClusterMessage(null, sessionQuit);
            //connect.write(clusterMessage);
        } catch (Exception e) {
            log.warn("session close error,sessionId is {}", sessionId, e);
        }
    }
}
