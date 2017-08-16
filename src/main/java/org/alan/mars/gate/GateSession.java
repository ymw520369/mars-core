/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package org.alan.mars.gate;

import org.alan.mars.cluster.ClusterClient;
import org.alan.mars.cluster.ClusterSystem;
import org.alan.mars.cluster.pb.SessionMessage;
import org.alan.mars.curator.NodeType;
import org.alan.mars.net.Connect;
import org.alan.mars.net.Inbox;
import org.alan.mars.netty.NettyConnect;
import org.alan.mars.protobuf.PbMessage;
import org.alan.mars.protobuf.PbMessage.TXMessage;
import org.alan.mars.protobuf.PbMessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 玩家网关服务器会话对象
 * </p>
 * <p>
 * Created on 2017/3/27.
 *
 * @author Alan
 * @since 1.0
 */
public class GateSession extends NettyConnect<TXMessage> implements Inbox<TXMessage> {

    public static Map<String, GateSession> gateSessionMap = new ConcurrentHashMap<>();

    Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 集群系统管理对象
     */
    private ClusterSystem clusterSystem;
    /**
     * 节点客户端对象
     */
    private ClusterClient clusterClient;
    /**
     * 节点连接
     */
    private Connect connect;

    private String sessionId;

    public GateSession(ClusterSystem clusterSystem) {
        this.clusterSystem = clusterSystem;
    }

    /**
     * 收到集群中其他节点发送过来的消息
     *
     * @param msg
     */
    @Override
    public void onReceive(Connect connect, TXMessage msg) {
        connect.write(msg);
    }

    /**
     * 接收客户端发送过来的消息
     *
     * @param msg
     */
    @Override
    public void messageReceived(TXMessage msg) {
        // 客户端发来的消息
        try {
            if (connect == null) {
                close();
            } else {
                PbMessage.ClusterMessage.Builder builder = PbMessage.ClusterMessage.newBuilder().setTargetId(sessionId).setMsg(msg);
                connect.write(builder.build());
            }
        } catch (Exception e) {
            log.warn("消息转发失败，sessionId is {}", sessionId, e);
        }
    }

    @Override
    public void onClose() {
        sessionId = channel.id().asShortText();
        // 将session添加到集群节点中
        clusterSystem.sessionMap().remove(sessionId);
        quit(clusterClient);
    }

    @Override
    public void onCreate() {
        sessionId = channel.id().asShortText();
        // 将session添加到集群节点中
        gateSessionMap.put(sessionId, this);
        // 默认将玩家进入数据节点
        enter(clusterSystem.getByNodeType(NodeType.DATA));
    }

    public void enter(ClusterClient cluster) {
        if (cluster == null) {
            log.debug("cluster client not found.");
            return;
        }
        quit(clusterClient);
        this.clusterClient = cluster;
        this.connect = clusterClient.getConnect();
        SessionMessage.SessionEnter sessionEnter = SessionMessage.SessionEnter.newBuilder()
                .setAddress(SessionMessage.Address.newBuilder().setHost(remoteAddress.getHost()).setPort(remoteAddress.getPort()))
                .setSessionId(sessionId).build();
        try {
            connect.write(PbMessageHelper.genClusterMessage(null, sessionEnter));
        } catch (Exception e) {
            log.warn("进入场景消息发送失败，sessionId is {}", sessionId, e);
        }
    }

    public void quit(ClusterClient clusterClient) {
        if (clusterClient != null) {
            SessionMessage.SessionQuit sessionQuit = SessionMessage.SessionQuit.newBuilder()
                    .setSessionId(sessionId).build();
            try {
                connect.write(PbMessageHelper.genClusterMessage(null, sessionQuit));
            } catch (Exception e) {
                log.warn("退出场景消息发送失败，sessionId is {}", sessionId, e);
            }
        }
    }

    public void send(TXMessage msg) {

    }
}
