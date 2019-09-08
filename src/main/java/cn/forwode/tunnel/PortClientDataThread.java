package cn.forwode.tunnel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortClientDataThread extends Thread {
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	private TunnelConfig config;
	
	public PortClientDataThread(TunnelConfig config) {
		super("DataThread-"+config.getRemotePort());
		this.config = config;
	}
	
	@Override
	public void run() {
		String processid = ManagementFactory.getRuntimeMXBean().getName();
		Socket clientSocket = null;
		try {
			clientSocket = new Socket(config.getServerIp(), config.getServerPort());	
			clientSocket.setSoTimeout(3000);
			PrintStream out = new PrintStream(clientSocket.getOutputStream());
			BufferedReader buf = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out.println(String.format("R=%s=%s", processid, config.getRemotePort()));
			boolean flag = true;
			while (flag) {
				try {
					String data = buf.readLine();
					logger.info("DATA line reply = "+ data);
					if (data.equals("200") || data.equals("201") ) {
						logger.info("DATA line OK! "+config.getRemotePort());
						new WriteToTargetThread(clientSocket, config).start();
						flag = false;
					} else if (data.equals("400") || data.equals("500")) {
						clientSocket.close();
						flag = false;
					} else {
						flag = false;
					}
				} catch (SocketTimeoutException e) {
				}

			}
			
		} catch (UnknownHostException e) {
			logger.error("unknown host exception", e);
			try {
				clientSocket.close();
			} catch (IOException e1) {
				logger.error("IO error", e);
			}
		} catch (Exception e) {
			logger.error("error", e);
			try {
				clientSocket.close();
			} catch (IOException e1) {
				logger.error("IO error", e);
			}
		}
	}
}
