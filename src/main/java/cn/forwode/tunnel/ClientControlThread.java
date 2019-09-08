package cn.forwode.tunnel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientControlThread extends Thread{
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	private String serverIp;
	private Integer serverPort;
	
	public ClientControlThread() {
		super("ControlThread");
		serverIp = Config.getInstance().getServerIp();
		serverPort = Config.getInstance().getServerPort();
	}
	
	@Override
	public void run() {
		Socket clientSocket = null;
		String processId = ManagementFactory.getRuntimeMXBean().getName();
		String serverIp = Config.getInstance().getServerIp();
		Integer serverPort = Config.getInstance().getServerPort();
		try {
			clientSocket = new Socket(serverIp, serverPort);	
			clientSocket.setSoTimeout(3000);
			PrintStream out = new PrintStream(clientSocket.getOutputStream());
			BufferedReader buf = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out.println(String.format("CTRL=%s", processId));
			boolean flag = true;
			while (flag) {
				String data = "";
				try {
					data = buf.readLine();
					if (data.equals("200")) {
						logger.info("CTRL bus OK! start DATA bus...");
						initDataThread();
					}
					if (data.equalsIgnoreCase("ping")) {
						out.println("pong");
					}
					if (data.startsWith("NEW=")) {
						String port = data.split("=")[1];
						newRemoteForwardDataThread(Integer.parseInt(port));
					}
				} catch (SocketTimeoutException e) {
				}

			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void initDataThread() {
		Config config = Config.getInstance();
		Map<Integer, Target> rTable = config.getrTable();
		rTable.forEach(
				(k,v) -> {
					newRemoteForwardDataThread(k);
				}
				);
	}
	
	private void newRemoteForwardDataThread(Integer port) {
		TunnelConfig tunnelConfig = new TunnelConfig();
		tunnelConfig.setServerIp(serverIp);
		tunnelConfig.setServerPort(serverPort);
		tunnelConfig.setRemotePort(port.toString());
		Target target = Config.getInstance().getRemoteTarget(port);
		tunnelConfig.setTarget(target);
		new PortClientDataThread(tunnelConfig).start();
	}

}
