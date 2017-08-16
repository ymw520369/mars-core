/**
 * Copyright Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 * <p>
 * 2016年5月31日
 */
package org.alan.mars.netty;

import org.alan.mars.rpc.protocol.RpcDecoder;
import org.alan.mars.rpc.protocol.RpcEncoder;
import org.alan.mars.rpc.protocol.RpcRequest;
import org.alan.mars.rpc.protocol.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author Alan
 * @version 1.0
 */
public class RpcChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 业务处理器
     */
    private ChannelHandler channelHandler;

    public RpcChannelInitializer(
            ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                .addLast(new RpcDecoder(RpcRequest.class))
                .addLast(new RpcEncoder(RpcResponse.class))
                .addLast(channelHandler);

    }

}
