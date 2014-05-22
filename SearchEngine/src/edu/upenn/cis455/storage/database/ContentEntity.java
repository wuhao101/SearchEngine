package edu.upenn.cis455.storage.database;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * This class is an Entity
 * 
 * @author martinng
 *
 */
@Entity
public class ContentEntity {
	@PrimaryKey
	private String m_contentSHA;
	private String m_url;
	private String m_address;
	
	/**
	 * This function sets content SHA1 value
	 * 
	 * @param contantSHA
	 */
	public void setContentSHA(String contentSHA) {
		this.m_contentSHA = contentSHA;
	}
	
	/**
	 * This function sets the corresponding URL 
	 * @param url
	 */
	public void setURL(String url) {
		this.m_url = url;
	}
	
	/**
	 * This function sets the IP address where this document locates
	 * 
	 * @param address
	 */
	public void setAddress(String address) {
		this.m_address = address;
	}
	
	/**
	 * This function returns the content SHA1 value
	 * 
	 * @return
	 */
	public String getContentSHA() {
		return this.m_contentSHA;
	}
	
	/**
	 * This function returns the URL of the content
	 * 
	 * @return
	 */
	public String getURL() {
		return this.m_url;
	}
	
	/**
	 * This function returns the location IP address
	 * 
	 * @return
	 */
	public String getAddress() {
		return this.m_address;
	}
}
