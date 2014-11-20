package com.fluent.etrading.execution;

import org.slf4j.*;

import java.util.*;

import com.fluent.etrading.framework.core.*;
import com.fluent.etrading.framework.events.in.*;
import com.fluent.etrading.framework.market.core.*;
import com.fluent.etrading.framework.events.core.*;
import com.fluent.etrading.framework.dispatcher.out.*;
import com.fluent.etrading.framework.events.out.order.*;
import com.fluent.etrading.framework.events.out.executor.*;

import static com.fluent.etrading.framework.events.core.FluentOutputEventType.*;


/**
 * TODO:
 * What if we have an admin error than prevents us from sending an order.
 * How do we notify the Strategy?
 */

public final class OrderExecutionManager implements FluentOutputEventListener, FluentService{

    private final LoopbackEventProvider provider;
    private final Map<Marketplace, OrderExecutor> executorMap;

    private final static String NAME        = OrderExecutionManager.class.getSimpleName();
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );


    public OrderExecutionManager( LoopbackEventProvider provider, List<OrderExecutor> executorList ){
        this.provider       = provider;
        this.executorMap    = new HashMap<Marketplace, OrderExecutor>( executorList.size() );

        for( OrderExecutor executor : executorList ){
            executorMap.put( executor.getMarketType(), executor );
        }
    }


    @Override
    public final String name( ){
        return NAME;
    }

    @Override
    public boolean isSupported( final FluentOutputEventType type ){
        return ( ORDER_TO_MARKET == type );
    }

    @Override
    public final void init( ){
        OutputEventDispatcher.add( this );
        LOGGER.info("Configured market executor for [{}].", executorMap.keySet() );
        LOGGER.info("Successfully started [{}], listening for [ORDER_TO_MARKET] event.", NAME  );
    }


    @Override
    public void update( final FluentOutputEvent outputEvent ){

        OrderEvent oEvent           = (OrderEvent) outputEvent;
        Marketplace type             = oEvent.getMarketType();
        OrderExecutor oExecutor     = executorMap.get( type );

        if( oExecutor != null ){
            oExecutor.execute( provider, oEvent );
            return;
        }

        String reason               = "No executor is configured for Market: " + type;
        LoopbackEvent loopEvent     = OrderExecutor.createInvalidReport( reason, oEvent );

        provider.addLoopbackEvent( loopEvent );

    }


    @Override
    public void stop( ){
        OutputEventDispatcher.remove( this );
        LOGGER.info("Successfully stopped {}.", NAME );
    }


}
