package dao.impl;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class is used to access to the external strings located in the messages.properties file.
 * @authors Sébastien Gouverneur & Gabriel Ladet
 */

public class Resources {
	private static String BUNDLE_NAME = "dao.impl.messages";

	private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	public Resources() {
	}

	/**
	 * This method is used to access to the string value associated to the external key in the messages.properties file
	 * @param key
	 * @return the associated value
	 */
	
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
