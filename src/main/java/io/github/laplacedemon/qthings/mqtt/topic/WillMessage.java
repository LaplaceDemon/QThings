package io.github.laplacedemon.qthings.mqtt.topic;

import io.github.laplacedemon.qthings.mqtt.protocal.common.QoS;

public class WillMessage {
	private String topic;
	private QoS qos;
	private byte[] payload;
	private boolean retain;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public QoS getQos() {
		return qos;
	}

	public void setQos(QoS qos) {
		this.qos = qos;
	}

	public boolean isRetain() {
		return retain;
	}

	public void setRetain(boolean retain) {
		this.retain = retain;
	}
	
}
