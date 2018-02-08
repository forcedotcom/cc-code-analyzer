package com.sfcc.codeanalyzer.utils;
/**
 * Copyright (c) 2018, Salesforce.com, Inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license. 
 * For full license text, see LICENSE.txt file in the repo root
*/

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

/**
 * The Settings class controls plug-in options
 * @author SFCC support
 */
public class Settings {

	public String cartridgePath;
	public boolean checkCacheList;
	
	/**
	 * Save the plug-in context
	 */
	public void savePluginSettings() {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("com.sfcc.codeanalyzer");
		prefs.put("CartridgePath", this.cartridgePath);
		prefs.putBoolean("ShowCacheList", this.checkCacheList);
		try {
		    // prefs are automatically flushed during a plugin's "super.stop()".
			prefs.flush();
		} catch(BackingStoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * fetch again (restore) the plug-in context
	 */
	public void loadPluginSettings() {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("com.sfcc.codeanalyzer");	
		this.cartridgePath = prefs.get("CartridgePath", "");
		this.checkCacheList = prefs.getBoolean("ShowCacheList", false);
	}
}
