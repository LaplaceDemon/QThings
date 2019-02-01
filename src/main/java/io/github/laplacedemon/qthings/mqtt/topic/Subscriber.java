package io.github.laplacedemon.qthings.mqtt.topic;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.laplacedemon.qthings.mqtt.handler.ChannelUtil;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.PubAckPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.PublishPacket;
import io.github.laplacedemon.qthings.mqtt.store.MessagePersistentStorage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.concurrent.ScheduledFuture;

public class Subscriber implements Comparable<Subscriber> {
	private AtomicUnsignedShort publishSeqId;
	private ArrayBlockingQueue<PublishPacket> queue;
	private AtomicBoolean online;
	private int qos;
	private ChannelHandlerContext ctx;
	private String topicFilter;
	private ConcurrentSkipListSet<Subscriber> topicTreeRoot;
	private ScheduledFuture<?> schedule;
	
	public Subscriber(ChannelHandlerContext ctx, int qos, String topicFilter) {
		super();
		this.online = new AtomicBoolean(true);
		this.publishSeqId = new AtomicUnsignedShort(0);
		this.queue = new ArrayBlockingQueue<>(100000);
		this.qos = qos;
		this.ctx = ctx;
		this.topicFilter = topicFilter;
		Attribute<Subscriber> attribute = this.ctx.channel().attr(ChannelUtil.SUBSCRIBER_CHANNEL_KEY);
		attribute.set(this);
	}

	public int getQos() {
		return qos;
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	@Override
	public int compareTo(Subscriber other) {
		return this.ctx.channel().id().compareTo(other.ctx.channel().id());
	}

	@Override
	public String toString() {
		return "Subscriber [qos=" + qos + ", ctx=" + ctx + "]";
	}
	
	public boolean isActive() {
		return this.ctx.channel().isActive();
	}
	
	private void writeAndFlush(final PublishPacket msg) {
		this.ctx.writeAndFlush(msg);
		if(this.schedule == null || this.schedule.isCancelled()) {
			// new schedule
			this.schedule = this.ctx.executor().schedule(() -> {
				// get the last msg
				System.err.println("retry publish");
				PublishPacket lastPacket = this.queue.peek();
				if (lastPacket.getPacketSeq() == msg.getPacketSeq()) {
					this.ctx.writeAndFlush(msg);
				}
			}, 10000, TimeUnit.SECONDS);
		}
	}
	
	public void publish(final PublishPacket msg) {
		int msgSeqId = publishSeqId.incrementAndGet();
		msg.setPacketSeq(msgSeqId);
		if (ctx.channel().isActive()) {
			int size = this.queue.size();
			boolean add = this.queue.add(msg);
			if(add && size == 0) {
				writeAndFlush(msg);
				return ;
			}
		}
		
		// channel 不在线，若session不需要clean，则持久化消息。
		Session session = ChannelUtil.sessionOnChannel(ctx.channel());
		if (!session.isCleanSession()) {
			MessagePersistentStorage.INS.save(session.getClientId(), msgSeqId, msg);
		}
	}

	public String getTopicFilter() {
		return topicFilter;
	}

	public void removeFromSubscribeTree() {
		if(topicTreeRoot != null) {
			this.topicTreeRoot.remove(this);
		}
	}

	public void setTopicTreeRoot(ConcurrentSkipListSet<Subscriber> topicTreeRoot) {
		this.topicTreeRoot = topicTreeRoot;
	}

	public void recvPubAck(PubAckPacket pubAckPacket) {
		PublishPacket peek = this.queue.peek();
		PublishPacket packet = peek;
		if(packet == null) {
			return ;
		}
		
		if (packet.getPacketSeq() == pubAckPacket.getPacketSeq()) {
			this.queue.poll();
			this.schedule.cancel(false);
			System.err.println("remove last msg");
			
			// get the last msg
			PublishPacket lastPacket = this.queue.peek();
			if(lastPacket != null) {
				this.writeAndFlush(lastPacket);
			}
		}
	}
	
	public boolean offline() {
		return this.online.compareAndSet(true, false);
	}
	
	public boolean online() {
		return this.online.compareAndSet(false, true);
	}

	public PublishPacket poll() {
		return this.queue.poll();
	}
}
