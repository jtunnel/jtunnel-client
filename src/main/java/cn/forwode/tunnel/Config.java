package cn.forwode.tunnel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config {
	
	private static Config instance = new Config();
	
	private String serverIp;
	private Integer serverPort;
	private Map<Integer, Target> rTable;
	private Map<Integer, Target> lTable;
	private Integer dynamicPort;
	private Boolean enabledDynamic;
	
	private Config() {
		rTable = new HashMap<Integer, Target>();
		lTable = new HashMap<Integer, Target>();
	}
	
    public static Config getInstance(){
        return instance;
    }
    
    public Target getRemoteTarget(Integer port) {
    	return rTable.get(port);
    }

	public Integer getDynamicPort() {
		return dynamicPort;
	}

	public void setDynamicPort(Integer dynamicPort) {
		this.dynamicPort = dynamicPort;
	}
	
	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public Integer getServerPort() {
		return serverPort;
	}

	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}

	public Map<Integer, Target> getrTable() {
		return rTable;
	}

	public void setrTable(Map<Integer, Target> rTable) {
		this.rTable = rTable;
	}

	public Map<Integer, Target> getlTable() {
		return lTable;
	}

	public void setlTable(Map<Integer, Target> lTable) {
		this.lTable = lTable;
	}

	public Boolean getEnabledDynamic() {
		return enabledDynamic;
	}

	public void setEnabledDynamic(Boolean enabledDynamic) {
		this.enabledDynamic = enabledDynamic;
	}

	public void load(String filePath) throws IOException {
		Properties p = new Properties();
		FileInputStream fis = new FileInputStream(filePath);
		p.load(fis);
		serverIp = p.getProperty("server.ip", "127.0.0.1");
		serverPort = Integer.parseInt(p.getProperty("server.port", "20006"));
		dynamicPort = Integer.parseInt(p.getProperty("dynamic.forward.port", "1080"));
		enabledDynamic = Boolean.parseBoolean(p.getProperty("dynamic.forward.enabled", "false"));
		
		p.forEach(
				(key, value) ->{
					if (key.toString().startsWith("remote.forward.")) {
						String[] remote = value.toString().split(":");
						Integer remotePort = Integer.parseInt(remote[0]);
						String targetIp = remote[1];
						Integer targetPort = Integer.parseInt(remote[2]);
						Target target = new Target(targetIp, targetPort);
						rTable.put(remotePort, target);
					}
					if (key.toString().startsWith("local.forward.")) {
						String[] local = value.toString().split(":");
						Integer localPort = Integer.parseInt(local[0]);
						String targetIp = local[1];
						Integer targetPort = Integer.parseInt(local[2]);
						Target target = new Target(targetIp, targetPort);
						lTable.put(localPort, target);
					}
				}
		);
	}
	
}
