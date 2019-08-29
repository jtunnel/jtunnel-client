package cn.forwode.tunnel;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
	final static Logger logger = LoggerFactory.getLogger(Client.class);
	
	public static void main(String[] args) throws IOException {
		logger.info("start client..........");
		TunnelConfig config = new TunnelConfig();
		config.setServerIp("tx.forwode.cn");
		config.setServerPort(20006);
		config.setRemotePort("9090");
		config.setTargetIp("127.0.0.1");
		config.setTargetPort(22);
		new PortClientControlThread(config).start();

		while(true) {
			try {
				Thread.sleep(10000000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
