package io.github.laplacedemon.qthings.mqtt.topic;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.laplacedemon.qthings.mqtt.protocal.packet.SubscribePacket.TopicFilter;
import io.netty.channel.ChannelHandlerContext;

public class SubscribeTreeManager {
	private final static Logger LOGGER = LoggerFactory.getLogger(SubscribeTreeManager.class);
	private ConcurrentSkipListMap<String, ConcurrentSkipListSet<Subscriber>> topicTree;
	
	public SubscribeTreeManager() {
		topicTree = new ConcurrentSkipListMap<>();
	}
	
	public ConcurrentSkipListSet<Subscriber> publish(final String topic) {
		ConcurrentSkipListSet<Subscriber> concurrentSkipListSet = this.topicTree.get(topic);
		return concurrentSkipListSet;
	}

	public Subscriber subscribe(TopicFilter topicFilter, ChannelHandlerContext ctx) {
		ConcurrentSkipListSet<Subscriber> set = topicTree.computeIfAbsent(topicFilter.getFilter(), (key)->{
			return new ConcurrentSkipListSet<>();
		});
		
		Subscriber subscriber = new Subscriber(ctx, topicFilter.getQos(), topicFilter.getFilter());
		boolean add = set.add(subscriber);
		if(add) {
			subscriber.setTopicTreeRoot(set);
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("add subscriber:{}", ctx.channel().id());
			}
		}
		
		return subscriber;
	}

	@Override
	public String toString() {
		return "TopicTreeManager <" + topicTree + ">";
	}
	
}
