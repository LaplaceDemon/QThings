package io.github.laplacedemon.qthings.mqtt.protocal.packet;

import io.github.laplacedemon.qthings.mqtt.protocal.common.ControlPacketType;
import io.github.laplacedemon.qthings.mqtt.protocal.common.QoS;
import io.netty.buffer.ByteBuf;

public class PingReqPacket extends MQTTPacket implements ReadablePacket {
	private QoS qos;
	
	public PingReqPacket() {
		super.type = ControlPacketType.PINGREQ;
	}
	
	@Override
	public void read(ByteBuf buffer) {
		byte headByte = super.getHeadByte();
		int qosValue = (headByte >> 1 & 0x01) + (headByte >> 2 & 0x01);
		this.qos = QoS.valueOf(qosValue);
	}

	public QoS getQos() {
		return qos;
	}

}
