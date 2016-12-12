package dao.impl;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Resources {
	private static String BUNDLE_NAME = "dao.impl.messages";

	private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	public Resources() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
