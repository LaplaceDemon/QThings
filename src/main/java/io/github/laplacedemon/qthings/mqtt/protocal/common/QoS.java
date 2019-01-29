package io.github.laplacedemon.qthings.mqtt.protocal.common;

public enum QoS {
	Failer((byte)-1),
	AtMostOnce((byte)0),
	AtLeastOnce((byte)1),
	OnlyOnce((byte)2);
	
	private byte value;
	
	QoS(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

	public static QoS valueOf(int qosValue) {
		if(qosValue == 0) {
			return AtMostOnce;
		} else if(qosValue == 1) {
			return AtLeastOnce;
		} else if(qosValue == 2) {
			return OnlyOnce;
		}
		
		throw new IllegalArgumentException();
	}
	
}
