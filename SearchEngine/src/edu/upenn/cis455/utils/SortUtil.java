package edu.upenn.cis455.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.upenn.cis455.crawler.message.NodeWrapper;

/**
 * This function is an utility related to sort operation
 * 
 * @author martinng
 *
 */
public class SortUtil {
	/**
	 * This function sorts nodes according to their hash keys
	 * 
	 * @param sortList
	 * @return
	 */
	public static List<NodeWrapper> sortNodes(List<NodeWrapper> sortList) {
		Collections.sort(sortList, new Comparator<NodeWrapper>() {
			public int compare(NodeWrapper o1, NodeWrapper o2) {
				String key1 = SHA1Util.byteToString(SHA1Util.generateSHA(o1.getName()));
				String key2 = SHA1Util.byteToString(SHA1Util.generateSHA(o2.getName()));
				return (key1.compareTo(key2));
			}
		});

		return sortList;
	}
}
