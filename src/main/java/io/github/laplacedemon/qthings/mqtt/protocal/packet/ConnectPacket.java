package io.github.laplacedemon.qthings.mqtt.protocal.packet;


import io.github.laplacedemon.qthings.mqtt.protocal.common.ConnectFlags;
import io.github.laplacedemon.qthings.mqtt.protocal.common.ControlPacketType;
import io.github.laplacedemon.qthings.mqtt.protocal.common.QoS;
import io.github.laplacedemon.qthings.mqtt.protocal.util.BufferUtil;
import io.netty.buffer.ByteBuf;

public class ConnectPacket extends MQTTPacket implements ReadablePacket {
	private short length;
	private String protocolName;
	private byte protocolLevel;
	private byte connectFlags;
	private int keepAlive;
	private String clientIdentifier;
	private boolean willFlag;
	private String willTopic;
	private byte[] willPayload;
	private QoS willQoS;
	private boolean willRetina;
	private String username;
	private String  password;
	
	private boolean cleanSession;
	
	public ConnectPacket() {
		super.type = ControlPacketType.CONNECT;
	}
	
	private int getQos(byte flags) {
		int value = 0;
		if((flags >> 3 & 0x01) == 1) {
			value += 1;
			return value;
		}
		
		if((flags >> 4 & 0x01) == 1) {
			value += 2;
			return value;
		}
		
		return value;
	}
	
	@Override
	public void read(ByteBuf buffer) {
		this.length = buffer.readShort();
		this.protocolName = BufferUtil.readString(buffer, this.length);
		
		this.protocolLevel = buffer.readByte();
		this.connectFlags = buffer.readByte();
		
		this.keepAlive = buffer.readUnsignedShort();
		
		int clientIdLength = buffer.readUnsignedShort();
		this.clientIdentifier = BufferUtil.readString(buffer, clientIdLength);
		
		if(ConnectFlags.WillFlag.hasFlag(this.connectFlags)) {
			this.willFlag = true;
			
			int willTopicLength = buffer.readUnsignedShort();
			this.willTopic = BufferUtil.readString(buffer, willTopicLength);
			
			int willPayloadLength = buffer.readUnsignedShort();
			this.willPayload = BufferUtil.readBytes(buffer, willPayloadLength);
		}
		
		if(ConnectFlags.UserNameFlag.hasFlag(this.connectFlags)) {
			int usernameLength = buffer.readUnsignedShort();
			this.username = BufferUtil.readString(buffer, usernameLength);
		}
		
		if(ConnectFlags.PasswordFlag.hasFlag(this.connectFlags)) {
			int passwordLength = buffer.readUnsignedShort();
			this.password = BufferUtil.readString(buffer, passwordLength);
		}
		
		if(ConnectFlags.WillRetina.hasFlag(this.connectFlags)) {
			this.willRetina = true;
		} else {
			this.willRetina = false;
		}
		
		if(ConnectFlags.WillQoS.hasFlag(this.connectFlags)) {
			this.willQoS = QoS.valueOf(this.getQos(this.connectFlags));
		}
		
		if(ConnectFlags.CleanSession.hasFlag(this.connectFlags)) {
			this.cleanSession = true;
		} else {
			this.cleanSession = false;
		}
		
	}

	public short getLength() {
		return length;
	}

	public String getProtocolName() {
		return protocolName;
	}

	public byte getProtocolLevel() {
		return protocolLevel;
	}

	public byte getConnectFlags() {
		return connectFlags;
	}

	public int getKeepAlive() {
		return keepAlive;
	}

	public String getClientIdentifier() {
		return clientIdentifier;
	}

	public String getWillTopic() {
		return willTopic;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public boolean isCleanSession() {
		return cleanSession;
	}

	public boolean isWillFlag() {
		return willFlag;
	}

	public QoS getWillQoS() {
		return willQoS;
	}

	public byte[] getWillPayload() {
		return willPayload;
	}

	public boolean isWillRetina() {
		return willRetina;
	}

}
