package io.github.laplacedemon.qthings.mqtt.protocal.packet;

import io.netty.buffer.ByteBuf;

public interface WritablePacket {
	void write(ByteBuf output);
}
