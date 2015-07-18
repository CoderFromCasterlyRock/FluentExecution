package com.fluent.etrading.codec;

import java.util.*;

import org.slf4j.*;

import com.fluent.framework.market.*;
import com.fluent.framework.events.in.*;

import static com.fluent.framework.util.FluentUtil.*;
import static com.fluent.framework.util.FluentToolkit.*;


public final class InboundEncoderFactory{
	
	private final static String NAME		= InboundEncoderFactory.class.getSimpleName();
	private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );
	
	
	public final static InEvent create( InType type, String message ){
		
		InEvent event 	= null;
		
		try{
		
			switch( type ){
		
				case MARKET_DATA:
					event = createMdEvent( message );
					break;
				
				default:
					LOGGER.warn("Failed to encode as Type: {} is UNSUPPORTED.", type );
			
			}
			
		}catch( Exception e ){
			LOGGER.warn("Failed to encode event for Type: {}, Message: {}", type, message, e );
		}

		return event;
				
	}

	
	//CME:EDZ5:97.59:1000:98.41:1200
	protected final static InEvent createMdEvent( String message ){
	
		List<String> data		= fastSplit(message, COLON_CHAR);
		
		Exchange exchange		= Exchange.valueFrom( data.get(ZERO) );
		String symbol			= toUpper( data.get(ONE) );
		double bid				= Double.parseDouble(data.get(TWO) );
		int bidSize				= Integer.parseInt(data.get(THREE) );
		double ask				= Double.parseDouble(data.get(FOUR) );
		int askSize				= Integer.parseInt(data.get(FIVE) );
		
		return new MarketDataEvent( exchange, symbol, bid, bidSize, ask, askSize );
		
	}
	
	

}
