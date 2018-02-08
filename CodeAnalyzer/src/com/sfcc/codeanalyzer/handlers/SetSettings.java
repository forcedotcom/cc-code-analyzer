package com.sfcc.codeanalyzer.handlers;
/**
 * Copyright (c) 2018, Salesforce.com, Inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license. 
 * For full license text, see LICENSE.txt file in the repo root
*/

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;

import com.sfcc.codeanalyzer.utils.Settings;
import com.sfcc.codeanalyzer.utils.SettingsDialog;

import org.eclipse.swt.widgets.*;

/**
 * The SetSettings class controls the plug-in Dialog box interaction
 * @author SFCC support
 */
public class SetSettings extends AbstractHandler {
		
	/**
	 * Find the current active console or instantiate a new one if none does exist
	 * @param name eclipse console identifier
	 * @return MessageConsole the console object
	 */
	private MessageConsole findConsole(String name) {
	      ConsolePlugin plugin = ConsolePlugin.getDefault();
	      IConsoleManager conMan = plugin.getConsoleManager();
	      IConsole[] existing = conMan.getConsoles();
	      for (int i = 0; i < existing.length; i++)
	         if (name.equals(existing[i].getName()))
	            return (MessageConsole) existing[i];
	      MessageConsole myConsole = new MessageConsole(name, null);
	      conMan.addConsoles(new IConsole[]{myConsole});
	      return myConsole;
	   }
	
	/**
	 * Execute method of the SetSettings class, creating the UI objects (dialog box and console) 
	 * @param event ExecutionEvent object to trigger dialogs box creation
	 * @return null Object
	 * @throws ExecutionException raised if any issue during the execution
	 */	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		SettingsDialog dialog = new SettingsDialog(window.getShell());
		Settings settings = new Settings();
		settings.loadPluginSettings();
		dialog.create();
		dialog.setCartridgePath(settings.cartridgePath);
		dialog.setCheckCacheList(settings.checkCacheList);
		if (dialog.open() == Window.OK) {
			settings.cartridgePath = dialog.getCartridgePath();
			settings.checkCacheList = dialog.getCheckCacheList();
			settings.savePluginSettings();
			MessageConsole console = findConsole("analyzer");
			MessageConsoleStream	out = console.newMessageStream();
		} 
		return null;
	}
}
