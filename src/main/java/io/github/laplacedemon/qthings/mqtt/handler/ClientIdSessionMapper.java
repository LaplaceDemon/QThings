package io.github.laplacedemon.qthings.mqtt.handler;

import java.util.concurrent.ConcurrentSkipListMap;

import io.github.laplacedemon.qthings.mqtt.topic.Session;

public class ClientIdSessionMapper {
	private ConcurrentSkipListMap<String, Session> map;
	
	public ClientIdSessionMapper() {
		this.map = new ConcurrentSkipListMap<>();
	}
	
	public Session put(String clientId, Session session) {
		Session oldSession = this.map.put(clientId, session);
		return oldSession;
	}
}
