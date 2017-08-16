/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package org.alan.mars.curator;

import com.alibaba.fastjson.JSON;
import org.alan.mars.config.NodeConfig;
import org.alan.mars.data.MarsConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;

import static org.alan.mars.data.MarsConst.SEPARATOR;

/**
 * Created on 2017/3/6.
 *
 * @author alan
 * @since 1.0
 */
public class NodeManager implements MarsCuratorListener, MarsNodeListener {
    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private NodeConfig nodeConfig;

    @Autowired
    private MarsCurator marsCurator;

    public NodeManager() {
    }

    @Override
    public void marsCuratorRefreshed(MarsCurator marsCurator) {
        register();
    }

    private void register() {
        try {
            StringBuilder sb = new StringBuilder(SEPARATOR);
            sb.append(nodeConfig.getParentPath()).append(SEPARATOR)
                    .append(nodeConfig.getType()).append(SEPARATOR)
                    .append(nodeConfig.getName());
            String path = sb.toString();
            log.info("node register,path is {}", path);
            String nc = JSON.toJSONString(nodeConfig);
            path = marsCurator.addPath(path, nc.getBytes("UTF-8"), false);
            marsCurator.addMarsNodeListener(path, this);
        } catch (UnsupportedEncodingException e) {
            log.warn("node register fail.", e);
        }
    }

    public MarsNode getMarNode(NodeType nodeType) {
        String nodePath = MarsConst.SEPARATOR + nodeConfig.getParentPath()
                + MarsConst.SEPARATOR + nodeType;
        return marsCurator.getMarsNode(nodePath);
    }

    @Override
    public void nodeChange(NodeChangeType nodeChangeType, MarsNode marsNode) {
        switch (nodeChangeType) {
            case NODE_ADD:

                break;
            case NODE_REMOVE:
                log.warn("本节点被异常移除");
                register();
                break;
        }
    }
}
