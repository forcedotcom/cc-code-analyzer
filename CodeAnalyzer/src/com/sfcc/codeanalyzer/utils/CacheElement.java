package com.sfcc.codeanalyzer.utils;
/**
 * Copyright (c) 2018, Salesforce.com, Inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license. 
 * For full license text, see LICENSE.txt file in the repo root
*/

public class CacheElement {

	public String status;
	public String type;
	public int hour;
	public int minute;
	public String varyby;
	public String template;
	public String includePath;
	
	/**
	 * Constructor of the CacheElement object
	 * @param status_ on/off is a deprecated option
	 * @param type_ "relative" or "daily"
	 * @param hour_ number of hours to cache the element must be lower than 24
	 * @param minute_ number of minute to cache the element
	 * @param varyby_ varyby clause of the iscache statement
	 * @param template_ cached template name
	 * @param includePath_ cached template path
	 */
	public CacheElement(String status_, String type_, int hour_, int minute_, String varyby_, String template_, String includePath_){
		status=status_;
		type=type_;
		hour=hour_;
		minute=minute_;
		varyby=varyby_;
		template=template_;
		includePath=includePath_;
	}
}
