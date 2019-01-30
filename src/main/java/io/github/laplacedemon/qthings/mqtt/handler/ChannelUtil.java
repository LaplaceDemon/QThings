package io.github.laplacedemon.qthings.mqtt.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	public static Session sessuibOnChannel(Channel channel) {
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
		Subscriber subscriber = subscriberOnChannel(channel);
		if(subscriber != null) {
			subscriber.remove();
		}
		
		ChannelFuture channelFuture = channel.close().addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				LOGGER.info("{} has been closed", channel.remoteAddress() );
			}
			
		});
		
		return channelFuture;
		
	}
	
	public static ChannelFuture closeChannelHandlerContext(ChannelHandlerContext ctx) {
		return closeChannel(ctx.channel());
	}
}
