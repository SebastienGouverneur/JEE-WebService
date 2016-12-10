package web;

import javax.servlet.http.HttpSession;

public class Utils {
	
	public static boolean isConnected(HttpSession session){
		return session.getAttribute("emailAddress") != null;
	}

}
