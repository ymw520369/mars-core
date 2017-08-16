package org.alan.mars.protostuff;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.alan.mars.net.Connect;
import org.alan.mars.net.NetAddress;
import org.alan.mars.net.Session;
import org.alan.mars.netty.ConnectImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Created on 2017/7/27.
 *
 * @author Alan
 * @since 1.0
 */
public class MarsMessageDispatcher extends
        SimpleChannelInboundHandler<MarsMessage> implements
        ApplicationListener<ContextRefreshedEvent> {
    Logger log = LoggerFactory.getLogger(getClass());

    private AttributeKey<PFSession> attributeKey = AttributeKey.valueOf("session");
    private Map<Integer, MessageController> messageControllers;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MarsMessage msg) throws Exception {
        int messageType = msg.messageType;
        int command = msg.cmd;
        MessageController messageController = messageControllers.get(messageType);
        PFSession session = getSession(ctx.channel());
        if (messageController != null) {
            MethodInfo methodInfo = messageController.MethodInfos.get(command);
            Object bean = messageController.been;
            if (methodInfo.parms != null && methodInfo.parms.length > 0) {
                Object[] args = new Object[methodInfo.parms.length];
                Object reference = session.getReference(Object.class);
                for (int i = 0; i < args.length; i++) {
                    Class<?> clazz = methodInfo.parms[i];
                    if (clazz == PFSession.class) {
                        args[i] = session;
                    } else if (reference != null && clazz == reference.getClass()) {
                        args[i] = reference;
                    } else {
                        RequestMessage rm = clazz.getAnnotation(RequestMessage.class);
                        if (rm != null && msg.data != null && msg.data.length > 0) {
                            args[i] = ProtostuffUtil.deserialize(msg.data, clazz);
                        }
                    }
                }
                messageController.methodAccess.invoke(bean, methodInfo.index, args);
            } else {
                messageController.methodAccess.invoke(bean, methodInfo.index);
            }
        } else {
            log.warn("未被注册的消息,messageType={},cmd={}", messageType, command);
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("channel active,ctx= {}", ctx);
        }
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        NetAddress netAddress = new NetAddress(address.getAddress().getHostAddress(), address.getPort());
        Connect connect = new ConnectImpl(ctx.channel());
        PFSession session = new PFSession(ctx.channel().id().asShortText(), connect, netAddress);
        ctx.channel().attr(attributeKey).set(session);
    }

    /**
     * 连接通道注册成功时被调用
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        if (log.isDebugEnabled()) {
            log.debug("channel registered,ctx= {}", ctx);
        }
    }

    /**
     * 连接断开时被调用
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        if (log.isDebugEnabled()) {
            log.debug("channel unregistered,ctx= {}", ctx);
        }
        Session session = getSession(ctx.channel());
        ctx.channel().attr(attributeKey).set(null);
        if (session != null) {
            session.onConnectClose(session.getConnect());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (log.isWarnEnabled()) {
            log.warn("exception caught,ctx=" + ctx, cause);
        }
    }

    /**
     * 获取绑定在通道上下文中的自定义session
     *
     * @param channel
     * @return
     */
    public PFSession getSession(Channel channel) {
        PFSession session = channel.attr(attributeKey).get();
        return session;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        messageControllers = MessageUtil.load(event.getApplicationContext());
        PFSession.responseMap = MessageUtil.loadResponseMessage();
    }
}
