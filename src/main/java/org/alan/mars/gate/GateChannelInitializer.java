/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package org.alan.mars.gate;

import org.alan.mars.protobuf.PbMessage;
import org.alan.mars.cluster.ClusterSystem;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
 * Created on 2017/4/6.
 *
 * @author Alan
 * @since 1.0
 */
public class GateChannelInitializer extends ChannelInitializer<SocketChannel> {
    private static final int MSG_MAX_SIZE = 10 * 1024 * 1024;

    private static final int HANDER_SIZE = 4;
    private ClusterSystem clusterSystem;


    public GateChannelInitializer(ClusterSystem clusterSystem) {
        this.clusterSystem = clusterSystem;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(
                MSG_MAX_SIZE, 0, HANDER_SIZE, 0, 4))
                .addLast(new ProtobufDecoder(PbMessage.TXMessage.getDefaultInstance()))
                .addLast(new LengthFieldPrepender(HANDER_SIZE))
                .addLast(new ProtobufEncoder())
                .addLast(new GateSession(clusterSystem));
    }
}
