package edu.upenn.cis455.crawler.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LinkMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7356607762495303489L;
	private List<Link> m_links;
	private String m_sourceIP;
	private int m_sourcePort;
	private String m_destIP;
	private int m_destPort;
	
	public LinkMessage(String sourceIP, int sourcePort, String destIP,
			int destPort) {
		this.m_sourceIP = sourceIP;
		this.m_sourcePort = sourcePort;
		this.m_destIP = destIP;
		this.m_destPort = destPort;
		this.m_links = new ArrayList<Link>();
	}
	
	/**
	 * This function sets links to crawl
	 * 
	 * @param links
	 */
	public void setLinks(List<Link> links) {
		this.m_links = links;
	}
	
	/**
	 * This function adds a new link to list
	 * 
	 * @param link
	 */
	public void addLink(Link link) {
		this.m_links.add(link);
	}
	
	/**
	 * This function returns links to crawl
	 * 
	 * @return
	 */
	public List<Link> getLinks() {
		return this.m_links;
	}
	
	/**
	 * This function returns the source IP address
	 * 
	 * @return
	 */
	public String getSourceIP() {
		return this.m_sourceIP;
	}
	
	/**
	 * This function returns the source port number
	 * 
	 * @return
	 */
	public int getSourcePort() {
		return this.m_sourcePort;
	}
	
	/**
	 * This function returns the destination IP address
	 * 
	 * @return
	 */
	public String getDestIP() {
		return this.m_destIP;
	}
	
	/**
	 * This function returns the destination port number
	 * 
	 * @return
	 */
	public int getDestPort() {
		return this.m_destPort;
	}
}
