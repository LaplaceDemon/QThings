package io.github.laplacedemon.qthings.mqtt.protocal.packet;

import io.github.laplacedemon.qthings.mqtt.protocal.common.ControlPacketType;
import io.netty.buffer.ByteBuf;

public class ConnAckPacket extends MQTTPacket implements WritablePacket {
	private byte connectAcknowledgeFlags;
	private boolean sessionPresent;
	private byte connectReturnCode;
	
	public ConnAckPacket() {
		super.type = ControlPacketType.CONNACK;
	}
	
	public void setSessionPresent(boolean sessionPresent) {
		if(sessionPresent) {
			this.connectAcknowledgeFlags = 1;
		} else {
			this.connectAcknowledgeFlags = 0;
		}
	}
	
	public boolean isSessionPresent() {
		return sessionPresent;
	}
	
	public void setConnectReturnCode(byte connectReturnCode) {
		this.connectReturnCode = connectReturnCode;
	}
	
	public byte getConnectAcknowledgeFlags() {
		return connectAcknowledgeFlags;
	}
 
	@Override
	public void write(ByteBuf output) {
		output.writeByte(ControlPacketType.CONNACK.ordinal() << 4);
		output.writeByte(2);
		output.writeByte(connectAcknowledgeFlags);
		output.writeByte(connectReturnCode);
	}
	
}
