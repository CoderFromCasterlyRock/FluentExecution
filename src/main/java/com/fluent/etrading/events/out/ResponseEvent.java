package com.fluent.etrading.events.out;

import com.eclipsesource.json.*;
import com.fluent.framework.events.core.*;
import com.fluent.framework.events.out.OutboundEvent;

import static com.fluent.framework.events.core.FluentJsonTags.*;
import static com.fluent.framework.events.out.OutboundType.*;


public final class ResponseEvent extends OutboundEvent{

	private final String eventId;
	private final String strategyId;
    private final String orderId;
    private final String message;

    private final static String PREFIX = "RESPONSE_";
    private final static long serialVersionUID = 1l;
    
    
    public ResponseEvent( String strategyId, String orderId, String message ){
        super( EVENT_TO_TRADER );

        this.eventId     	= PREFIX + getSequenceId();
        this.strategyId     = strategyId;
        this.orderId    	= orderId;
        this.message    	= message;
        
    }


    @Override
    public final String getEventId( ){
        return eventId;
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
    protected final void toJSON( final JsonObject object ){
        
    	object.add( STRATEGY_ID.field(),     strategyId );
    	object.add( ORDER_ID.field(),  		 orderId );
    	object.add( UPDATE_MESSAGE.field(),  message );
        
    }

}
