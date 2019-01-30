package io.github.laplacedemon.qthings.mqtt.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.laplacedemon.qthings.mqtt.etc.Config.Authenticate.Account;
import io.github.laplacedemon.qthings.mqtt.etc.ConfigInstance;
import io.github.laplacedemon.qthings.mqtt.protocal.common.ConnectAckType;
import io.github.laplacedemon.qthings.mqtt.protocal.common.ControlPacketType;
import io.github.laplacedemon.qthings.mqtt.protocal.common.QoS;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.ConnAckPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.ConnectPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.MQTTPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.PingRespPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.PubAckPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.PublishPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.SubAckPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.SubscribePacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.SubscribePacket.TopicFilter;
import io.github.laplacedemon.qthings.mqtt.store.TopicStore;
import io.github.laplacedemon.qthings.mqtt.topic.Session;
import io.github.laplacedemon.qthings.mqtt.topic.SubscribeTreeManager;
import io.github.laplacedemon.qthings.mqtt.topic.Subscriber;
import io.github.laplacedemon.qthings.mqtt.topic.TopicMessage;
import io.github.laplacedemon.qthings.mqtt.topic.WillMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MQTTHandler extends ChannelInboundHandlerAdapter {
	private final static Logger LOGGER = LoggerFactory.getLogger(MQTTHandler.class);
	private SubscribeTreeManager topicTreeManager;
	private ClientIdSessionMapper clientIdSessionMapper;
	private TopicStore topicStore;
	
	public MQTTHandler(ClientIdSessionMapper clientIdSessionMapper, SubscribeTreeManager topicTreeManager, TopicStore topicStore) {
		this.clientIdSessionMapper= clientIdSessionMapper;
		this.topicTreeManager = topicTreeManager;
		this.topicStore = topicStore;
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
		LOGGER.error("", cause);
		ChannelUtil.closeChannelHandlerContext(ctx).addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				Session session = ChannelUtil.sessuibOnChannel(ctx.channel());
				if(session.isWillFlag()) {
					WillMessage willMessage = session.getWillMessage();
					PublishPacket publishPacket = new PublishPacket();
					publishPacket.setTopicName(willMessage.getTopic());
					publishPacket.setRetain(willMessage.isRetain());
					publishPacket.setPayload(willMessage.getPayload());
					publishPacket.setQos(willMessage.getQos());
					publish(publishPacket);
				}
			}
		});
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx,final Object msg) throws Exception {
		MQTTPacket mqttPacket = (MQTTPacket)msg;
		ControlPacketType type = mqttPacket.getType();
		switch (type) {
		case CONNECT : {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug(mqttPacket.toString());
			}
			ConnectPacket connectPacket = (ConnectPacket)mqttPacket;
			
			// authenticate
			if(ConfigInstance.INS.getAuthenticate().isEnable()) {
				String username = connectPacket.getUsername();
				String password = connectPacket.getPassword();
				
				List<Account> accounts = ConfigInstance.INS.getAuthenticate().getAccounts();
				
				boolean authSuccess = false;
				for(Account account :accounts) {
					if (account.getUsername().equals(username) && account.getPassword().equals(password)) {
						authSuccess = true;
						break;
					}
				}
				
				if (!authSuccess) {
					ConnAckPacket connAckPacket = new ConnAckPacket();
					connAckPacket.setConnectReturnCode(ConnectAckType.Unauthorized.getReturnCode());
					ctx.writeAndFlush(connAckPacket).addListener(new ChannelFutureListener() {
						
						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							ChannelUtil.closeChannel(ctx.channel());
						}
					});
					break;
				}
			}
			
			// clientId
			String clientIdentifier = connectPacket.getClientIdentifier();
			if(clientIdentifier != null && clientIdentifier.length() > 0) {
				if(clientIdentifier.length() > 23) {
					ConnAckPacket connAckPacket = new ConnAckPacket();
					connAckPacket.setConnectReturnCode(ConnectAckType.UnqualifiedClientIdentifier.getReturnCode());
					ctx.writeAndFlush(connAckPacket);
					break;
				}
				
				clientIdentifier = ("#" + clientIdentifier);
			} else {
				Channel channel = ctx.channel();
				clientIdentifier = ("!" + channel.id());
			}
			
			// will message
			WillMessage willMessage = null;
			boolean willFlag = false;
			if(connectPacket.isWillFlag()) {
				willFlag = true;
				willMessage = new WillMessage();
				willMessage.setPayload(connectPacket.getWillPayload());
				willMessage.setTopic(connectPacket.getWillTopic());
				willMessage.setQos(connectPacket.getWillQoS());
				willMessage.setRetain(connectPacket.isWillRetina());
			}
			
			Session session = new Session(ctx.channel(), willFlag, willMessage);
			Session oldSession = this.clientIdSessionMapper.put(clientIdentifier, session);
			if(oldSession != null) {
				oldSession.close();
			}
			
			// keep alive
			int keepAlive = connectPacket.getKeepAlive();
			int timeout = (int)(keepAlive * 1.5);
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("the keep alive time: {} s", keepAlive);
			}
			ReadTimeoutHandler readTimeoutHandler = new ReadTimeoutHandler(timeout, TimeUnit.SECONDS);
			ctx.pipeline().addFirst(readTimeoutHandler);
			
			// connack message
			ConnAckPacket connAckPacket = new ConnAckPacket();
			ctx.writeAndFlush(connAckPacket);
			break;
		}
		case DISCONNECT : {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("disconnect");
			}
			ChannelUtil.closeChannelHandlerContext(ctx);
			break;
		}
		case PUBLISH : {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("publish");
			}
			PublishPacket publishPacket = (PublishPacket)mqttPacket;
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug(publishPacket.toString());
			}
			
			if(publishPacket.isRetain()) {
				// 存储消息
				topicStore.store(publishPacket.getTopicName(), publishPacket.getPayload(), publishPacket.getQos().getValue());
				if(LOGGER.isDebugEnabled()) {
					LOGGER.debug("save retain message");
				}
			}
			
			QoS qos = publishPacket.getQos();
			if(qos == QoS.AtLeastOnce) {
				PubAckPacket pubAckPacket = new PubAckPacket();
				pubAckPacket.setQos(QoS.AtLeastOnce);
				pubAckPacket.setPacketSeq(publishPacket.getPacketSeq());
				ctx.writeAndFlush(pubAckPacket);
			}
			
			publish((PublishPacket)publishPacket.clone());
			break;
		}
		case PUBACK : {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("puback");
			}
			PubAckPacket pubAckPacket = (PubAckPacket)mqttPacket;
			Subscriber subscriber = ChannelUtil.subscriberOnChannelHandlerContext(ctx);
			if(subscriber == null) {
				break;
			}
			
			subscriber.recvPubAck(pubAckPacket);
			break;
		}
		case SUBSCRIBE : {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("subscribe");
			}
			
			SubscribePacket subscribePacket = (SubscribePacket)mqttPacket;
			List<TopicFilter> topicFilters = subscribePacket.getTopicFilters();
			int returnListSize = topicFilters.size();
			List<Byte> qosReturnList = new ArrayList<>(returnListSize);
			List<Subscriber> subscriberList = new ArrayList<>(returnListSize);
			for(TopicFilter topicFilter : topicFilters) {
				Subscriber subscribe = topicTreeManager.subscribe(topicFilter, ctx);
				qosReturnList.add((byte)topicFilter.getQos());
				subscriberList.add(subscribe);
			}
			
			int packetSeq = subscribePacket.getPacketSeq();
			SubAckPacket subAckPacket = new SubAckPacket();
			
			subAckPacket.setPacketSeq(packetSeq);
			subAckPacket.setQosReturnList(qosReturnList);
			
			ctx.writeAndFlush(subAckPacket);
			
			// Retain Message
			for(TopicFilter topicFilter : topicFilters) {
				String filter = topicFilter.getFilter();
				TopicMessage topicMessage = this.topicStore.load(filter);
				if(topicMessage == null) {
					continue ;
				}
				
				for(Subscriber subscriber : subscriberList) {
					PublishPacket publishPacket = new PublishPacket();
					publishPacket.setTopicName(topicMessage.getTopic());
					publishPacket.setQos(QoS.valueOf(topicMessage.getQos()));
					publishPacket.setPayload(topicMessage.getPayload());
					publishPacket.setRetain(true);
					subscriber.publish(publishPacket);
				}
			}
			
			break;
		}
		case PINGREQ : {
			/*
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("ping request");
			}
			*/
			PingRespPacket pingRespPacket = new PingRespPacket();
			ctx.writeAndFlush(pingRespPacket);
			break;
		}
		default:
			break;
		}
	}
	
	private void publish(PublishPacket publishPacket) {
		ConcurrentSkipListSet<Subscriber> subscriberSet = topicTreeManager.publish(publishPacket.getTopicName());
		if(subscriberSet == null) {
			return ;
		}
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("subscriber size:{}", subscriberSet.size());
		}
		
		for(Subscriber subscriber : subscriberSet) {
			if(subscriber.isActive()) {
				subscriber.publish(publishPacket);
			} else {
				subscriber.remove();
			}
		}
	}
	
}
