package cn.forwode.tunnel;

public class KeepAlivedThread extends Thread {

	private Thread controlThread;
	
	public KeepAlivedThread() {
		super("KeepAlivedThread");
		this.controlThread = new ClientControlThread();
		controlThread.start();
	}
	
	@Override
	public void run() {
		while(true) {
			if (!controlThread.isAlive()) {
				controlThread = new ClientControlThread();
				controlThread.start();
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
