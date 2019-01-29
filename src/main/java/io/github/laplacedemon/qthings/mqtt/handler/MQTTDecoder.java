package io.github.laplacedemon.qthings.mqtt.handler;

import java.util.List;

import io.github.laplacedemon.qthings.mqtt.protocal.common.ControlPacketType;
import io.github.laplacedemon.qthings.mqtt.protocal.exception.MQTTDecodeException;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.MQTTPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.ReadablePacket;
import io.github.laplacedemon.qthings.mqtt.protocal.util.RemainingLengthUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

public class MQTTDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		try{
			 Object decoded = decode(ctx, in);
		     if (decoded != null) {
		         out.add(decoded);
		     }
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	private Object decode(ChannelHandlerContext ctx, ByteBuf in) {
		int readerIndex = in.readerIndex();
		
		byte byte0 = in.getByte(readerIndex);
		byte byte1 = in.getByte(readerIndex + 1);
		if(((byte1 >> 7) & 0x01) != 1) {
			int length = RemainingLengthUtil.decode(byte1);
			if(in.readableBytes() < length) {
				return null;
			}
			
			in.readByte(); // head
			in.readByte();
			
			return readPacket(byte0, in, length);
		}
		
		byte byte2 = in.getByte(readerIndex + 2);
		if(((byte2 >> 7) & 0x01) != 1) {
			int length = RemainingLengthUtil.decode(byte1, byte2);
			if(in.readableBytes() < length) {
				return null;
			}
			
			in.readByte(); // head
			in.readByte();
			in.readByte();
			
			return readPacket(byte0, in, length);
		}
		
		byte byte3 = in.getByte(readerIndex + 3);
		if(((byte3 >> 7) & 0x01) != 1) {
			int length = RemainingLengthUtil.decode(byte1, byte2, byte3);
			if(in.readableBytes() < length) {
				return null;
			}
			
			in.readByte(); // head
			in.readByte();
			in.readByte();
			in.readByte();
			
			return readPacket(byte0, in, length);
		}
		
		byte byte4 = in.getByte(readerIndex + 3);
		if(((byte4 >> 7) & 0x01) != 1) {
			int length = RemainingLengthUtil.decode(byte1, byte2, byte3, byte4);
			if(in.readableBytes() < length) {
				return null;
			}
			
			in.readByte(); // head
			in.readByte();
			in.readByte();
			in.readByte();
			in.readByte();
			
			return readPacket(byte0, in, length);
		}
		
		throw new TooLongFrameException();
	}
	
	public MQTTPacket readPacket(byte firstByte, ByteBuf in, int remainingLength) {
		byte b = (byte)((firstByte & 0xF0) >> 4);
		ControlPacketType controlPacketType = ControlPacketType.valueOf(b);
		MQTTPacket packet = controlPacketType.createPacket();
		if(packet == null) {
			throw new MQTTDecodeException();
		}
		packet.setPacketSize(remainingLength);
		packet.setHeadByte(firstByte);
		if (packet instanceof ReadablePacket) {
			((ReadablePacket)packet).read(in);
		}
		
		return packet;
	}

}
