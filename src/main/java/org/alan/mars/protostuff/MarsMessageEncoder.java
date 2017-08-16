package org.alan.mars.protostuff;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Created on 2017/7/27.
 *
 * @author Alan
 * @since 1.0
 */
public class MarsMessageEncoder extends MessageToMessageEncoder<MarsMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MarsMessage msg, List<Object> out) throws Exception {
        out.add(Unpooled.wrappedBuffer(ProtostuffUtil.serialize(msg)));
    }
}
