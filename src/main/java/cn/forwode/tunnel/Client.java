package cn.forwode.tunnel;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
	final static Logger logger = LoggerFactory.getLogger(Client.class);
	
	public static void main(String[] args) throws IOException {
		logger.info("start client..........");
		String filePath = "";
		if (args.length >= 1) {
			filePath = args[0];
			File file = new File(filePath);
			if (!file.exists()) {
				logger.error(String.format("%s config file not exists!", filePath));
			} else {
				Config.getInstance().load(filePath);
			}
		}
		Thread keepAlivedThread = new KeepAlivedThread();
		keepAlivedThread.start();
		
		while(true) {
			try {
				Thread.sleep(10000000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
