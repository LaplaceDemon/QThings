package io.github.laplacedemon.qthings.mqtt.topic;

import io.github.laplacedemon.qthings.mqtt.handler.ChannelUtil;
import io.netty.channel.Channel;

public class Session {
	private Channel channel;
	private boolean willFlag;
	private WillMessage willMessage;

	public Session(Channel channel,boolean willFlag, WillMessage willMessage) {
		super();
		this.channel = channel;
		this.willFlag = willFlag;
		if(willFlag) {
			this.willMessage = willMessage;
		}
	}

	public Channel getChannel() {
		return channel;
	}

	public boolean isWillFlag() {
		return willFlag;
	}

	public WillMessage getWillMessage() {
		return willMessage;
	}

	public void close() {
		ChannelUtil.closeChannel(channel);
	}
	
}
