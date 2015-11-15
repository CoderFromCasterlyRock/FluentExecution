package com.fluent.etrading.events.out;

import com.fluent.framework.events.out.OutEvent;

import static com.fluent.framework.events.core.FluentJsonTags.*;
import static com.fluent.framework.events.out.OutType.*;


public final class ResponseEvent extends OutEvent{

	private final String strategyId;
    private final String orderId;
    private final String message;


    private final static long serialVersionUID = 1l;
    
    
    public ResponseEvent( String strategyId, String orderId, String message ){
        super( EVENT_TO_TRADER );

        this.strategyId     = strategyId;
        this.orderId    	= orderId;
        this.message    	= message;
        
    }

    
    public final String getStrategyId( ){
        return strategyId;
    }
    
    
    public final String getOrderId( ){
        return orderId;
    }
    

    public final String getMessage( ){
        return message;
    }

    
	@Override
	public final void toEventString(StringBuilder builder) {
		builder.append( STRATEGY_ID.field() ).append( strategyId );
		builder.append( ORDER_ID.field()).append( orderId );
    	builder.append( UPDATE_MESSAGE.field()).append( message );
    }
	

}
