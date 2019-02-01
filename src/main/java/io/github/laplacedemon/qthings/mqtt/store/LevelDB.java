package io.github.laplacedemon.qthings.mqtt.store;

import java.io.File;
import java.io.IOException;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.laplacedemon.qthings.mqtt.etc.ConfigInstance;

public class LevelDB {
	private final static Logger LOGGER = LoggerFactory.getLogger(LevelDB.class);
	public static DB INS = null;
	
	static {
		DBFactory factory = new Iq80DBFactory();
		Options options = new Options();
		options.createIfMissing(true);
		String dataPath = ConfigInstance.INS.getServer().getDataPath();
		try {
			INS = factory.open(new File(dataPath), options);
		} catch (IOException e) {
			LOGGER.error("", e);
		}
	}
	
}
