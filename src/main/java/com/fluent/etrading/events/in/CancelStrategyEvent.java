package com.fluent.etrading.events.in;

import com.eclipsesource.json.*;
import com.fluent.framework.events.in.*;

import static com.fluent.framework.util.FluentUtil.*;
import static com.fluent.framework.events.core.FluentJsonTags.*;


public final class CancelStrategyEvent extends InboundEvent{

	private final String eventId;
	private final String strategyId;
	private final String strategyTrader;
	private final String reason;
    
    private final static long serialVersionUID 	= 1L;
    private final static String PREFIX 			= "CancelStrategy-";
    
    
    public CancelStrategyEvent( String strategyId, String strategyTrader, String reason ){

        super( InboundType.NEW_STRATEGY );

        this.strategyId			= strategyId;
        this.strategyTrader     = strategyTrader;
        this.reason       		= reason;
        this.eventId			= PREFIX + strategyId + UNDERSCORE + getSequenceId();
   
    }

   
    @Override
    public final String getEventId( ){
        return eventId;
    }
    
    
    public final String getStrategyId( ){
        return strategyId;
    }
    

    public final String getStrategyTrader(){
        return strategyTrader;
    }
    

    public final String getReason(){
        return reason;
    }

        
    @Override
    protected final void toJSON( final JsonObject object ){
        object.add( STRATEGY_ID.field(),        strategyId );
        object.add( STRATEGY_OWNER.field(),     strategyTrader );
        object.add( REASON.field(),     		reason );
    }
    

}


