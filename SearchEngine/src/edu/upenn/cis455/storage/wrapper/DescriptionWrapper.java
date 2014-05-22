package edu.upenn.cis455.storage.wrapper;

public class DescriptionWrapper {
	private String m_docID;
	
	private String m_url;
	private String m_title;
	private String m_description;
	private String m_parentURL;
	private String m_type;
	
	public void setDocID(String docID) {
		this.m_docID = docID;
	}
	
	public void setURL(String url) {
		this.m_url = url;
	}
	
	public void setTitle(String title) {
		this.m_title = title;
	}
	
	public void setDescription(String description) {
		this.m_description = description;
	}
	
	public void setParentURL(String parentURL) {
		this.m_parentURL = parentURL;
	}
	
	public void setContentType(String type) {
		this.m_type = type;
	}
	
	public String getDocID() {
		return this.m_docID;
	}
	
	public String getURL() {
		return this.m_url;
	}
	
	public String getTitle() {
		return this.m_title;
	}
	
	public String getDescription() {
		return this.m_description;
	}
	
	public String getParentURL() {
		return this.m_parentURL;
	}
	
	public String getContentType() {
		return this.m_type;
	}
}
