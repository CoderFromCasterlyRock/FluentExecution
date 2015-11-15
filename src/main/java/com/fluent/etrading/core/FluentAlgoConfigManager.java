package com.fluent.etrading.core;

import com.fluent.framework.config.*;


public final class FluentAlgoConfigManager{

	private final ConfigManager cfgManager;
	 
	 
	public FluentAlgoConfigManager( ConfigManager cfgManager ){
		this.cfgManager = cfgManager;
	}
	
	
	public final ConfigManager getRootConfig( ){
		return cfgManager;
	}
	
	
}
