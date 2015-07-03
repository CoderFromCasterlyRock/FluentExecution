package com.fluent.etrading.market.core;

import org.slf4j.*;
import java.util.*;

import com.fluent.framework.core.*;
import com.fluent.etrading.config.*;
import com.fluent.framework.market.*;
import com.fluent.etrading.market.adaptor.*;
import com.typesafe.config.Config;

import static com.fluent.framework.util.FluentPreconditions.*;


public class MarketDataManager implements FluentStartable{

	private final AlgoConfigManager cfgManager;
	private final Map<ExchangeInfo, MarketDataAdapter> adaptorMap;
	
    private final static String NAME	= MarketDataManager.class.getSimpleName();
    private final static Logger LOGGER  = LoggerFactory.getLogger( NAME );

    
	public MarketDataManager( AlgoConfigManager cfgManager ){
		
		this.cfgManager		= notNull( cfgManager, "Algo config can't be null.");
		this.adaptorMap		= getAdaptorMap( );
		
	}
	
	
	@Override
	public final String name( ){
		return NAME;
	}
	
	
	@Override
	public final void init( ){
		for( MarketDataAdapter adaptor : adaptorMap.values() ){
			adaptor.init();
		}
	}
		
	
	protected final Map<ExchangeInfo, MarketDataAdapter> getAdaptorMap( ){
		
		Map<ExchangeInfo, MarketDataAdapter> map	= new HashMap<>( );
		
		for( Config exchangeConfig : cfgManager.getExchangeConfig() ){
			ExchangeInfo eInfo			= ExchangeInfoFactory.create( exchangeConfig );
			MarketDataAdapter adaptor 	= new MarketDataAdapter( eInfo );
			map.put( eInfo, adaptor );
		}
				
		return map;
		
	}


	
	@Override
	public final void stop() {
		for( MarketDataAdapter adaptor : adaptorMap.values() ){
			adaptor.stop();
		}
	}
	
}
