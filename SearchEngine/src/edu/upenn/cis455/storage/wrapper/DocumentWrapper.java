package edu.upenn.cis455.storage.wrapper;

import edu.upenn.cis455.crawler.message.Link;

/**
 * This class is a wrapper for document crawled
 * 
 * @author martinng
 * 
 */
public class DocumentWrapper {
	/*
	 * Properties
	 */
	private byte[] m_content = null;
	private Link m_url = null;
	private int m_size = -1;
	private String m_type = null;
	private long m_lastModifiedTime = -1;
	private String m_charset;

	/**
	 * Default constructor
	 */
	public DocumentWrapper() {
		this.m_content = null;
		this.m_url = null;
		this.m_size = -1;
		this.m_type = null;
		this.m_lastModifiedTime = System.currentTimeMillis();
	}

	/**
	 * Constructor: assign values to properties
	 * 
	 * @param content
	 * @param url
	 * @param size
	 * @param type
	 * @param charset
	 */
	public DocumentWrapper(byte[] content, Link url, int size, String type, String charset) {
		this.m_content = content;
		this.m_url = url;
		this.m_size = size;
		this.m_type = type;
		this.m_lastModifiedTime = System.currentTimeMillis();
		this.m_charset = charset;
	}

	/*
	 * Set methods
	 */
	/**
	 * This function assigns value to content
	 * 
	 * @param content
	 */
	public void setContent(byte[] content) {
		this.m_content = content;
	}

	/**
	 * This function assigns value to url
	 * 
	 * @param url
	 */
	public void setURL(Link url) {
		this.m_url = url;
	}

	/**
	 * This function assigns value to size
	 * 
	 * @param size
	 */
	public void setSize(int size) {
		this.m_size = size;
	}

	/**
	 * This function assigns value to type
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.m_type = type;
	}

	/*
	 * Get methods
	 */
	/**
	 * This function returns the content
	 * 
	 * @return
	 */
	public byte[] getContent() {
		return this.m_content;
	}

	/**
	 * This function returns the URL
	 * 
	 * @return
	 */
	public Link getURL() {
		return this.m_url;
	}

	/**
	 * This function returns the size
	 * 
	 * @return
	 */
	public int getSize() {
		return this.m_size;
	}

	/**
	 * This function returns the type
	 * 
	 * @return
	 */
	public String getType() {
		return this.m_type;
	}
	
	/**
	 * This function returns the last-modified-time
	 * 
	 * @return
	 */
	public long getLastModifiedTime() {
		return this.m_lastModifiedTime;
	}
	
	public String getCharset() {
		return this.m_charset;
	}
	
	public void setCharset(String charset) {
	    this.m_charset = charset;
	}
}
