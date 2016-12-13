package web;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.servlet.http.HttpSession;

public class Utils {
	
		static final String alphaNum = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		static SecureRandom random = new SecureRandom();
		
		public static boolean isConnected(HttpSession session){
			return session.getAttribute("Person") != null;
		}
		
		public static String get_SHA_512_SecurePassword(String passwordToHash, String   salt) throws UnsupportedEncodingException{
			String generatedPassword = null;
			    try {
			         MessageDigest md = MessageDigest.getInstance("SHA-512");
			         md.update(salt.getBytes("UTF-8"));
			         byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
			         StringBuilder sb = new StringBuilder();
			         for(int i=0; i< bytes.length ;i++){
			            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			         }
			         generatedPassword = sb.toString();
			        } 
			       catch (NoSuchAlgorithmException e){
			        e.printStackTrace();
			       }
			    return generatedPassword;
			}
		
		public static String randomSalt(int len){
			   StringBuilder sb = new StringBuilder(len);
			   for(int i = 0; i < len; i++) 
			      sb.append(alphaNum.charAt(random.nextInt(alphaNum.length())));
			   return sb.toString();
			}

}
