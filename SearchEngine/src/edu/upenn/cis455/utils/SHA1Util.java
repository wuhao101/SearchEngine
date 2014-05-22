package edu.upenn.cis455.utils;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * This class is an utility for SHA1
 * 
 * @author martinng
 *
 */
public class SHA1Util {
	private static String MAX_SHA1 = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
	/**
	 * This function calculates the SHA1 value of a given string
	 * 
	 * @param content
	 * @return
	 */
	public static byte[] generateSHA(String content) {
		byte[] result = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(content.getBytes());
	        result = md.digest();
			md.reset();
		} catch (Exception e) {
//			Logger.error("generateSHA: " + e.getMessage());
		}
		return result;
	}
	
	/**
	 * This function transfer byte array format to String format for SHA1
	 * 
	 * @param result
	 * @return
	 */
	public static String byteToString(byte[] result) {
		String contentHashValue = new String();
		for (int i = 0; i < result.length; i++) {
        	contentHashValue += Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1);
        }
		return contentHashValue;
	}
	
	/**
	 * This function hashes the SHA1 value to a worker
	 * 
	 * @param sha1Value
	 * @param numOfWorkers
	 * @return
	 */
	public static int hashToWorker(String sha1Value, int numOfWorkers) {
		int workerID = -1;
		try {
			BigInteger maxSHA1 = new BigInteger(SHA1Util.MAX_SHA1, 16);
			BigInteger currentSHA1 = new BigInteger(sha1Value, 16);
			BigInteger workerNum = new BigInteger(String.valueOf(numOfWorkers),
					10);
			BigInteger arrangePerWorker = maxSHA1.divide(workerNum);
			int mod = maxSHA1.mod(workerNum).intValue();
			for (int i = 1; i <= numOfWorkers; i++) {
				BigInteger floorIndex = new BigInteger(String.valueOf(i - 1),
						10);
				BigInteger roofIndex = new BigInteger(String.valueOf(i), 10);
				BigInteger floor = arrangePerWorker.multiply(floorIndex).add(
						new BigInteger(new String("1"), 10));
				BigInteger roof = arrangePerWorker.multiply(roofIndex);
				if (mod > 0 && mod >= (i - 1)) {
					if (floorIndex.compareTo(new BigInteger(new String("0"), 10)) == 0) {
						floor.subtract(new BigInteger(new String("1"), 10));
					} else {
						floor.add(new BigInteger(new String("1"), 10));
					}
				}
				if (mod > 0 && mod >= i) {
					roof.add(new BigInteger(new String("1"), 10));
				}
				if (currentSHA1.compareTo(floor) >= 0
						&& currentSHA1.compareTo(roof) <= 0) {
					workerID = i;
					break;
				}
			}
		} catch (Exception e) {
//			Logger.error("hashToWorker: " + e.getMessage());
		}
		return workerID;
	}
}
