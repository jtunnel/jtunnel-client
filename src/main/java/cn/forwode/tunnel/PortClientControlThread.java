package cn.forwode.tunnel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class PortClientControlThread extends PortClientThread {

	public PortClientControlThread(TunnelConfig config) {
		super(config, "ControlThread-"+config.getRemotePort());
	}

	@Override
	protected void processSocket(Socket clientSocket) {
		new PortClientDataThread(config).start();
		try {
			PrintStream out = new PrintStream(clientSocket.getOutputStream());
			BufferedReader buf = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out.println(String.format("R=%s", config.getRemotePort()));
			boolean flag = true;
			while (flag) {
				try {
					String data = buf.readLine();
					if (data.equalsIgnoreCase("ping")) {
						out.println("pong");
					}
					if (data.equalsIgnoreCase("new")) {
						new PortClientDataThread(config).start();
					}
				} catch (SocketTimeoutException e) {
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
