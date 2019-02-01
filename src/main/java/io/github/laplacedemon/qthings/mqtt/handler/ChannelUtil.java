package io.github.laplacedemon.qthings.mqtt.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.laplacedemon.qthings.mqtt.protocal.packet.PublishPacket;
import io.github.laplacedemon.qthings.mqtt.store.MessagePersistentStorage;
import io.github.laplacedemon.qthings.mqtt.topic.Session;
import io.github.laplacedemon.qthings.mqtt.topic.Subscriber;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class ChannelUtil {
	private final static Logger LOGGER = LoggerFactory.getLogger(ChannelUtil.class);
	public static final AttributeKey<Subscriber> SUBSCRIBER_CHANNEL_KEY = AttributeKey.valueOf("subscriber");
	public static final AttributeKey<Session> SESSION_CHANNEL_KEY = AttributeKey.valueOf("session");
	
	public static Subscriber subscriberOnChannelHandlerContext(ChannelHandlerContext ctx) {
		return subscriberOnChannel(ctx.channel());
	}
	
	public static Session sessionOnChannel(Channel channel) {
		Attribute<Session> attr = channel.attr(SESSION_CHANNEL_KEY);
		Session session = attr.get();
		return session;
	}
	
	public static Subscriber subscriberOnChannel(Channel channel) {
		Attribute<Subscriber> attr = channel.attr(SUBSCRIBER_CHANNEL_KEY);
		Subscriber subscriber = attr.get();
		return subscriber;
	}
	
	public static ChannelFuture closeChannel(Channel channel) {
		final Subscriber subscriber = subscriberOnChannel(channel);
		final Session session = sessionOnChannel(channel);
		
		ChannelFuture channelFuture = channel.close().addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				LOGGER.info("{} has been closed", channel.remoteAddress());
				
				if(session.isCleanSession()) {
					session.clean();
					if(subscriber != null) {
						subscriber.removeFromSubscribeTree();
					}
				} else {
					// 持久化session
					if(subscriber != null) {
						while(true) {
							PublishPacket msg = subscriber.poll();
							if(msg == null) {
								break;
							}
							
							MessagePersistentStorage.INS.save(session.getClientId(), msg.getPacketSeq(), msg);
						}
					}
				}
			}
		});
		
		return channelFuture;
		
	}
	
	public static ChannelFuture closeChannelHandlerContext(ChannelHandlerContext ctx) {
		return closeChannel(ctx.channel());
	}
}
