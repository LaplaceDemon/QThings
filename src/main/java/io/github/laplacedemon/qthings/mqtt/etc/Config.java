package io.github.laplacedemon.qthings.mqtt.etc;

import java.util.List;

public class Config {
	public static class Authenticate {
		public static class Account {
			private String username;
			private String password;

			public String getUsername() {
				return username;
			}

			public void setUsername(String username) {
				this.username = username;
			}

			public String getPassword() {
				return password;
			}

			public void setPassword(String password) {
				this.password = password;
			}

		}
		
		private boolean enable;
		private List<Account> accounts;

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public List<Account> getAccounts() {
			return accounts;
		}

		public void setAccounts(List<Account> accounts) {
			this.accounts = accounts;
		}
	}

	public static class Server {
		private int port;
		private int publishTimeout;
		private String dataPath;
		
		public int getPublishTimeout() {
			return publishTimeout;
		}

		public void setPublishTimeout(int publishTimeout) {
			this.publishTimeout = publishTimeout;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getDataPath() {
			return dataPath;
		}

		public void setDataPath(String dataPath) {
			this.dataPath = dataPath;
		}
		
	}

	private Authenticate authenticate;

	private Server server;

	public Authenticate getAuthenticate() {
		return authenticate;
	}

	public void setAuthenticate(Authenticate authenticate) {
		this.authenticate = authenticate;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

}
