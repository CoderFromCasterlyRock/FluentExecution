package com.fluent.etrading.codec;

import java.util.*;

import org.slf4j.*;

import com.fluent.framework.market.core.Exchange;
import com.fluent.framework.market.core.InstrumentSubType;
import com.fluent.framework.market.event.MarketDataEvent;
import com.fluent.framework.events.in.*;

import static com.fluent.framework.util.FluentUtil.*;
import static com.fluent.framework.util.FluentToolkit.*;


public final class InboundEncoderFactory{
	
	private final static String NAME		= InboundEncoderFactory.class.getSimpleName();
	private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );
	
	
	public final static FluentInEvent create( FluentInType type, String message ){
		
		FluentInEvent event 	= null;
		
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
	protected final static FluentInEvent createMdEvent( String message ){
	
		List<String> data		= fastSplit(message, COLON_CHAR);
		
		Exchange exchange		= Exchange.fromCode( data.get(ZERO) );
		InstrumentSubType sType	= InstrumentSubType.valueOf(data.get(ONE));
		String symbol			= toUpper( data.get(TWO) );
		double bid				= Double.parseDouble(data.get(THREE) );
		int bidSize				= Integer.parseInt(data.get(FOUR) );
		double ask				= Double.parseDouble(data.get(FIVE) );
		int askSize				= Integer.parseInt(data.get(SIX) );
		
		return new MarketDataEvent( exchange, sType, symbol, bid, bidSize, ask, askSize );
		
	}
	
	

}
