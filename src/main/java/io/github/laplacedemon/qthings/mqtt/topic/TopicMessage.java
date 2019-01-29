package io.github.laplacedemon.qthings.mqtt.topic;

public class TopicMessage {
	private String topic;
	private byte qos;
	private byte[] payload;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public byte getQos() {
		return qos;
	}

	public void setQos(byte qos) {
		this.qos = qos;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

}
