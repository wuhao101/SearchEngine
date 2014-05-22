package edu.upenn.cis455.crawler.worker.robots;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class is a container for storing allow and disallow URLs in robots.txt
 * 
 * @author martinng
 *
 */
public class URLSet extends TreeSet<String> {
    private static final long serialVersionUID = 1L;
    
    /**
     * This function adds a new URL to container
     */
    @Override
    public boolean add(String url) {
            SortedSet<String> sub = headSet(url);
            if (!sub.isEmpty() && url.startsWith(sub.last())) {
                    /* 
                     * No need to add; prefix is already present
                     */
                    return false;
            }
            boolean retVal = super.add(url);
            sub = tailSet(url + "\0");
            while (!sub.isEmpty() && sub.first().startsWith(url)) {
                    /* 
                     * Remove redundant entries
                     */
                    sub.remove(sub.first());
            }
            return retVal;
    }
    
    /**
     * This function finds common prefix
     * 
     * @param url
     * @return
     */
	public boolean containsPrefixOf(String url) {
		SortedSet<String> sub = headSet(url);
		/*
		 * Because redundant prefixes have been eliminated, only a test against
		 * last item in headSet is necessary
		 */
		if (!sub.isEmpty() && url.startsWith(sub.last())) {
			return true; // prefix substring exists
		}
		return contains(url);
	}
}