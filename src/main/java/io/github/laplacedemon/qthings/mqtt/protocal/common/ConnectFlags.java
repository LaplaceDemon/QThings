package io.github.laplacedemon.qthings.mqtt.protocal.common;


public enum ConnectFlags {
	UserNameFlag {
		public boolean hasFlag(byte flags) {
			if((flags >> 7 & 0x01) == 1) {
				return true;
			}
			return false;
		}
	},
	PasswordFlag{
		public boolean hasFlag(byte flags) {
			if((flags >> 6 & 0x01) == 1) {
				return true;
			}
			return false;
		}
	},
	WillRetina {
		public boolean hasFlag(byte flags) {
			if((flags >> 5 & 0x01) == 1) {
				return true;
			}
			return false;
		}
	},
	WillQoS{
		public boolean hasFlag(byte flags) {
			return true;
		}
	},
	WillFlag {
		public boolean hasFlag(byte flags) {
			if((flags >> 2 & 0x01) == 1) {
				return true;
			}
			return false;
		}
	},
	CleanSession {
		public boolean hasFlag(byte flags) {
			if((flags >> 1 & 0x01) == 1) {
				return true;
			}
			return false;
		}
	},
	Reserved {
		public boolean hasFlag(byte flags) {
			return true;
		}
	};

	public boolean hasFlag(byte flags) {
		return false;
	}
	
}
