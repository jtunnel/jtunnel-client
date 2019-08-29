package cn.forwode.tunnel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadFromTargetThread extends Thread {
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	private Socket clientSocket;
	private Socket targetSocket;

	public ReadFromTargetThread(Socket clientSocket, Socket targetSocket) {
		super("ReadFromTargetThread-"+targetSocket.getRemoteSocketAddress().toString()+":"+targetSocket.getPort());
		this.clientSocket = clientSocket;
		this.targetSocket = targetSocket;
	}

	public void run() {
		int data = 0;
		try {
			InputStream inputStream = targetSocket.getInputStream();
			OutputStream outputStream = clientSocket.getOutputStream();
			byte[] buffer = new byte[1024000];
			while (data != -1 && !targetSocket.isClosed() && !clientSocket.isClosed()) {
				try {
					data = inputStream.read(buffer);
					if (data != -1) {
						outputStream.write(buffer, 0, data);
					} else {
						logger.error("read from target return -1");
						break;
					}
				} catch (SocketTimeoutException e) {
				}

			}
		} catch (IOException e) {
			logger.error("ReadFromTarget error", e);
		} finally {
			logger.error("socket closed, end ReadFromTargetThread.....");
			try {
				targetSocket.close();
			} catch (IOException e) {
				logger.error("close socket error", e);
			}

		}

	}

}
