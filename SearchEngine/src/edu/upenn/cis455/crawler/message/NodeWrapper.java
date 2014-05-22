package edu.upenn.cis455.crawler.message;

import java.io.Serializable;

public class NodeWrapper implements Serializable {
	private static final long serialVersionUID = 8548911126559333753L;
	private String m_name;
	private String m_ipAddr;
	private int m_port;
	
	public NodeWrapper(String name, String ipAddr, int port) {
		this.m_name = name;
		this.m_ipAddr = ipAddr;
		this.m_port = port;
	}
	
	public void setName(String name) {
		this.m_name = name;
	}
	
	public void setIP(String ipAddr) {
		this.m_ipAddr = ipAddr;
	}
	
	public void setPort(int port) {
		this.m_port = port;
	}
	
	public String getName() {
		return this.m_name;
	}
	
	public String getIPAddr() {
		return this.m_ipAddr;
	}
	
	public int getPort() {
		return this.m_port;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(this.m_name + " " + this.m_ipAddr + ":" + this.m_port);
		return result.toString();
	}
}
