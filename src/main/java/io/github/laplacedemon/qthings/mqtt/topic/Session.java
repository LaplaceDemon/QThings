package io.github.laplacedemon.qthings.mqtt.topic;

import java.util.concurrent.atomic.AtomicReference;

import io.github.laplacedemon.qthings.mqtt.handler.ChannelUtil;
import io.github.laplacedemon.qthings.mqtt.handler.ClientIdSessionMapper;
import io.netty.channel.Channel;
import io.netty.util.Attribute;

public class Session {
	private String clientId;
	private AtomicReference<Channel> channel;
	private boolean cleanSession;
	private boolean willFlag;
	private WillMessage willMessage;
	private ClientIdSessionMapper clientIdSessionMapper;

	public Session(Channel channel,String clientId, boolean cleanSession, boolean willFlag, WillMessage willMessage) {
		super();
		this.channel = new AtomicReference<Channel>(channel);
		this.willFlag = willFlag;
		if(willFlag) {
			this.willMessage = willMessage;
		}
		
		Attribute<Session> attribute = this.channel.get().attr(ChannelUtil.SESSION_CHANNEL_KEY);
		attribute.set(this);
		
		this.cleanSession = cleanSession;
	}

	public Channel getChannel() {
		return channel.get();
	}

	public boolean isWillFlag() {
		return willFlag;
	}

	public WillMessage getWillMessage() {
		return willMessage;
	}

	public void close() {
		ChannelUtil.closeChannel(channel.get());
	}

	public boolean isCleanSession() {
		return cleanSession;
	}

	public void clean() {
		clientIdSessionMapper.remove(clientId);
	}

	public String getClientId() {
		return clientId;
	}

	public void updateChannel(Channel channel, boolean cleanSession, boolean willFlag, WillMessage willMessage) {
		Channel oldChannel = this.channel.get();
		while(true) {
			boolean compareAndSet = this.channel.compareAndSet(oldChannel, channel);
			if(compareAndSet) {
				break;
			}
		}
		
		if(oldChannel.isActive()) {
			oldChannel.close();
		}
		
		this.cleanSession = cleanSession;
		this.willFlag = willFlag;
		this.willMessage = willMessage;
	}
	
}
