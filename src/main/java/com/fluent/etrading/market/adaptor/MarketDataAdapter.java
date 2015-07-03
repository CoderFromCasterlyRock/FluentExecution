package com.fluent.etrading.market.adaptor;

import org.slf4j.*;

import com.fluent.etrading.codec.*;
import com.fluent.framework.core.*;
import com.fluent.framework.market.*;
import com.fluent.framework.events.in.*;
import com.fluent.framework.events.core.*;
import com.fluent.framework.transport.core.*;

import static com.fluent.framework.events.in.InboundType.*;


public final class MarketDataAdapter implements FluentDataListener, FluentStartable{
	
	private final ExchangeInfo eInfo;
	private final Transport transport;
			 
	private final static String NAME		= MarketDataAdapter.class.getSimpleName();
	private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );

	
	public MarketDataAdapter( ExchangeInfo eInfo ){
		this.eInfo		= eInfo;
		this.transport	= TransportFactory.create( eInfo );
						
	}
	
	
	@Override
	public final String name( ){
		return NAME;
	}

	
	@Override
	public void init( ){
		transport.register( this );
		transport.init();
		LOGGER.info("Successfully started market data provider for [{}].", name() );
	}
	
	
	public final ExchangeInfo getExchangeInfo( ){
		return eInfo;
	}

	
	@Override
	public final void onMessage( String message ){
		InboundEvent mdEvent 	= InboundEncoderFactory.create( MARKET_DATA, message );
		if( mdEvent == null ) return;
		
		InboundEventDispatcher.enqueue( mdEvent );
	}
	

	
	@Override
	public void stop( ){
		transport.deregister( this );
		transport.stop();
		LOGGER.info("Successfully stopped market data provider for [{}].", name() );
	}

}


