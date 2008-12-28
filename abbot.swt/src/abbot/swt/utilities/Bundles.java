package abbot.swt.utilities;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;

/**
 * Convenience methods for looking up {@link Plugin}s, {@link Bundle}s, {@link ResourceBundle}s,
 * and {@link String}s within them.
 * 
 * @author Gary Johnston
 */

public class Bundles {

	/**
	 * @param bundle
	 * @param key
	 * @return the String value
	 * @exception NullPointerException
	 *                if <code>key</code> or <code>bundle</code> are <code>null</code>
	 * @exception MissingResourceException
	 *                if no object for the given key can be found or if the ResourceBundle itself
	 *                cannot be found
	 * @exception ClassCastException
	 *                if the object found for the given key is not a string
	 */
	public static String getString(Bundle bundle, String key) throws MissingResourceException {
		return getResourceBundle(bundle).getString(key);
	}

	/**
	 * @param plugin
	 * @param key
	 * @return the String value
	 * @exception NullPointerException
	 *                if <code>key</code> or <code>plugin</code> are <code>null</code>
	 * @exception MissingResourceException
	 *                if no object for the given key can be found or if the ResourceBundle itself
	 *                cannot be found
	 * @exception ClassCastException
	 *                if the object found for the given key is not a string
	 */
	public static String getString(Plugin plugin, String key) {
		return getResourceBundle(plugin).getString(key);
	}

	/**
	 * @param symbolicName
	 *            the plugin's symbolic name (plug-in ID)
	 * @param key
	 * @return the String value
	 * @exception NullPointerException
	 *                if <code>key</code> or <code>symbolicName</code> are <code>null</code>
	 * @exception MissingResourceException
	 *                if no object for the given key can be found or if the ResourceBundle itself
	 *                cannot be found
	 * @exception ClassCastException
	 *                if the object found for the given key is not a string
	 */
	public static String getString(String symbolicName, String key) {
		return getResourceBundle(symbolicName).getString(key);
	}

	/**
	 * @param bundle
	 * @return the ResourceBundle
	 * @exception NullPointerException
	 *                if <code>bundle</code> is <code>null</code>
	 * @exception MissingResourceException
	 *                if the resource bundle was not found
	 */
	public static ResourceBundle getResourceBundle(Bundle bundle) {
		return Platform.getResourceBundle(bundle);
	}

	/**
	 * @param plugin
	 * @return the ResourceBundle
	 * @exception NullPointerException
	 *                if <code>plugin</code> is <code>null</code>
	 * @exception MissingResourceException
	 *                if the resource bundle was not found
	 */
	public static ResourceBundle getResourceBundle(Plugin plugin) {
		return Platform.getResourceBundle(plugin.getBundle());
	}

	/**
	 * @param symbolicName
	 *            the plugin's symbolic name (plug-in ID)
	 * @return the ResourceBundle
	 * @exception NullPointerException
	 *                if <code>symbolicName</code> is <code>null</code>
	 * @exception MissingResourceException
	 *                if the resource bundle was not found
	 */
	public static ResourceBundle getResourceBundle(String symbolicName) {
		return Platform.getResourceBundle(getBundle(symbolicName));
	}

	/**
	 * @param symbolicName
	 *            the plug-in's symbolic name (plug-in ID)
	 * @return the {@link Bundle}, or <code>null</code> if it couldn't be found
	 */
	public static Bundle getBundle(String symbolicName) {
		return Platform.getBundle(symbolicName);
	}
}
