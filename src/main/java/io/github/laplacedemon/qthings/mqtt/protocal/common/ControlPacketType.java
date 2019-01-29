package io.github.laplacedemon.qthings.mqtt.protocal.common;

import io.github.laplacedemon.qthings.mqtt.protocal.packet.ConnAckPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.ConnectPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.DisConnectPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.MQTTPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.PingReqPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.PingRespPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.PubAckPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.PubCompPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.PubRecPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.PubRelPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.PublishPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.SubAckPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.SubscribePacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.UnSubAckPacket;
import io.github.laplacedemon.qthings.mqtt.protocal.packet.UnSubscribePacket;

public enum ControlPacketType {
	Reserved0,
	CONNECT {
		public boolean is(byte flag) {
			if(flag >> 4 == (byte)1) {
				return true;
			}
			
			return false;
		}
		
		public MQTTPacket createPacket() {
			return new ConnectPacket();
		}
	},
	CONNACK {
		public boolean is(byte flag) {
			if(flag >> 4 == (byte)2) {
				return true;
			}
			
			return false;
		}
		
		public MQTTPacket createPacket() {
			return new ConnAckPacket();
		}
	},
	PUBLISH {
		public boolean is(byte flag) {
			if(flag >> 4 == (byte)3) {
				return true;
			}
			
			return false;
		}
		
		public MQTTPacket createPacket() {
			return new PublishPacket();
		}
	},
	PUBACK {
		public boolean is(byte flag) {
			if(flag >> 4 == (byte)4) {
				return true;
			}
			
			return false;
		}
		
		public MQTTPacket createPacket() {
			return new PubAckPacket();
		}
	},
	PUBREC {
		public boolean is(byte flag) {
			if(flag >> 4 == (byte)5) {
				return true;
			}
			
			return false;
		}
		
		public MQTTPacket createPacket() {
			return new PubRecPacket();
		}
	},
	PUBREL {
		public boolean is(byte flag) {
			if(flag >> 4 == (byte)6) {
				return true;
			}
			
			return false;
		}
		
		public MQTTPacket createPacket() {
			return new PubRelPacket();
		}
	},
	PUBCOMP {
		public boolean is(byte flag) {
			if(flag >> 4 == (byte)7) {
				return true;
			}
			
			return false;
		}
		
		public MQTTPacket createPacket() {
			return new PubCompPacket();
		}
	},
	SUBSCRIBE {
		public boolean is(byte flag) {
			if(flag >> 4 == (byte)8) {
				return true;
			}
			
			return false;
		}
		
		public MQTTPacket createPacket() {
			return new SubscribePacket();
		}
	},
	SUBACK {
		public boolean is(byte flag) {
			if(flag >> 4 == (byte)9) {
				return true;
			}
			
			return false;
		}
		
		public MQTTPacket createPacket() {
			return new SubAckPacket();
		}
	},
	UNSUBSCRIBE {
		public boolean is(byte flag) {
			if(flag >> 4 == (byte)10) {
				return true;
			}
			
			return false;
		}
		
		public MQTTPacket createPacket() {
			return new UnSubscribePacket();
		}
	},
	UNSUBACK {
		public boolean is(byte flag) {
			if(flag >> 4 == (byte)11) {
				return true;
			}
			
			return false;
		}
		
		public MQTTPacket createPacket() {
			return new UnSubAckPacket();
		}
	},
	PINGREQ {
		public boolean is(byte flag) {
			if(flag >> 4 == (byte)12) {
				return true;
			}
			
			return false;
		}
		
		public MQTTPacket createPacket() {
			return new PingReqPacket();
		}
	},
	PINGRESP {
		public boolean is(byte flag) {
			if(flag >> 4 == (byte)13) {
				return true;
			}
			
			return false;
		}
		
		public MQTTPacket createPacket() {
			return new PingRespPacket();
		}
	},
	DISCONNECT {
		public boolean is(byte flag) {
			if(flag >> 4 == (byte)14) {
				return true;
			}
			
			return false;
		}
		
		public MQTTPacket createPacket() {
			return new DisConnectPacket();
		}
	},
	Reserved1;

	public boolean is(byte flag) {
		return false;
	}

	public MQTTPacket createPacket() {
		return null;
	}

	public static ControlPacketType valueOf(int i) {
		ControlPacketType[] values = ControlPacketType.values();
		return values[i];
	}
	
}
