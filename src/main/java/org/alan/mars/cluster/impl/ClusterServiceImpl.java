/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package org.alan.mars.cluster.impl;

import org.alan.mars.cluster.ClusterService;
import org.alan.mars.cluster.ClusterSystem;
import org.alan.mars.net.MarsSession;
import org.alan.mars.net.NetAddress;
import org.alan.mars.rpc.server.RpcService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created on 2017/3/31.
 *
 * @author Alan
 * @since 1.0
 */
@RpcService(ClusterService.class)
public class ClusterServiceImpl implements ClusterService {

    private ClusterSystem clusterSystem;

    @Autowired
    public ClusterServiceImpl(ClusterSystem clusterSystem) {
        this.clusterSystem = clusterSystem;
    }

    @Override
    public boolean sessionEnter(String sessionId, NetAddress netAddress) {
//        MarsSession marsSession = new MarsSession(sessionId, netAddress);
//        clusterSystem.sessionMap().put(sessionId, marsSession);
//        marsSession.onCreate();MarsSession marsSession = new MarsSession(sessionId, netAddress);
//        clusterSystem.sessionMap().put(sessionId, marsSession);
//        marsSession.onCreate();
        return true;
    }

    @Override
    public boolean sessionQuit(String sessionId) {
        MarsSession marsSession = (MarsSession) clusterSystem.sessionMap().remove(sessionId);
        if (marsSession != null) {
            //marsSession.onClose();
        }
        return true;
    }

    @Override
    public void messageReceive(String sessionId, Object msg) {
        MarsSession marsSession = (MarsSession) clusterSystem.sessionMap().get(sessionId);
        if (marsSession != null) {
        }
    }
}
