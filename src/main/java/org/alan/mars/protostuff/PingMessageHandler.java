package org.alan.mars.protostuff;

import org.springframework.stereotype.Component;

/**
 * 心跳消息处理器
 * <p>
 * author Alan
 * eamil mingweiyang@foxmail.com
 * date 2017/8/20
 */
@Component
@MessageType(1)
public class PingMessageHandler {

    @ProtobufMessage(resp = true, messageType = 1, cmd = 2)
    public static class Pong {
        long time;

        public Pong(long time) {
            this.time = time;
        }
    }


    @Command(1)
    public void ping(PFSession session) {
        session.send(new Pong(System.currentTimeMillis()));
    }
}
