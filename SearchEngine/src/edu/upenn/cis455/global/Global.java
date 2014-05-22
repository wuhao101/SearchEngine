package edu.upenn.cis455.global;

import java.util.HashSet;
import java.util.Set;

/**
 * This class contains all global variables
 * 
 * @author martinng
 * 
 */
public class Global {
	// User-agent for HTTP client
	public static final String USER_AGENT = "cis455crawler";

	public static final String CRLF = "\r\n";

	public static final String HTML_TYPE = "text/html";

	public static final String SPLASH = "/";

	public static final String DOUBLE_SPLASH = "://";

	public static final String COLON = ":";

	public static final String EQUAL_SIGN = "=";

	public static final String CHARSET_REGX = "(?i);(\\s)*charset(\\s)*=(\\s)*(.)+\"";
	
	public static final String XML_ENCODE_REGX = "(?i)<\\?xml(\\s)+version(\\s)*=(\\s)*\"\\d.\\d\"(\\s)+encoding(\\s)*=(\\s)*\"[^\"]+\"(\\s)*\\?>";
	
	public static final String XML_ENCODE_VALUE_REGX = "encoding(\\s)*=(\\s)*\"[^\"]+\"";
	
	public static final Set<String> ALLOWED_TYPE = new HashSet<String>();
	static {
		ALLOWED_TYPE.add("text/plain");
		ALLOWED_TYPE.add("text/html");
		ALLOWED_TYPE.add("text/xml");
		ALLOWED_TYPE.add("application/xml");
		ALLOWED_TYPE.add("image/gif");
		ALLOWED_TYPE.add("image/jpeg");
		ALLOWED_TYPE.add("image/png");
		ALLOWED_TYPE.add("application/msword");
		ALLOWED_TYPE.add("application/pdf");
		ALLOWED_TYPE.add("application/vnd.ms-powerpoint");
		ALLOWED_TYPE.add("application/x-ppt");
	}
}
