package cn.forwode.tunnel;

import java.net.Socket;

public class PortClientDataThread extends PortClientThread {

	public PortClientDataThread(TunnelConfig config) {
		super(config, "DataThread-"+config.getRemotePort());
	}

	@Override
	protected void processSocket(Socket clientSocket) {
		new WriteToTargetThread(clientSocket, config).start();
	}

}
