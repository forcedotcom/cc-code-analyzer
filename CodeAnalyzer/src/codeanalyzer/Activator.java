package codeanalyzer;
/**
 * Copyright (c) 2018, Salesforce.com, Inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license. 
 * For full license text, see LICENSE.txt file in the repo root
*/

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * @author SFCC support
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.sfcc.codeanalyzer";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor of the Activator, building the Activator object
	 */
	public Activator() {}

	/**
	 * Starting the plugin and its AbstractUIPlugin parent
	 * @param context BundleContext object
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * Stopping the plugin and its AbstractUIPlugin parent
	 * @param context BundleContext object
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Get the shared Activator instance
	 * @return Activator, the plugin object
	 */
	public static Activator getDefault() {
		return plugin;
	}
}
