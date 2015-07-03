package com.fluent.etrading.strategy.spreader;

import org.slf4j.*;
import java.util.*;
import java.util.concurrent.*;
import org.jctools.queues.*;

import com.fluent.framework.util.*;
import com.fluent.etrading.order.*;
import com.fluent.etrading.request.*;
import com.fluent.etrading.execution.*;
import com.fluent.framework.events.in.*;
import com.fluent.framework.collection.*;
import com.fluent.framework.events.core.*;
import com.fluent.etrading.market.core.*;
import com.fluent.etrading.strategy.core.*;

import static com.fluent.framework.util.FluentUtil.*;


public final class SpreadAlgo extends AbstractAlgo implements Runnable{

    private AlgoState state;
    private volatile boolean keepRunning; 

    private final String[] instruments;
    private final ExecutorService service;
    private final SpscArrayQueue<InboundEvent> queue;
    private final Map<String, MarketDataEvent> priceMap;

    private final static Logger LOGGER      =  LoggerFactory.getLogger( SpreadAlgo.class.getSimpleName() );


    public SpreadAlgo( String strategyId, NewStrategyEvent newStrategy){
    	
        super( strategyId, newStrategy.getStrategyName(), newStrategy.getOwner() );

        this.instruments    = newStrategy.getInstrumentArray();
        this.priceMap       = new HashMap<String, MarketDataEvent>( FOUR );
        this.queue          = new SpscArrayQueue<InboundEvent>( FOUR * SIXTY_FOUR );
        this.service        = Executors.newSingleThreadExecutor( new FluentThreadFactory( getStrategyName() ) );

        this.state          = AlgoState.CREATED;
    }

    
    @Override
    public final String name( ){
        return getStrategyName();
    }
    
    
	@Override
	public final void prime( ){
		
	}


    @Override
    public final void init( ){
        keepRunning = true;
        service.execute( this );

        String outputMessage    = new StringBuilder( getStrategyName()).append(" OwnedBy:").append( getStrategyOwner() ).append(" STARTED").toString();

        //getOutDispatcher().eaddResponseEvent( nextOutputEventId(), cEvent, outputMessage );
        LOGGER.debug( "[{}]",outputMessage );
    }

    
    public final AlgoState getStrategyState( ){
    	return state;
    }
    

    @Override
    public final void update( InboundEvent event ){
        queue.offer( event );
    }


    @Override
    public final void run( ){

        while( keepRunning ){

            try{

            	InboundEvent event = queue.poll();
                if( event == null ){
                    FluentBackoffStrategy.apply();
                    continue;
                }

                handleEvent( event );

            }catch( Exception e ){
                LOGGER.warn( "Exception while processing events.", e );
            }

        }

    }


    protected final void handleEvent( InboundEvent event ){

        InboundType type    		= event.getType();
        FluentEventCategory category 	= event.getCategory();

        switch( category ){

            case FROM_INTERNAL_CATEGORY:
                handleInternalRequest( type, event );
                break;

            case FROM_TRADER_CATEGORY:
                handleTraderRequest( type, event );
                break;

            case FROM_MARKET_CATEGORY:
                handleMarketRequest( type, event );
                break;

            case FROM_EXECUTION_CATEGORY:
                handleExecutionRequest( type, event );
                break;

            default:
                LOGGER.warn( "Type [{}] of Category [{}] is unsupported.", type, category );

        }

    }


    protected void handleTraderRequest( InboundType type, InboundEvent event ){

    }

    protected void handleMarketRequest( InboundType type, InboundEvent event ){

        MarketDataEvent mdEvent = (MarketDataEvent) event;
        String instrumentId     = mdEvent.getSymbol();

        LOGGER.debug( "[{}]", mdEvent.toJSON() );
        boolean updatedNeeded   = marketUpdateRequired( instrumentId, instruments );

    }


    protected void handleExecutionRequest( InboundType type, InboundEvent event ){

        ExecutionReportEvent eReport    = (ExecutionReportEvent) event;
        String orderId                  = eReport.getOrderId();
        OrderFillStatus fillStatus      = eReport.getFillStatus();
        String eReportAsJson            = eReport.toJSON();

        LOGGER.debug( "ExecutionReport arrived [{}]", eReportAsJson);
       // getOutDispatcher().addResponseEvent( nextOutputEventId(), eReport, "Execution Report Arrived" );

        switch( fillStatus ){

            case INTERNALLY_REJECTED:{
                String message =  "STOPPING [" + getStrategyName() + "] as OrderId " + orderId + " was internally rejected.";
                LOGGER.warn( "{}", message );
                //getOutDispatcher().addResponseEvent( nextOutputEventId(), null, message );
                stop();
                break;
            }

            case MARKET_REJECTED:{
                String message =  "STOPPING [" + getStrategyName() + "] as OrderId " + orderId + " was rejected by " + eReport.getMarketType();
                LOGGER.warn( "{}", message );
                //getOutDispatcher().addResponseEvent( nextOutputEventId(), null, message );
                stop();
                break;
            }

            case LIVE_ZERO_FILL:
            case LIVE_PARTIAL_FILL:
            case LIVE_FULL_FILL:
            case LIVE_OVER_FILL:
                String message =  "Fill Arrived for OrderId " + orderId + " of " + getStrategyName() + " from Market: " + eReport.getMarketType();
                LOGGER.debug( "{}", message );
                //HANDLE IT
                //getOutDispatcher().addResponseEvent( nextOutputEventId(), null, message );
                break;

            default:
                LOGGER.warn( "OrderFillStatus [{}] is UNHANDLED!", fillStatus );

        }

    }


    protected void handleInternalRequest( InboundType type, InboundEvent event ){
        LOGGER.warn( "Internal Request [{}] is unsupported.", type );
    }


    @Override
    public final void stop( ){
        keepRunning = false;
        LOGGER.debug( "Stopped [{}] thread.", getStrategyName() );
    }


    @Override
    public final String toString( ){
        return getStrategyName();
    }


}
