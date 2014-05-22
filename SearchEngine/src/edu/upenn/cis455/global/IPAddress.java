package edu.upenn.cis455.global;

/**
 * This class is a container to wrap IP address and port number
 * 
 * @author martinng
 *
 */
public class IPAddress {
	/*
	 * Porperties
	 */
	private String m_ipAddress = null;
	private int m_port = -1;
	
	/**
	 * Constructor: sets up
	 * 
	 * @param ipAddress
	 * @param port
	 */
	public IPAddress(String ipAddress, int port) {
		this.m_ipAddress = ipAddress;
		this.m_port = port;
	}
	
	/*
	 * Set methods
	 */
	/**
	 * This function sets IP address
	 * 
	 * @param ipAddress
	 */
	public void setIPAddress(String ipAddress) {
		this.m_ipAddress = ipAddress;
	}
	
	/**
	 * This function sets port number
	 * 
	 * @param port
	 */
	public void setPortNumber(int port) {
		this.m_port = port;
	}
	
	/*
	 * Get methods
	 */
	/**
	 * This function returns IP address
	 * @return
	 */
	public String getIPAddress() {
		return this.m_ipAddress;
	}
	
	/**
	 * This function returns port number
	 * @return
	 */
	public int getPort() {
		return this.m_port;
	}
}
