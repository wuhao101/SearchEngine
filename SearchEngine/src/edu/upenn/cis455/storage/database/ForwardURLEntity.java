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
public class ForwardURLEntity {
	/*
	 * Attributes
	 */
	@PrimaryKey
	private String m_url;
	
	public void setURL(String url) {
		this.m_url = url;
	}
	
	public String getURL() {
		return this.m_url;
	}
}
