package edu.upenn.cis455.crawler.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.upenn.cis455.global.MessageType;
import edu.upenn.cis455.utils.SHA1Util;

/**
 * This function is message object for communication between crawler node and node
 * 
 * @author martinng
 *
 */
public class CrawlerMessage implements Serializable {
	private static final long serialVersionUID = -7154347015797475842L;
	/*
	 * Properties
	 */
	private MessageType m_messageType;
	private String m_name;
	private String m_sourceIP;
	private int m_sourcePort;
	private String m_destIP;
	private int m_destPort;
	private int m_sourceOptionalPort;
	private boolean m_isHasEntryURL;
	private String m_urlSHA;
	private byte[] m_diget;
	private boolean m_isSeen;
	private List<Link> m_links;
	private List<NodeWrapper> m_crawlerNodes;
	
	/**
	 * Constructor: sets up message type, source ip, source port, destination
	 * ip, and destination port
	 * 
	 * @param sourceIP
	 * @param sourcePort
	 * @param destIP
	 * @param destPort
	 * @param messageType
	 */
	public CrawlerMessage(String sourceIP, int sourcePort, String destIP,
			int destPort, MessageType messageType) {
		this.m_sourceIP = sourceIP;
		this.m_sourcePort = sourcePort;
		this.m_destIP = destIP;
		this.m_destPort = destPort;
		this.m_messageType = messageType;
		this.m_links = new ArrayList<Link>();
		this.m_isHasEntryURL = false;
	}
	
	/*
	 * Set methods
	 */
	public void setName(String name) {
		this.m_name = name;
	}
	
	public void setOptionalPort(int optionalPort) {
		this.m_sourceOptionalPort = optionalPort;
	}
	/**
	 * This function sets crawler nodes that will work
	 * 
	 * @param url
	 */
	public void setCrawlerNodes(List<NodeWrapper> crawlerNodes) {
		this.m_crawlerNodes = crawlerNodes;
	}
	/**
	 * This function sets entry URL
	 * 
	 * @param url
	 */
	public void setEntryURLs(List<Link> links) {
		this.m_links = links;
		this.m_isHasEntryURL = true;
	}
	
	/**
	 * This function add an entry URL
	 * 
	 * @param url
	 */
	public void addEntryURL(Link link) {
		this.m_links.add(link);
		this.m_isHasEntryURL = true;
	}
	
	/**
	 * This function adds a new entry URL to list
	 * 
	 * @param link
	 */
	public void getEntryURL(Link link) {
		this.m_links.add(link);
		this.m_isHasEntryURL = true;
	}
	
	/**
	 * This function sets URL
	 * @param url
	 */
	public void setURLSHA(String url) {
		this.m_urlSHA = SHA1Util.byteToString(SHA1Util.generateSHA(url));
	}
	
	
	/**
	 * This function sets seen digest content
	 * 
	 * @param digest
	 */
	public void setDigest(byte[] digest) {
		this.m_diget = digest;
	}
	
	/**
	 * This function sets whether the content has been seen
	 * 
	 * @param isSeen
	 */
	public void setIsSeen(boolean isSeen) {
		this.m_isSeen = isSeen;
	}
	
	/*
	 * Get methods
	 */
	/**
	 * This function returns URL
	 * 
	 * @return
	 */
	public String getURLSHA() {
		return this.m_urlSHA;
	}
	
	
	/**
	 * This function returns seen digest content
	 * 
	 * @return
	 */
	public byte[] getSeenDigest() {
		return this.m_diget;
	}
	
	/**
	 * This function checks if the content has been seen
	 * 
	 * @return
	 */
	public boolean isSeen() {
		return this.m_isSeen;
	}
	
	/**
	 * This function returns the message type
	 * 
	 * @return
	 */
	public MessageType getMessageType() {
		return this.m_messageType;
	}
	
	/**
	 * This function checks if this message contains entry URL
	 * 
	 * @return
	 */
	public boolean isHashEntryURL() {
		return this.m_isHasEntryURL;
	}
	
	/**
	 * This function returns address of crawler nodes that will work
	 * 
	 * @param url
	 */
	public List<NodeWrapper> geetCrawlerNodes() {
		return this.m_crawlerNodes;
	}
	
	public int getOptionalPort() {
		return this.m_sourceOptionalPort;
	}
	
	/**
	 * This function returns links to crawl
	 * 
	 * @return
	 */
	public List<Link> getEntryLinks() {
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
	
	public String getName() {
		return this.m_name;
	}
}
