package com.fluent.etrading.execution;

import org.slf4j.*;

import java.util.*;

import com.fluent.etrading.order.OrderEvent;
import com.fluent.framework.core.*;
import com.fluent.framework.events.in.*;
import com.fluent.framework.market.*;
import com.fluent.framework.events.core.*;
import com.fluent.framework.events.out.*;

import static com.fluent.framework.events.out.OutType.*;

/**
 * TODO:
 * What if we have an admin error than prevents us from sending an order.
 * How do we notify the Strategy?
 */

/*
public final class OrderExecutionManager implements OutboundListener, FluentService{

    private final LoopbackEventProvider provider;
    private final Map<Exchange, OrderExecutor> executorMap;

    private final static String NAME        = OrderExecutionManager.class.getSimpleName();
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );


    public OrderExecutionManager( LoopbackEventProvider provider, List<OrderExecutor> executorList ){
        this.provider       = provider;
        this.executorMap    = new HashMap<Exchange, OrderExecutor>( executorList.size() );

        for( OrderExecutor executor : executorList ){
            executorMap.put( executor.getMarketType(), executor );
        }
    }


    @Override
    public final String name( ){
        return NAME;
    }

    @Override
    public boolean isSupported( final OutboundType type ){
        return ( ORDER_TO_MARKET == type );
    }

    @Override
    public final void init( ){
        
    	OutboundEventDispatcher.register( this );
        LOGGER.info("Configured market executor for [{}].", executorMap.keySet() );
        LOGGER.info("Successfully started [{}], listening for [ORDER_TO_MARKET] event.", NAME  );
    }


    @Override
    public final boolean update( final OutboundEvent outputEvent ){

        OrderEvent oEvent           = (OrderEvent) outputEvent;
        Exchange type            	 = oEvent.getExchange();
        OrderExecutor oExecutor     = executorMap.get( type );

        if( oExecutor != null ){
            oExecutor.execute( provider, oEvent );
            return false;
        }

        String reason               = "No executor is configured for Market: " + type;
        LoopbackEvent loopEvent     = OrderExecutor.createInvalidReport( reason, oEvent );

        provider.addLoopbackEvent( loopEvent );
        
        return true;
    }


    @Override
    public void stop( ){
        OutboundEventDispatcher.deregister(this );
        LOGGER.info("Successfully stopped {}.", NAME );
    }


}
*/