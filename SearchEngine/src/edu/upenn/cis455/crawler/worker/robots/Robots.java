package edu.upenn.cis455.crawler.worker.robots;

public class Robots {
	private static final long EXPIRATION_DELAY = 24 * 60 * 1000L;

	private URLSet m_disallows = new URLSet();
	private URLSet m_allows = new URLSet();
	private long m_delay = 2;

	private long m_timeFetched;
	private long m_timeLastAccessed;
	
	/**
	 * Constructor: get current time
	 */
	public Robots() {
		this.m_timeFetched = System.currentTimeMillis();
	}

	/**
	 * Checks whether this robots expires
	 * 
	 * @return
	 */
	public boolean needsRefetch() {
		return (System.currentTimeMillis() - this.m_timeFetched > EXPIRATION_DELAY);
	}

	/**
	 * Checks whether the given path is allowed
	 * 
	 * @param path
	 * @return
	 */
	public boolean allows(String path) {
		this.m_timeLastAccessed = System.currentTimeMillis();
		if (this.m_disallows.containsPrefixOf(path)) {
			return false;
		}
		return true;
	}
	
	/*
	 * Set methods
	 */
	/**
	 * This function sets crawl-delay
	 * @param delay
	 */
	public void setDelay(long delay) {
		this.m_delay = delay;
	}
	
	/**
	 * This function adds a new URL disallowed
	 * 
	 * @param path
	 */
	public void addDisallow(String url) {
		this.m_disallows.add(url);
	}
	
	/**
	 * This function adds a new URL allowed
	 * 
	 * @param path
	 */
	public void addAllow(String url) {
		this.m_allows.add(url);
	}
	
	/*
	 * Get methods
	 */
	
	/**
	 * This function returns all URLs disallowed
	 * 
	 * @return
	 */
	public URLSet getDisallows() {
		return this.m_disallows;
	}
	
	/**
	 * This function returns all URLs allowed
	 * 
	 * @return
	 */
	public URLSet getAllows() {
		return this.m_allows;
	}
	
	/**
	 * This function returns crawl-delay
	 * @return
	 */
	public long getDelay() {
		return this.m_delay;
	}
	
	public long getLastAccessTime() {
		return this.m_timeLastAccessed;
	}
}
