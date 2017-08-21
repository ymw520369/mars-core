/**
 * Copyright Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 *
 * 2017年2月8日 	
 */
package org.alan.mars.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.alan.mars.net.MarsSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * 
 * @scene 1.0
 * 
 * @author Alan
 *
 */
//@Component
//@ServerHandler
public class PingMessageHandler implements PbMessageHandler {

	private Logger log  = LoggerFactory.getLogger(getClass());

	@Override
	public void handle(MarsSession session, PbMessage.TXMessage msg) {
		if (log.isDebugEnabled()) {
			log.debug("ping message received.thread={}",Thread.currentThread().getId());
		}
		ByteString bs = msg.getDataMessage();
//		System.out.println(bs.size());
		try {
			PbMessage.ReqPingMessage pm = PbMessage.ReqPingMessage.parseFrom(bs);
			int mt = pm.getMessageType();
			long time = pm.getTime();
			long currentTime = System.currentTimeMillis();
			int ping = (int) (currentTime - time);
			log.debug("ping message parse ok, time=" + time + ",ping=" + ping
					+ ",messageType=" + mt);
			PbMessage.ResPingMessage rpm = PbMessage.ResPingMessage.newBuilder().setPing(ping)
					.build();
			session.send(rpm);
		} catch (InvalidProtocolBufferException e) {
			log.warn("ping message parse warn.", e);
		}
	}

	@Override
	public int getMessageType() {
		return PbMessage.ReqPingMessage.getDefaultInstance().getMessageType();
	}

}
