package io.github.laplacedemon.qthings.mqtt.protocal.packet;

import io.github.laplacedemon.qthings.mqtt.protocal.common.ControlPacketType;

public class MQTTPacket {
	protected ControlPacketType type;
	private int packetSize;
	private byte headByte = -1;

	public ControlPacketType getType() {
		return type;
	}

	@Override
	public String toString() {
		return this.type.name() + " Message";
	}

	public int getPacketSize() {
		return packetSize;
	}

	public void setPacketSize(int packetSize) {
		this.packetSize = packetSize;
	}

	public byte getHeadByte() {
		return headByte;
	}

	public void setHeadByte(byte headByte) {
		this.headByte = headByte;
	}
	
}
