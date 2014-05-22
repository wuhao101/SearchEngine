package edu.upenn.cis455.storage.database;

import java.util.List;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class HtmlEntity {
	/*
	 * Attributes
	 */
	@PrimaryKey
	private String m_docID;

	private String m_url;
	
	private int m_size;
	private String m_type;
	private byte[] m_content;
	private double m_pageRank;
	private long m_crawledTime;
	private String m_charset;
	private int m_hits = 0;
	private List<String> m_locations = null;

	/*
	 * Get methods
	 */
	/**
	 * This function returns the time when this page is crawled
	 * 
	 * @return
	 */
	public long getCrawledTime() {
		return this.m_crawledTime;
	}
	
	/**
	 * This function returns the time of hits of the content
	 * 
	 * @return
	 */
	public int getHits() {
		return this.m_hits;
	}
	
	/**
	 * This function returns the locations of server
	 * 
	 * @return
	 */
	public List<String> getLocations() {
		return this.m_locations;
	}
	/**
	 * This function returns the document ID
	 * 
	 * @return
	 */
	public String getDocId() {
		return this.m_docID;
	}
	
	/**
	 * This function returns the URL
	 * 
	 * @return
	 */
	public String getURL() {
		return this.m_url;
	}
	
	/**
	 * This function returns the size of the content
	 * 
	 * @return
	 */
	public int getSize() {
		return this.m_size;
	}
	
	/**
	 * This function returns the page content
	 * 
	 * @return
	 */
	public byte[] getContent() {
		return this.m_content;
	}
	
	/**
	 * This function returns the page rank value
	 * 
	 * @return
	 */
	public double getPageRank() {
	    return this.m_pageRank;
	}
	
	/**
	 * This function returns the content type
	 * 
	 * @return
	 */
	public String getType() {
		return this.m_type;
	}

	/*
	 * Set methods
	 */
	/**
	 * This function sets locations of server
	 * 
	 * @param locations
	 */
	public void setLocations(List<String> locations) {
		this.m_locations = locations;
	}
	
	/**
	 * This function sets the time when this page is crawled
	 * 
	 * @param crawledTime
	 */
	public void setCrawledTime(long crawledTime) {
		this.m_crawledTime = crawledTime;
	}
	
	/**
	 * This function adds one time of hits
	 */
	public void addHits() {
		this.m_hits ++;
	}
	/**
	 * This function sets the document ID
	 * 
	 * @param docID
	 */
	void setDocId(String docID) {
		this.m_docID = docID;
	}
	
	/**
	 * This function sets the URL
	 * 
	 * @param url
	 */
	void setURL(String url) {
		this.m_url = url;
	}
	
	/**
	 * This function sets the size of the content
	 * 
	 * @param size
	 */
	public void setSize(int size) {
		this.m_size = size;
	}
	
	/**
	 * This function sets the page content 
	 * @param content
	 */
	void setContent(byte[] content) {
		this.m_content = content;
	}
	
	/**
	 * This function sets the page rank value
	 * 
	 * @param pageRank
	 */
	public void setPageRank(double pageRank) {
	    this.m_pageRank = pageRank;
	}
	
	/**
	 * This function sets content type
	 * 
	 * @param type
	 */
	public void setType(String type) {
	    this.m_type = type;
	}
	
	public String getCharset() {
		return this.m_charset;
	}
	
	public void setCharset(String charset) {
	    this.m_charset = charset;
	}
}
