package cn.forwode.tunnel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteToTargetThread extends Thread {

	final Logger logger = LoggerFactory.getLogger(getClass());
	private Socket clientSocket;
	private Socket targetSocket;
	private TunnelConfig config;

	public WriteToTargetThread(Socket clientSocket, TunnelConfig config) {
		super("WriteToTargetThread-" + config.getTargetIp() + "-" + config.getTargetPort());
		this.clientSocket = clientSocket;
		this.config = config;
	}

	@Override
	public void run() {
		int data = 0;
		try {
			InputStream inputStream = clientSocket.getInputStream();
			byte[] buffer = new byte[1024000];
			while (data != -1 && !clientSocket.isClosed()) {
				try {
					data = inputStream.read(buffer);
					if (data != -1) {

						if (targetSocket == null || targetSocket.isClosed() || targetSocket.isInputShutdown()
								|| targetSocket.isOutputShutdown()) {
							targetSocket = new Socket(config.getTargetIp(), config.getTargetPort());
							targetSocket.setSoTimeout(3000);
							new ReadFromTargetThread(clientSocket, targetSocket).start();
						}
						OutputStream outputStream = targetSocket.getOutputStream();
						outputStream.write(buffer, 0, data);

					} else {
						logger.error("read from server return -1");
						break;
					}

				} catch (SocketTimeoutException e) {
				}

			}
		} catch (IOException e) {
			logger.error("WriteToTarget error", e);
		} finally {
			logger.error("socket closed, end WriteToTargetThread.....");
			try {
				clientSocket.close();
				targetSocket.close();
			} catch (Exception e) {
				logger.error("close socket error", e);
			}

		}
	}
}
