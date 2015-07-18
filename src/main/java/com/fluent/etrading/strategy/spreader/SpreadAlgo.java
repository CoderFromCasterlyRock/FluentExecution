package com.fluent.etrading.strategy.spreader;

import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;

import org.jctools.queues.*;

import com.fluent.framework.market.MarketDataEvent;
import com.fluent.framework.util.*;
import com.fluent.etrading.order.*;
import com.fluent.etrading.events.in.*;
import com.fluent.etrading.events.out.*;
import com.fluent.framework.events.in.*;
import com.fluent.framework.collection.*;
import com.fluent.etrading.strategy.core.*;

import static com.fluent.framework.util.FluentUtil.*;


public final class SpreadAlgo extends AbstractAlgo implements Runnable{

    private AlgoState state;
    private volatile boolean keepRunning; 

    private final String[] instruments;
    private final ExecutorService service;
    private final SpscArrayQueue<InEvent> queue;
    private final Map<String, MarketDataEvent> priceMap;

    private final static Logger LOGGER      =  LoggerFactory.getLogger( SpreadAlgo.class.getSimpleName() );


    public SpreadAlgo( String strategyId, NewStrategyEvent strategy ){
    	
        super( strategyId, strategy.getStrategyName(), strategy.getStrategyTrader() );

        this.instruments    = strategy.getInstruments();
        this.priceMap       = new HashMap<String, MarketDataEvent>( FOUR );
        this.queue          = new SpscArrayQueue<InEvent>( FOUR * SIXTY_FOUR );
        this.service        = Executors.newSingleThreadExecutor( new FluentThreadFactory( getStrategyName() ) );
        this.state			= AlgoState.CREATED;
        
    }

    
    @Override
    public final String name( ){
        return getStrategyName();
    }


    @Override
    public final void start( ){
        keepRunning = true;
        service.execute( this );

        LOGGER.debug( "Strategy :: {}", toString() );
    }

    
    public final AlgoState getStrategyState( ){
    	return state;
    }
    

    @Override
    public final void update( InEvent event ){
        queue.offer( event );
    }


    @Override
    public final void run( ){

        while( keepRunning ){

            try{

            	InEvent event = queue.poll();
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


    protected final void handleEvent( InEvent event ){

        InType type	= event.getType();

        switch( type ){

        		case MARKET_DATA:
        			handleMarketData( event );
        		break;

        		
        		case NEW_STRATEGY:
        			handleNewStrategy( event );
                break;

                
        		case CANCEL_STRATEGY:
        			handleCancelStrategy( event );
                break;

            
        		case EXECUTION_REPORT:
        			handleExecutionReport( event );
                break;

            default:
                LOGGER.warn( "InboundEvent of Type [{}] is unsupported.", type );

        }

    }


     protected void handleMarketData( InEvent event ){

        MarketDataEvent mdEvent	= (MarketDataEvent) event;
        String instrument	    = mdEvent.getSymbol();
        boolean updatedNeeded   = marketUpdateRequired( instrument, instruments );
        if( !updatedNeeded ){
        	LOGGER.warn("Discarding market data update for [{}] as Strategy:[{}] doesnt use it.", instrument, getStrategyId() );
        }
    
     }
     
     
     
     protected void handleNewStrategy( InEvent event ){
      	LOGGER.debug( "NEW STARTEGY EXECUTE!");
      }
     
     
     protected void handleCancelStrategy( InEvent event ){
     	LOGGER.debug( "handleCancelStrategy is unsupported!");
     }
     


    protected void handleExecutionReport( InEvent event ){

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


    @Override
    public final void stop( ){
        keepRunning = false;
        LOGGER.debug( "Stopped [{}] thread.", getStrategyName() );
    }


    @Override
    public final String toString( ){
    	
        StringBuilder info 	= new StringBuilder( THIRTY_TWO );
        
        info.append( getStrategyName() ).append( COMMA );
        info.append( getStrategyId() ).append( COMMA );
        info.append( getStrategyOwner() ).append( COMMA );
        info.append( getStrategyState() );
        
        return info.toString();
    }


}
