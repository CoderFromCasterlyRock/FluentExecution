package com.fluent.etrading.algo.spreader;

import org.slf4j.*;

import org.cliffc.high_scale_lib.*;
import com.fluent.etrading.requests.*;
import com.fluent.etrading.algo.core.*;

import com.fluent.etrading.framework.core.*;
import com.fluent.etrading.framework.events.in.*;
import com.fluent.etrading.framework.events.core.*;
import com.fluent.etrading.framework.dispatcher.in.*;

import static com.fluent.etrading.framework.utility.ContainerUtil.*;



public final class SpreadAlgoManager extends AbstractAlgoManager{

    private final NonBlockingHashMapLong<SpreadAlgo> strategyMap;

    private final static String NAME        = SpreadAlgoManager.class.getSimpleName();
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );


    public SpreadAlgoManager( SpreadAlgoFactory factory ){
        super( factory );

        this.strategyMap        = new NonBlockingHashMapLong<SpreadAlgo>(  );

    }


    @Override
    public final String name(){
        return NAME;
    }


    @Override
    public final void init( ){
        FluentInputEventDispatcher.add( this );
        LOGGER.info("[{}] initialized, listening for [{}].", NAME, FluentEventCategory.allInput() );
    }


    @Override
    public final void update( final FluentInputEvent inputEvent ){

    	if( !FluentContext.isReady() ){
    		LOGGER.warn("Discarding event [{}] as container is currently in [{}] state!", inputEvent, FluentContext.getState() );
    		return;
    	}
    	
    	FluentInputEventType type 	= inputEvent.getType( );

        switch( type ){

            case ADMIN_EVENT:
                handleInternal( inputEvent );
            break;

            case LOOPBACK_EVENT:
                handleLoopback( inputEvent );
            break;

            case CREATE_STRATEGY:
                createStrategy( inputEvent );
            break;

            case AMEND_STRATEGY:
            break;

            case CANCEL_STRATEGY:
            break;

            case CANCEL_ALL_STRATEGY:
            break;

            case EXECUTION_REPORT_UPDATE:
                handleExecutionReport( inputEvent );
            break;

            case TREASURY_MD:
            case FUTURES_MD:
            case SWAPS_MD:
                handleMarketMessage( inputEvent );
            break;

            default:
                LOGGER.warn( "Input event of type [{}] is unsupported.", type );
        }

    }


    protected final void handleInternal( final FluentInputEvent inputEvent ){
        LOGGER.warn( "handleInternal is UNSUPPORTED ATM.");
    }


    protected final void handleLoopback( final FluentInputEvent inputEvent ){
        FluentInputEvent loopEvent   = ((LoopbackEvent) inputEvent).getLoopbackEvent();
        LOGGER.debug("Loopback event carries event of Type [{}].", loopEvent.getType() );
        update( loopEvent );
    }


    protected final void createStrategy( final FluentInputEvent inputEvent ){

        boolean started                 = false;
        String message                  = EMPTY;
        long strategyId                 = NEGATIVE_ONE;

        try{

            CreateStrategyEvent tEvent  = (CreateStrategyEvent) inputEvent;
            strategyId                  = tEvent.getStrategyId();
            SpreadAlgo strategy         = getFactory().create( strategyId, tEvent );

            if( strategy != null ){
                strategy.init();

                strategyMap.put( strategyId, strategy );
                started = true;
            }

        }catch( Exception e ){
            message = "Internal Error! Failed to start Strategy Id:" + strategyId;
            LOGGER.warn( message );
            LOGGER.warn("Exception:", e );

        }finally{
            if( !started ){
                LOGGER.warn("Failed to start Strategy, Trader must be updated!");
                getOutDispatcher().addResponseEvent( nextOutputEventId(), inputEvent, message );
            }
        }

    }


    protected final void handleExecutionReport( final FluentInputEvent inputEvent ){
        ExecutionReportEvent eReport    = (ExecutionReportEvent) inputEvent;

        long strategyId                 = eReport.getStrategyId();
        SpreadAlgo strategy             = getStrategy( strategyId );
        if( strategy == null ) return;

        strategy.update( inputEvent );
    }


    protected final void handleMarketMessage( final FluentInputEvent inputEvent ){
    	
        for( SpreadAlgo strategy : strategyMap.values() ){
            strategy.update( inputEvent );
        }

    }


    protected final SpreadAlgo getStrategy( final long strategyId ){
        SpreadAlgo strategy = strategyMap.get( strategyId );

        if( strategy == null ){
            String message = "Failed to find strategy corresponding to Id:" + strategyId;
            LOGGER.warn( "{}", message );
            getOutDispatcher().addResponseEvent( nextOutputEventId(), null, message );
        }

        return strategy;

    }


    @Override
    public final void stop(){
        FluentInputEventDispatcher.remove( this );
        LOGGER.info("Stopped [{}].", NAME );
    }

}

