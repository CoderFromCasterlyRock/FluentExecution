package com.fluent.etrading.events.in;

import com.fluent.framework.events.in.*;

import static com.fluent.framework.events.core.FluentJsonTags.*;


public final class CancelStrategyEvent extends InEvent{

	private final String strategyId;
	private final String strategyTrader;
	private final String reason;
    
    private final static long serialVersionUID 	= 1L;
    
    
    public CancelStrategyEvent( String strategyId, String strategyTrader, String reason ){

        super( InType.NEW_STRATEGY );

        this.strategyId			= strategyId;
        this.strategyTrader     = strategyTrader;
        this.reason       		= reason;
  
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
    public final void toEventString( StringBuilder builder ){
    	builder.append( STRATEGY_ID.field() ).append( strategyId );
        builder.append( STRATEGY_OWNER.field() ).append(strategyTrader );
        builder.append( REASON.field() ).append(reason );
    }
    

}


