package io.github.laplacedemon.qthings.mqtt.protocal.packet;

import java.util.ArrayList;
import java.util.List;

import io.github.laplacedemon.qthings.mqtt.protocal.common.ControlPacketType;
import io.github.laplacedemon.qthings.mqtt.protocal.util.BufferUtil;
import io.netty.buffer.ByteBuf;

public class SubscribePacket extends MQTTPacket implements ReadablePacket {
	private int packetSeq;
	private List<TopicFilter> topicFilters;
	
	public SubscribePacket() {
		this.type = ControlPacketType.SUBSCRIBE;
		this.packetSeq = -1;
		this.topicFilters = new ArrayList<>();
	}
	
	@Override
	public void read(ByteBuf buffer) {
		this.packetSeq = buffer.readUnsignedShort();
		int length = super.getPacketSize() - 2;
		do {
			int topicFilterLength = buffer.readUnsignedShort();
			String topicFilterStr = BufferUtil.readString(buffer, topicFilterLength);
			int qos = buffer.readByte();
			TopicFilter topicFilter = new TopicFilter(topicFilterStr, qos);
			this.topicFilters.add(topicFilter);
			length -= (topicFilterLength + 1 + 2);
		} while(length > 0);
	}
	
	public int getPacketSeq() {
		return packetSeq;
	}

	public List<TopicFilter> getTopicFilters() {
		return topicFilters;
	}

	public static class TopicFilter {
		private String filter;
		private int qos;
		
		public TopicFilter(String filter, int qos) {
			super();
			this.filter = filter;
			this.qos = qos;
		}

		public String getFilter() {
			return filter;
		}
		
		public void setFilter(String filter) {
			this.filter = filter;
		}
		
		public int getQos() {
			return qos;
		}
		
		public void setQos(int qos) {
			this.qos = qos;
		}
		
		
	}
}

