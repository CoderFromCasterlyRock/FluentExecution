package com.fluent.etrading.algo.spreader;

import org.slf4j.*;
import java.util.*;
import java.util.concurrent.*;

import com.fluent.etrading.requests.*;
import com.fluent.etrading.algo.core.*;
import com.fluent.etrading.framework.core.*;
import com.fluent.etrading.framework.order.*;
import com.fluent.etrading.framework.events.in.*;
import com.fluent.etrading.framework.collections.*;
import com.fluent.etrading.framework.events.core.*;
import com.fluent.etrading.framework.market.core.*;
import com.fluent.etrading.framework.events.out.order.*;
import com.fluent.etrading.framework.dispatcher.core.*;
import com.fluent.etrading.framework.dispatcher.out.*;

import static com.fluent.etrading.framework.utility.ContainerUtil.*;
import static com.fluent.etrading.framework.events.core.FluentOutputEventType.*;


public final class SpreadAlgo extends AbstractAlgo implements Runnable{

    private AlgoState state;
    private volatile boolean keepRunning; 

    private final String[] instruments;
    private final BackoffStrategy backoff;
    private final ExecutorService service;
    private final CreateStrategyEvent cEvent;
    private final FluentSPSCQueue<FluentInputEvent> queue;
    private final Map<String, MarketDataEvent> priceMap;

    private final static Logger LOGGER      =  LoggerFactory.getLogger( SpreadAlgo.class.getSimpleName() );


    public SpreadAlgo( long strategyId, OutputEventDispatcher dispatcher, CreateStrategyEvent cEvent ){
        super( strategyId, cEvent.getStrategyName(), cEvent.getStrategyOwner(), dispatcher );

        this.cEvent         = cEvent;
        this.instruments    = cEvent.getInstrumentArray();
        this.state          = AlgoState.CREATED;
        this.backoff        = new BackoffStrategy( );
        this.priceMap       = new HashMap<String, MarketDataEvent>( FOUR );
        this.queue          = new FluentSPSCQueue<FluentInputEvent>( FOUR * SIXTY_FOUR );
        this.service        = Executors.newSingleThreadExecutor( new FluentThreadFactory( getFullStrategyName() ) );

    }

    @Override
    public final String name( ){
        return getFullStrategyName();
    }


    @Override
    public final void init( ){
        keepRunning = true;
        service.execute( this );

        String outputMessage    = new StringBuilder(getFullStrategyName()).append(" OwnedBy:").append( getStrategyOwner() ).append(" STARTED").toString();

        getOutDispatcher().addResponseEvent( nextOutputEventId(), cEvent, outputMessage );
        LOGGER.debug( "[{}]",outputMessage );
    }

    
    public final AlgoState getStrategyState( ){
    	return state;
    }
    

    @Override
    public final void update( FluentInputEvent event ){
        queue.offer( event );
    }


    @Override
    public final void run( ){

        while( keepRunning ){

            try{

                FluentInputEvent event = queue.poll();
                if( event == null ){
                    backoff.apply();
                    continue;
                }

                handleEvent( event );

            }catch( Exception e ){
                LOGGER.warn( "Exception while processing events.", e );
            }

        }

    }


    protected final void handleEvent( FluentInputEvent event ){

        FluentInputEventType type    = event.getType();
        FluentEventCategory category = event.getCategory();

        switch( category ){

            case INTERNAL_CATEGORY:
                handleInternalRequest( type, event );
                break;

            case TRADER_CATEGORY:
                handleTraderRequest( type, event );
                break;

            case MARKET_CATEGORY:
                handleMarketRequest( type, event );
                break;

            case EXECUTION_CATEGORY:
                handleExecutionRequest( type, event );
                break;

            default:
                LOGGER.warn( "Type [{}] of Category [{}] is unsupported.", type, category );

        }

    }


    protected void handleTraderRequest( FluentInputEventType type, FluentInputEvent event ){

    }

    protected void handleMarketRequest( FluentInputEventType type, FluentInputEvent event ){

        MarketDataEvent mdEvent = (MarketDataEvent) event;
        long mdEventId          = mdEvent.getEventId();
        String instrumentId     = mdEvent.getSymbol();

        boolean updatedNeeded   = marketUpdateRequired( instrumentId, instruments );
        if( !updatedNeeded ) return;

        //Based on some criteria, one of these price updates will cause us to send out an Order.
        if( mdEventId % 7 == 0 ){

            long orderId        = nextOutputEventId();
            OrderEvent order    = new OutrightOrderEvent(   getStrategyId(), orderId, mdEventId, ORDER_TO_MARKET,
                                                            Marketplace.BTEC, OrderType.NEW, instruments[0],
                                                            cEvent.getStrategySide(), cEvent.getSpread(),
                                                            cEvent.getQuantityArray()[0], cEvent.getQuantityArray()[0],
                                                            "e239063", "Vicky Singh", EMPTY );

            LOGGER.debug( "Market Data triggers Order creation [{}]", mdEvent.toJSON() );
            LOGGER.debug( "Order Details [{}]", order.toJSON() );

            priceMap.put( instrumentId, mdEvent );

            getOutDispatcher().addOrderEvent( order );
            getOutDispatcher().addResponseEvent( nextOutputEventId(), order, "Order Created!" );
            getOutDispatcher().addResponseEvent( nextOutputEventId(), mdEvent, "Market data which triggered Order." );

        }

    }


    protected void handleExecutionRequest( FluentInputEventType type, FluentInputEvent event ){

        ExecutionReportEvent eReport    = (ExecutionReportEvent) event;
        long orderId                    = eReport.getOrderId();
        OrderFillStatus fillStatus      = eReport.getFillStatus();
        String eReportAsJson            = eReport.toJSON();

        LOGGER.debug( "ExecutionReport arrived [{}]", eReportAsJson);
        getOutDispatcher().addResponseEvent( nextOutputEventId(), eReport, "Execution Report Arrived" );

        switch( fillStatus ){

            case INTERNALLY_REJECTED:{
                String message =  "STOPPING [" + getFullStrategyName() + "] as OrderId " + orderId + " was internally rejected.";
                LOGGER.warn( "{}", message );
                getOutDispatcher().addResponseEvent( nextOutputEventId(), null, message );
                stop();
                break;
            }

            case MARKET_REJECTED:{
                String message =  "STOPPING [" + getFullStrategyName() + "] as OrderId " + orderId + " was rejected by " + eReport.getMarketType();
                LOGGER.warn( "{}", message );
                getOutDispatcher().addResponseEvent( nextOutputEventId(), null, message );
                stop();
                break;
            }

            case LIVE_ZERO_FILL:
            case LIVE_PARTIAL_FILL:
            case LIVE_FULL_FILL:
            case LIVE_OVER_FILL:
                String message =  "Fill Arrived for OrderId " + orderId + " of " + getFullStrategyName() + " from Market: " + eReport.getMarketType();
                LOGGER.debug( "{}", message );
                //HANDLE IT
                getOutDispatcher().addResponseEvent( nextOutputEventId(), null, message );
                break;

            default:
                LOGGER.warn( "OrderFillStatus [{}] is UNHANDLED!", fillStatus );

        }

    }


    protected void handleInternalRequest( FluentInputEventType type, FluentInputEvent event ){
        LOGGER.warn( "Internal Request [{}] is unsupported.", type );
    }


    @Override
    public final void stop( ){
        keepRunning = false;
        LOGGER.debug( "Stopped [{}] thread.", getFullStrategyName() );
    }


    @Override
    public final String toString( ){
        return getFullStrategyName();
    }


}