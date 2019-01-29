package io.github.laplacedemon.qthings.mqtt.etc;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

public class ConfigInstance {
	public static Config INS;
	
	public static void loadConfig() throws FileNotFoundException, YamlException {
		YamlReader yamlReader = new YamlReader(new FileReader("etc/qthings.yml"));
		Config config = yamlReader.read(Config.class);
		
		INS = config;
	}
}
