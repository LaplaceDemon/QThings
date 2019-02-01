package io.github.laplacedemon.qthings.mqtt.store;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;

import io.github.laplacedemon.qthings.mqtt.protocal.packet.PublishPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MessagePersistentStorage {
	public static MessagePersistentStorage INS;
	static {
		INS = new MessagePersistentStorage();
	}
	
	private DB db;
	
	private MessagePersistentStorage() {
		this.db = LevelDB.INS;
	}
	
	public void save(String clientId, int msgSeqId, PublishPacket msg) {
		byte[] clientBytes = clientId.getBytes();
		ByteBuffer keyByteBuffer = ByteBuffer.allocate(clientBytes.length + 4);
		
		keyByteBuffer.put(clientBytes);
		keyByteBuffer.putInt(msgSeqId);
		
		ByteBuf valueBuf = Unpooled.buffer(msg.packetBufferSize());
		msg.write(valueBuf);
		
		this.db.put(keyByteBuffer.array(), valueBuf.array());
	}

	public List<PublishPacket> query(String clientId, int count) {
		List<PublishPacket> msgList = new ArrayList<>();
		DBIterator iterator = this.db.iterator();
		iterator.seek(clientId.getBytes());
		for (;count >= 0 ;) {
			Entry<byte[], byte[]> next = iterator.next();
			byte[] msgBytes = next.getValue();
			
			ByteBuf bb = Unpooled.wrappedBuffer(msgBytes);
			
			PublishPacket publishPacket = new PublishPacket();
			publishPacket.read(bb);
			
			msgList.add(publishPacket);
			count--;
	    }
		
		return msgList;
	}
}
