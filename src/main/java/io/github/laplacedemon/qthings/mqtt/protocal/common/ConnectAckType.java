package io.github.laplacedemon.qthings.mqtt.protocal.common;

public enum ConnectAckType {
	Success((byte)0x00),
	UnsupportedProtocolVersion((byte)0x01),
	UnqualifiedClientIdentifier((byte)0x02),
	ServerIsNotAvailable((byte)0x03),
	InvalidUsernameOrPassword((byte)0x04),
	Unauthorized((byte)0x05);
	
	private byte returnCode;
	
	ConnectAckType(byte returnCode) {
		this.returnCode = returnCode;
	}

	public byte getReturnCode() {
		return returnCode;
	}
}
