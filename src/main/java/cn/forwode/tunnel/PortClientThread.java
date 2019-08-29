package cn.forwode.tunnel;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class PortClientThread extends Thread {
	
	protected TunnelConfig config;
	
	public PortClientThread(TunnelConfig config, String name) {
		super(name);
		this.setConfig(config);
	}
	
	@Override
	public void run() {
		try {
			Socket clientSocket = new Socket(getConfig().getServerIp(), getConfig().getServerPort());	
			clientSocket.setSoTimeout(3000);
			PrintStream out = new PrintStream(clientSocket.getOutputStream());
			out.println(String.format("R=%s", config.getRemotePort()));
			processSocket(clientSocket);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected abstract void processSocket(Socket clientSocket);

	public TunnelConfig getConfig() {
		return config;
	}

	public void setConfig(TunnelConfig config) {
		this.config = config;
	}

}
