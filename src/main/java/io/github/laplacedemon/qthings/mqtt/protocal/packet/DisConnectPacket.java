package io.github.laplacedemon.qthings.mqtt.protocal.packet;

import io.github.laplacedemon.qthings.mqtt.protocal.common.ControlPacketType;
import io.netty.buffer.ByteBuf;

public class DisConnectPacket extends MQTTPacket implements ReadablePacket {
	public DisConnectPacket() {
		super.type = ControlPacketType.DISCONNECT;
	}

	@Override
	public void read(ByteBuf buffer) {
	}
}
