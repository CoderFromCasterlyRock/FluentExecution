package com.fluent.etrading.config;

import java.util.*;

import com.typesafe.config.*;
import com.fluent.framework.config.*;


public final class AlgoConfigManager extends ConfigManager{

	
	private final static String EXCHANGES_KEY	= APP_SECTION_KEY + "exchanges"; 
	
	public AlgoConfigManager( ){
		
	}

	
	
	public final List<? extends Config> getExchangeConfig( ){
		
		List<? extends Config> configs 	= getConfig().getConfigList( EXCHANGES_KEY );
		if( configs == null || configs.isEmpty() ){
			throw new IllegalStateException("Section " + EXCHANGES_KEY + " must be configured!");
		}
		
		return configs;
		
	}


	
}
