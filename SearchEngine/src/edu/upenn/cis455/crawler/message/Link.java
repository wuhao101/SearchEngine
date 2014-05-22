package edu.upenn.cis455.crawler.message;

import java.io.Serializable;

/**
 * This class is a container for forward link
 * 
 * @author martinng
 *
 */
public class Link implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3942081272981965052L;
	/*
	 * Properties
	 */
	private String m_url;
	private String m_parentURL;
	private String m_parentTitle;
	private String m_title;
	private String m_alt;
	private String m_text;
	
	/*
	 * Set methods
	 */
	public void setURL(String url) {
		this.m_url = url;
	}
	
	public void setParURL(String parURL) {
		this.m_parentURL = parURL;
	}
	
	public void setParTitle(String parTitle) {
		this.m_parentTitle = parTitle;
	}
	
	public void setTitle(String title) {
		this.m_title = title;
	}
	
	public void setAlt(String alt) {
		this.m_alt = alt;
	}
	
	public void setText(String text) {
		this.m_text = text;
	}
	
	/*
	 * Get methods
	 */
	public String getURL() {
		return this.m_url;
	}
	
	public String getParURL() {
		return this.m_parentURL;
	}
	
	public String getParTitle() {
		return this.m_parentTitle;
	}
	
	public String getAlt() {
		return this.m_alt;
	}
	
	public String getText() {
		return this.m_text;
	}
	
	public String getTitle() {
		return this.m_title;
	}
}
