package io.github.laplacedemon.qthings.mqtt.protocal.packet;

import java.util.List;

import io.github.laplacedemon.qthings.mqtt.protocal.common.ControlPacketType;
import io.netty.buffer.ByteBuf;

public class SubAckPacket extends MQTTPacket implements WritablePacket {
	private int packetSeq;
	private List<Byte> qosReturnList;

	public SubAckPacket() {
		this.type = ControlPacketType.SUBACK;
		this.packetSeq = -1;
	}
	
	@Override
	public void write(ByteBuf output) {
		output.writeByte(ControlPacketType.SUBACK.ordinal() << 4);
		output.writeByte(2 + qosReturnList.size());
		output.writeShort(packetSeq);
		for(Byte qos : qosReturnList) {
			output.writeByte(qos);
		}
	}

	public int getPacketSeq() {
		return packetSeq;
	}

	public void setPacketSeq(int packetSeq) {
		this.packetSeq = packetSeq;
	}

	public List<Byte> getQosReturnList() {
		return qosReturnList;
	}

	public void setQosReturnList(List<Byte> qosReturnList) {
		this.qosReturnList = qosReturnList;
	}

}
