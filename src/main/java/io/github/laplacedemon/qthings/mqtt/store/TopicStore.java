package io.github.laplacedemon.qthings.mqtt.store;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import io.github.laplacedemon.qthings.mqtt.topic.TopicMessage;

public class TopicStore {
	private DB db;
	
	public TopicStore(String dirPath) throws IOException {
		DBFactory factory = new Iq80DBFactory();
		Options options = new Options();
		options.createIfMissing(true);
		this.db = factory.open(new File(dirPath), options);
	}
	
	public void store(String topic, byte[] payload, byte qos) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(1 + payload.length);
		buffer.put(qos);
		buffer.put(payload);
		this.db.put(topic.getBytes(Charset.forName("UTF-8")), buffer.array());
	}
	
	public TopicMessage load(String topic) {
		byte[] bs = this.db.get(topic.getBytes(Charset.forName("UTF-8")));
		if(bs == null) {
			return null;
		}
		ByteBuffer buf = ByteBuffer.wrap(bs);
		byte qos = buf.get();
		byte[] dst = new byte[bs.length - 1];
		buf.get(dst);
		
		TopicMessage topicMessage = new TopicMessage();
		topicMessage.setTopic(topic);
		topicMessage.setQos(qos);
		topicMessage.setPayload(dst);
		
		return topicMessage;
	}
	
}
