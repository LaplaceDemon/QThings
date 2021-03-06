package io.github.laplacedemon.qthings.mqtt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.laplacedemon.qthings.mqtt.etc.ConfigInstance;
import io.github.laplacedemon.qthings.mqtt.handler.ClientIdSessionMapper;
import io.github.laplacedemon.qthings.mqtt.handler.MQTTDecoder;
import io.github.laplacedemon.qthings.mqtt.handler.MQTTEncoder;
import io.github.laplacedemon.qthings.mqtt.handler.MQTTHandler;
import io.github.laplacedemon.qthings.mqtt.topic.SubscribeTreeManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class MQTTServer {
	private final static Logger LOGGER = LoggerFactory.getLogger(MQTTServer.class);
	
	public static void main(String[] args) throws InterruptedException, IOException {
		ConfigInstance.loadConfig();
		
		final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		final EventLoopGroup workGroup = new NioEventLoopGroup();
		final SubscribeTreeManager topicTreeManager = new SubscribeTreeManager();
		final ClientIdSessionMapper clientIdSessionMapper = new ClientIdSessionMapper();
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workGroup)
				.channel(NioServerSocketChannel.class)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.SO_REUSEADDR, true)
				.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline()
						.addLast(new MQTTDecoder())
						.addLast(new MQTTEncoder())
						.addLast(new MQTTHandler(clientIdSessionMapper, topicTreeManager));
					}
				});

			final int port = ConfigInstance.INS.getServer().getPort();
			ChannelFuture f = bootstrap.bind(port).addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					LOGGER.info("mqtt server is running. server port is {}", port);
				}
				
			}).sync();
			
			f.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}
}
