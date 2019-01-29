package io.github.laplacedemon.qthings.mqtt.protocal.packet;

import io.github.laplacedemon.qthings.mqtt.protocal.common.ControlPacketType;
import io.netty.buffer.ByteBuf;

public class PingRespPacket extends MQTTPacket implements WritablePacket {
	
	public PingRespPacket() {
		this.type = ControlPacketType.PINGRESP;
	}

	@Override
	public void write(ByteBuf output) {
		output.writeByte(ControlPacketType.PINGRESP.ordinal() << 4);
		output.writeByte(0);
	}

}
