package io.github.laplacedemon.qthings.mqtt.protocal.packet;

import io.github.laplacedemon.qthings.mqtt.protocal.common.ControlPacketType;
import io.github.laplacedemon.qthings.mqtt.protocal.common.QoS;
import io.netty.buffer.ByteBuf;

public class PubAckPacket extends MQTTPacket implements WritablePacket, ReadablePacket {
	private int packetSeq;
	private QoS qos;

	public PubAckPacket() {
		this.type = ControlPacketType.PUBACK;
		this.packetSeq = -1;
	}

	@Override
	public void write(ByteBuf output) {
		byte headByte = (byte) ((byte)(ControlPacketType.PUBACK.ordinal() << 4 ) | (byte)(this.qos.getValue() << 1));
		output.writeByte(headByte);
		output.writeByte(2);
		output.writeShort(packetSeq);
	}

	public int getPacketSeq() {
		return packetSeq;
	}

	public void setPacketSeq(int packetSeq) {
		this.packetSeq = packetSeq;
	}
	
	public void setQos(QoS qos) {
		this.qos = qos;
	}

	@Override
	public void read(ByteBuf buffer) {
		this.packetSeq = buffer.readUnsignedShort();
	}

	public QoS getQos() {
		return qos;
	}
	
}
