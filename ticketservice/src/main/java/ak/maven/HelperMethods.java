package ak.maven;

import java.util.Locale;
import java.util.Random;

public class HelperMethods {
	
	/**
	 * Checks if the option provided by the user (as string) is a positive integer
	 * @param s the string representation of the user selection
	 * @return true if positive integer, false otherwise
	 */
	public static boolean isPositiveInteger(String s) {
		boolean isValidInteger = false;
		try {
			int i = Integer.parseInt(s);
	         if(i > 0) {
	        	 isValidInteger = true;
	         }
	      }
		
		catch (NumberFormatException ex) {
			//Don't care about the exception, only the fact that 'False' will be returned
	      }
		
		return isValidInteger;
	}
	
	/**
	 * Generates an 6-digit random positive integer to be used as an ID for SeatHold  
	 * @return a 6-digit random positive integer
	 */
	public static int generateID() {
		Random rnd = new Random();
		int n = 100000 + rnd.nextInt(900000);
		return n;
	}
	
	/**
	 * Generates a random positive integer of requested length  
	 * @return a random positive integer
	 */
	public static int generateID(int length) {
		
		if (length < 1) throw new IllegalArgumentException();
		
		Random rnd = new Random();
		return (int) (Math.pow(10, length-1) + rnd.nextInt((int) (9 * Math.pow(10, length-1))));		
	}
	
	/**
	 * Generates a random string of requested length
	 * Uses a combination of uppercase and lowercase letters, and digits
	 * @param length length of random string desired 
	 */
	public static String generateRandomString(int length) {
		
		if (length < 1) throw new IllegalArgumentException();
		
		 String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		 String lower = upper.toLowerCase(Locale.ROOT);
		 String digits = "0123456789";
		 char[] symbols = (upper + lower + digits).toCharArray();
		 Random random = new Random();
		 char[] bufs = new char[length];
		 
		 for (int idx = 0; idx < bufs.length; ++idx) {
			 bufs[idx] = symbols[random.nextInt(symbols.length)];
			 }
		 
		 return new String(bufs);
	}
}
