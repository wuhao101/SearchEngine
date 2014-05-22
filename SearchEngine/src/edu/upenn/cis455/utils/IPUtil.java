package edu.upenn.cis455.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * This class is an utility related to IP
 * 
 * @author martinng
 * 
 */
public class IPUtil {
	/**
	 * This function gets local IP address
	 * 
	 * @return
	 */
	public static String getLocalIP() {
		try {
			String localip = null;
			String netip = null;
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface
					.getNetworkInterfaces();
			InetAddress ip = null;
			boolean finded = false;
			while (netInterfaces.hasMoreElements() && !finded) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					ip = address.nextElement();
					if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
							&& ip.getHostAddress().indexOf(":") == -1) {
						netip = ip.getHostAddress();
						finded = true;
						break;
					} else if (ip.isSiteLocalAddress()
							&& !ip.isLoopbackAddress()
							&& ip.getHostAddress().indexOf(":") == -1) {
						localip = ip.getHostAddress();
					}
				}
			}
			if (netip != null && !"".equals(netip)) {
				return netip;
			} else {
				return localip;
			}
		} catch (SocketException e) {
//			Logger.error("getLocalIP: " + e.getMessage());
			return null;
		}
	}
}
