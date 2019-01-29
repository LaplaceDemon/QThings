package io.github.laplacedemon.qthings.mqtt.handler;

import io.github.laplacedemon.qthings.mqtt.protocal.packet.WritablePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MQTTEncoder extends MessageToByteEncoder<WritablePacket> {

	@Override
	protected void encode(ChannelHandlerContext ctx, WritablePacket msg, ByteBuf out) throws Exception {
		msg.write(out);
	}

}
