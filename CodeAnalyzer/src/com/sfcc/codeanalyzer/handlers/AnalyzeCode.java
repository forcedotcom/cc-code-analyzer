package com.sfcc.codeanalyzer.handlers;
/**
 * Copyright (c) 2018, Salesforce.com, Inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license. 
 * For full license text, see LICENSE.txt file in the repo root
*/

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sfcc.codeanalyzer.utils.CacheElement;
import com.sfcc.codeanalyzer.utils.Settings;
import com.sfcc.codeanalyzer.views.CacheView;

/**
 * The main wrapping class of the code analyzer 
 * @author SFCC support
 */
public class AnalyzeCode extends AbstractHandler {
	
	boolean DEBUG = false;
	MessageConsoleStream out;
	
	int missingTemplates;
	String cartridgePath;
	IProject[] projects;
	HashMap<String,String> modules;
	
	/**
	 * The constructor of the AnalyzeCode class.
	 */
	public AnalyzeCode() {}

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
	         if (name.equals(existing[i].getName())) {
	            conMan.showConsoleView( (MessageConsole) existing[i] );
	            return (MessageConsole) existing[i];
	         }
	      MessageConsole myConsole = new MessageConsole(name, null);
	      conMan.addConsoles(new IConsole[]{myConsole});
	      conMan.showConsoleView( myConsole );
	      return myConsole;
	   }
	
	/**
	 * Execute method of the AnalyzeCode class, extracting the needed information from the application context
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
	
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		modules = new HashMap<String,String>();
		
		//MessageDialog.openInformation(window.getShell(),"CodeAnalyzer","Hello, Eclipse world");
		Settings settings = new Settings();
		settings.loadPluginSettings();
		cartridgePath = settings.cartridgePath;
		
		MessageConsole console = findConsole("analyzer");
		
		out = console.newMessageStream();
		out.println("Active Cartridge Path:" + cartridgePath);
		
		// set selection service
		ISelectionService service = window.getSelectionService();
		// set structured selection
		IStructuredSelection structured = (IStructuredSelection) service.getSelection();
	 
		//check if it is an IFile
		if (structured.getFirstElement() instanceof IFile) {
			// get the selected file
			IFile file = (IFile) structured.getFirstElement();
			IPath path = file.getLocation();
			Vector<CacheElement> caches = new Vector<CacheElement>();
			missingTemplates = 0;
			HashMap<String,String> includes = new HashMap<String,String>(); 
			scanModules(new Path(path.toPortableString()), new HashMap<String, String>());
			dive(new Path(path.toPortableString()), caches, includes, path.toPortableString());
			/*
			try {
				CacheView cacheView = (CacheView)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView("com.sfcc.codeanalyzer.views.CacheView");
				cacheView.test();
			} catch (PartInitException e) {
				// TODO Handle errors better
				e.printStackTrace();
			}
			*/
			if (DEBUG) out.println("Scanned: " + path.toPortableString());
			out.println("Templates scanned: " + (includes.size()+1));
			out.println("iscache statements found: " + caches.size());
			out.println("Modules found: " + modules.size());
			out.println("Missing templates detected: " + missingTemplates);
			if (caches.size()>0){
				CacheElement worst;// = compare(caches);
				Collections.sort(caches, new CacheComparator());
				worst=caches.lastElement();
				
				if (worst != null){
					if (settings.checkCacheList) {
						out.println("=====================");
						out.println("All iscache statements found in descending order");
						out.println("=====================");
						for (int i=0;i<caches.size();i++){
					
							CacheElement cacheElement = caches.get(i);
							out.println("Template:" + cacheElement.template);
							out.println("  Status: " + cacheElement.status);
							out.println("    Type: " + cacheElement.type);
							out.println("    Hour: " + cacheElement.hour);
							out.println("  Minute: " + cacheElement.minute);
							out.println("  Varyby: " + cacheElement.varyby);
							out.println("Included: " + cacheElement.includePath);
							out.println("=====================");
						}
					}
					out.println("*************************************************************************************");
					out.println("Worst iscache statement found in template:" + worst.template);
					out.println("  Status: " + worst.status);
					out.println("    Type: " + worst.type);
					out.println("    Hour: " + worst.hour);
					out.println("  Minute: " + worst.minute);
					out.println("  Varyby: " + worst.varyby);
					out.println("Included: " + worst.includePath);
				}
			} else {
				out.println("Found no iscache statements!");
			}
			out.println("");
		}
		return null;
	}
	
	/**
	 * Recursively search through the files for modules
	 * @param path relative path of the module file
	 * @param includes mapping of remote and local includes
	 */
	private void scanModules(Path path,  HashMap<String,String> includes){
		File input = path.toFile();
		
		Document doc;
		try {
			doc = Jsoup.parse(input, "UTF-8", "");
		} catch (Exception e){
			if (DEBUG) out.println("Could not find template:" + path.toPortableString());
			if (DEBUG) out.println(e.getLocalizedMessage());
			return;
		}
		
		Elements links = doc.select("isinclude");
		Elements decorators = doc.select("isdecorate");
		Elements ismodules = doc.select("ismodule");
		
		int segment = 0;
		
		for (int i=0;i < path.segmentCount();i++) {
			if (path.segment(i).equals("templates")) segment = i;
			if (path.segment(i).equals("default") && segment>0) segment = i;			
		}
		
		// Get the root path for our cartridge
		Path root = new Path(path.uptoSegment(segment+1).toPortableString());
		
		// Store module names + templates into a hashmap if not already found a previous declaration
		for (Element element : ismodules) {
			String name, template;
			name = element.attr("name");
			if (!element.attr("template").equals("") && !modules.containsKey("is"+name)){
				template = element.attr("template");
				modules.put("is"+name, template);
				out.println("Adding module:" + name + " Template:" + template);
			}
		}
		
		for (Element element : links) {
			if (!element.attr("template").equals("")) {
				String templateName = element.attr("template");
				if (!element.attr("template").endsWith(".isml"))
					templateName = templateName + ".isml";
				if (DEBUG) out.println(root.toPortableString() + "/" + templateName);
				String foundPath = findTemplate(root.toPortableString(), templateName);
				
				Path filetocheck = new Path(foundPath);
				if (!includes.containsKey(filetocheck.toPortableString())) {
					includes.put(filetocheck.toPortableString(), filetocheck.lastSegment());
					scanModules(filetocheck, includes);			
				} else if (DEBUG) out.println(filetocheck.toPortableString() + " ALREADY SCANNED");
			}
			
		}
		
		for (Element element : decorators) {
			if (!element.attr("template").equals("")) {
				String templateName = element.attr("template");
				if (!element.attr("template").endsWith(".isml"))
					templateName = templateName + ".isml";
				if (DEBUG) out.println(root.toPortableString() + "/" + templateName);
				String foundPath = findTemplate(root.toPortableString(), templateName);
				Path filetocheck = new Path(foundPath);
				if (!includes.containsKey(filetocheck.toPortableString())) {
					includes.put(filetocheck.toPortableString(), filetocheck.lastSegment());
					scanModules(filetocheck, includes);					
				} else if (DEBUG) out.println(filetocheck.toPortableString() + " ALREADY SCANNED");
			}
			
		}
		
	}
	
	/**
	 * Find the given template within the code base
	 * @param root string relative cartridge path
	 * @param path string relative template path
	 * @return
	 */
	private String findTemplate(String root, String path){
		if (cartridgePath.length() == 0)
			return root + "/" + path;
		String[] cartridges = cartridgePath.split(":");
		for (int i=0;i<cartridges.length;i++){
			IProject prj = ResourcesPlugin.getWorkspace().getRoot().getProject(cartridges[i]);
			if (prj.exists()){
				IPath prjPath = prj.getLocation();
				String templatePath = prjPath.toPortableString() + "/cartridge/templates/default/" + path;
				File test = new File(templatePath);
				if (test.exists()){
					if (DEBUG) out.println("File found. breaking");
					return templatePath;
				}
				if (DEBUG) out.println("PATH:" + templatePath);
			
			} else {
				if (DEBUG) out.println("Cartridge defined as " + cartridges[i] + " does not exist!");
			}
		}
		return path;
	}
	
	/**
	 * Recursive method to find the worst CacheElement
	 * @param path path of the template
	 * @param caches Vector of CacheElement
	 * @param includes HashMap of String,String
	 * @param includePath String
	 */
	public void dive(Path path, Vector<CacheElement> caches, HashMap<String,String> includes, String includePath){
		
		if (DEBUG) out.println("Scanning:" + path.toPortableString());
		File input = path.toFile();
		
		Document doc;
		try{
			doc = Jsoup.parse(input, "UTF-8", "");
		} catch (Exception e){
			out.println("Could not find template:" + path.toPortableString() + " Include path:" + includePath);
			missingTemplates++;
			if (DEBUG) out.println(e.getLocalizedMessage());
			return;
		}
		
		Elements links = doc.select("isinclude");
		Elements decorators = doc.select("isdecorate");
		Elements iscaches = doc.select("iscache");
		
		if (DEBUG) out.println("iscaches found:" + iscaches.size());
		if (DEBUG) out.println("isincludes found:" + links.size());
		int segment = 0;
		
		for (int i=0;i < path.segmentCount();i++){
			if (path.segment(i).equals("templates")) segment = i;
			if (path.segment(i).equals("default") && segment>0) segment = i;
				
		}
		
		// Get the root path for our cartridge
		Path root = new Path(path.uptoSegment(segment+1).toPortableString());
		
		// looping through iscaches
		for (Element element : iscaches) {
			int hour, minute;
			if (DEBUG) out.println(element.attr("status") + " " + element.attr("type") + " " + element.attr("hour") + " " + element.attr("minute") + " " + element.attr("varyby"));
		    if (element.attr("hour").equals("")) hour = 0;
		    else hour = new Integer(element.attr("hour"));
		    if (element.attr("minute").equals("")) minute = 0;
		    else minute = new Integer(element.attr("minute"));	    
		    CacheElement cacheElement = new CacheElement(element.attr("status"),element.attr("type"), hour, minute, element.attr("varyby"), path.toPortableString(), includePath);
		    caches.add(cacheElement);
		}
		
		// looping through links
		for (Element element : links) {
			if (!element.attr("template").equals("")) {
				String templateName = element.attr("template");
				if (!element.attr("template").endsWith(".isml"))
					templateName = templateName + ".isml";
				if (DEBUG) out.println(root.toPortableString() + "/" + templateName);
				String foundPath = findTemplate(root.toPortableString(), templateName);
				Path filetocheck = new Path(foundPath);
				if (!includes.containsKey(filetocheck.toPortableString())) {
					includes.put(filetocheck.toPortableString(), filetocheck.lastSegment());
					dive(filetocheck, caches, includes, includePath + "->" + templateName);
				} else if (DEBUG) out.println(filetocheck.toPortableString() + " ALREADY SCANNED");
			}
		}
		
		// looping through decorators
		for (Element element : decorators) {
			if (!element.attr("template").equals("")) {
				String templateName = element.attr("template");
				if (!element.attr("template").endsWith(".isml"))
					templateName = templateName + ".isml";
				if (DEBUG) out.println(root.toPortableString() + "/" + templateName);
				String foundPath = findTemplate(root.toPortableString(), templateName);
				Path filetocheck = new Path(foundPath);
				if (!includes.containsKey(filetocheck.toPortableString())) {
					includes.put(filetocheck.toPortableString(), filetocheck.lastSegment());
					dive(filetocheck, caches, includes, includePath + "->" + templateName);
				} else if (DEBUG) out.println(filetocheck.toPortableString() + " ALREADY SCANNED");
			}
		}

		// Scan for moduled templates
		Iterator<Entry<String, String>> it = modules.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry<String, String> entry = (Map.Entry<String,String>)it.next();
			if (DEBUG) out.println("Checking:" + entry.getKey());
			Elements modules = doc.select(entry.getKey());
			// Keep it a loop just in case we check the parameters too
			for (@SuppressWarnings("unused") Element element : modules){
				String templateName = entry.getValue();
				if (!templateName.endsWith(".isml"))
					templateName+= ".isml";
				String foundPath = findTemplate(root.toPortableString(), templateName);
				Path filetocheck = new Path(foundPath);
				if (!includes.containsKey(filetocheck.toPortableString())) {
					includes.put(filetocheck.toPortableString(), filetocheck.lastSegment());
					dive(filetocheck, caches, includes, includePath + "->" + templateName);
				} else if (DEBUG) out.println(filetocheck.toPortableString() + " ALREADY SCANNED");
				break;
			}
		}
		
		if (DEBUG) out.println("Returning from dive");
	}
	
	/**
	 * Loop through all CacheElement of the Vector caches to find the worst CacheElement object
	 * @param caches Vector of CacheElement
	 * @return worst CacheElement
	 */
	public CacheElement compare(Vector<CacheElement> caches){
		CacheElement worst = caches.firstElement();
		for (int i=0;i<caches.size();i++){
			CacheElement tocompare = caches.get(i);
			if (tocompare.status.equals("off")) worst=tocompare;
			else if (worst.type.equals(tocompare.type)) {
				// We got the same type so they can be compared
				if (worst.hour > tocompare.hour) worst=tocompare;
				else if (worst.hour == tocompare.hour && worst.minute > tocompare.minute) worst=tocompare;
				else if (worst.hour == tocompare.hour && worst.minute == tocompare.minute && worst.varyby.equals("") && !tocompare.varyby.equals(""))
					worst=tocompare;
			}
		}
		return worst;
	}
	
	/**
	 * CacheComparator overriding the default comparison method for the CacheElement typ
	 */
	class CacheComparator implements Comparator<CacheElement> {
	    @Override
	    public int compare(CacheElement c1, CacheElement c2){	        
	        if (c2.status.equals("off")) return -1;
	        else if (c1.type.equals(c2.type)) {
	            // We got the same type so they can be compared
	            if (c1.hour > c2.hour) return -1;
	            else if (c1.hour == c2.hour && c1.minute > c2.minute) return -1;
	            else if (c1.hour == c2.hour && c1.minute == c2.minute && c1.varyby.equals("") && !c2.varyby.equals(""))
	                return -1;
	            
	            return 1;
	        }
	        return 0;
	    }
	}
}
