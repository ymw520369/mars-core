package org.alan.mars.protostuff;

import org.alan.mars.net.Connect;
import org.alan.mars.net.NetAddress;
import org.alan.mars.net.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created on 2017/8/4.
 *
 * @author Alan
 * @since 1.0
 */
public class PFSession extends Session<Object, MarsMessage> {

    public static Logger log = LoggerFactory.getLogger(PFSession.class);

    public static Map<Class<?>, ResponseMessage> responseMap;

    public PFSession(String sessionId, Connect connect, NetAddress address) {
        super(sessionId, connect, address);
    }

    @Override
    public void send(Object msg) {
        ResponseMessage responseMessage = responseMap.get(msg.getClass());
        if (responseMessage == null) {
            log.warn("消息发送失败，该消息结构没有被ResponseMessage注解，msg-class={}", msg.getClass());
            return;
        }
        byte[] data = ProtostuffUtil.serialize(msg);
        MarsMessage marsMessage = new MarsMessage(responseMessage.messageType(), responseMessage.cmd(), data);
        connect.write(marsMessage);
    }
}
