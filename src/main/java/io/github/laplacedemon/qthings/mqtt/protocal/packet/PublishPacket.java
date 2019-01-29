package io.github.laplacedemon.qthings.mqtt.protocal.packet;

import java.util.Arrays;

import io.github.laplacedemon.qthings.mqtt.protocal.common.ControlPacketType;
import io.github.laplacedemon.qthings.mqtt.protocal.common.QoS;
import io.github.laplacedemon.qthings.mqtt.protocal.util.BufferUtil;
import io.github.laplacedemon.qthings.mqtt.protocal.util.RemainingLengthUtil;
import io.netty.buffer.ByteBuf;

public class PublishPacket extends MQTTPacket implements ReadablePacket,WritablePacket,Cloneable {
	private boolean dup;
	private QoS qos;
	private boolean retain;
	private String topicName;
	private int packetSeq;
	private byte[] payload;
	
	public PublishPacket() {
		this.type = ControlPacketType.PUBLISH;
		this.packetSeq = -1;
	}
	
	@Override
	public void read(ByteBuf buffer) {
		byte headByte = super.getHeadByte();
		this.dup = (headByte >> 3 & 0x01) == 1;
		int qosValue = (headByte >> 1 & 0x01) + (headByte >> 2 & 0x01);
		this.qos = QoS.valueOf(qosValue);
		this.retain = (headByte & 0x01) == 1;
		
		int topicNameLength = buffer.readUnsignedShort();
		this.topicName = BufferUtil.readString(buffer, topicNameLength);
		this.packetSeq = buffer.readUnsignedShort();
		int messageLength = this.getPacketSize() - topicNameLength - 2 - 2;
		this.payload =BufferUtil.readBytes(buffer, messageLength);
	}

	public boolean isDup() {
		return dup;
	}

	public QoS getQos() {
		return qos;
	}

	public boolean isRetain() {
		return retain;
	}

	public String getTopicName() {
		return topicName;
	}

	public int getPacketSeq() {
		return packetSeq;
	}

	public byte[] getPayload() {
		return payload;
	}

	@Override
	public String toString() {
		return "PublishPacket [dup=" + dup + ", qos=" + qos + ", retain=" + retain + ", topicName=" + topicName
				+ ", packetSeq=" + packetSeq + ", payload=" + Arrays.toString(payload) + "]";
	}

	@Override
	public void write(ByteBuf output) {
		byte headByte = super.getHeadByte();
		if(headByte == -1) {
			headByte = (byte)(ControlPacketType.PUBLISH.ordinal() << 4);
			if(this.dup) {
				headByte |= (byte)(0x01 << 3);
			}
			headByte |=(byte)(this.qos.getValue() << 1);
			if(this.retain) {
				headByte |= (byte)(0x01);
			}
			
			super.setHeadByte(headByte);
		}
		
		output.writeByte(headByte);
		int packetLength = 2 + this.topicName.length() + 2 + this.payload.length;
		output.writeBytes(RemainingLengthUtil.encode(packetLength));
		output.writeShort(this.topicName.length());
		output.writeBytes(this.topicName.getBytes());
		output.writeShort(this.packetSeq);
		output.writeBytes(this.payload);
	}

	public void setPacketSeq(int packetSeq) {
		this.packetSeq = packetSeq;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		PublishPacket publishPacket = new PublishPacket();
		publishPacket.dup = this.dup;
		publishPacket.retain = this.retain;
		publishPacket.packetSeq = this.packetSeq;
		publishPacket.qos = this.qos;
		publishPacket.topicName = this.topicName;
		publishPacket.payload = this.payload;
		
		return publishPacket;
	}

	public void setQos(QoS qos) {
		this.qos = qos;
	}

	public void setRetain(boolean retain) {
		this.retain = retain;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
}
