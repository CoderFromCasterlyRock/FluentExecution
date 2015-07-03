package com.fluent.etrading.strategy.spreader;

import org.slf4j.*;
import org.cliffc.high_scale_lib.*;

import com.fluent.etrading.config.*;
import com.fluent.etrading.execution.ExecutionReportEvent;
import com.fluent.etrading.request.*;
import com.fluent.etrading.strategy.core.*;
import com.fluent.framework.admin.StateManager;
import com.fluent.framework.events.core.FluentEventCategory;
import com.fluent.framework.events.in.InboundEvent;
import com.fluent.framework.events.in.InboundEventDispatcher;
import com.fluent.framework.events.in.InboundType;



import static com.fluent.framework.util.FluentUtil.*;


public final class SpreadAlgoManager extends AbstractAlgoManager{

	private final AlgoConfigManager cfgManager;
    private final NonBlockingHashMap<String, SpreadAlgo> strategyMap;

    private final static String NAME        = SpreadAlgoManager.class.getSimpleName();
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );


    //public SpreadAlgoManager( AlgoConfigManager cfgManager, SpreadAlgoFactory factory ){
    public SpreadAlgoManager( AlgoConfigManager cfgManager ){
        
        this.cfgManager		= cfgManager;
        this.strategyMap	= new NonBlockingHashMap<>( );

    }


    @Override
    public final String name( ){
        return NAME;
    }
    
    
	@Override
	public final void prime( ){
		
	}


    @Override
    public final void init( ){
        InboundEventDispatcher.register( this );
        LOGGER.info("[{}] initialized, listening for [{}].", NAME, FluentEventCategory.allInputs() );
    }


    @Override
    public final boolean update( final InboundEvent inputEvent ){

    	if( !StateManager.isRunning() ){
    		LOGGER.warn("Discarding event [{}] as we are currently in [{}] state!", inputEvent, StateManager.getState() );
    		return false;
    	}
    	
    	InboundType type 	= inputEvent.getType( );

        switch( type ){

            case METRONOME_EVENT:
                handleInternal( inputEvent );
            break;

            case LOOPBACK_EVENT:
                handleLoopback( inputEvent );
            break;

            case NEW_STRATEGY:
                createStrategy( inputEvent );
            break;

            case MODIFY_STRATEGY:
            break;

            case CANCEL_STRATEGY:
            break;

            case CANCEL_ALL_STRATEGY:
            break;

            case EXECUTION_REPORT:
                handleExecutionReport( inputEvent );
            break;

            case MARKET_DATA:
                handleMarketMessage( inputEvent );
            break;

            default:
                LOGGER.warn( "Input event of type [{}] is unsupported.", type );
        }

        return true;
        
    }

    

    protected final void handleInternal( final InboundEvent inputEvent ){
        LOGGER.warn( "Unprocessed >> {} ", inputEvent );
    }


    protected final void handleLoopback( final InboundEvent inputEvent ){
    	LOGGER.warn( "Unprocessed >> {} ", inputEvent );
    }


    protected final void createStrategy( final InboundEvent inputEvent ){

        boolean started     = false;
        String message      = EMPTY;
        String strategyId	= EMPTY;

        try{

        	NewStrategyEvent tEvent  	= (NewStrategyEvent) inputEvent;
            strategyId                  = tEvent.getEventId();
            SpreadAlgo strategy         = new SpreadAlgo( strategyId, tEvent );

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
                //getOutDispatcher().addResponseEvent( nextOutputEventId(), inputEvent, message );
            }
        }

    }


    protected final void handleExecutionReport( final InboundEvent inputEvent ){
        ExecutionReportEvent eReport    = (ExecutionReportEvent) inputEvent;

        String strategyId                = eReport.getEventId();
        SpreadAlgo strategy             = getStrategy( strategyId );
        if( strategy == null ) return;

        strategy.update( inputEvent );
    }


    protected final void handleMarketMessage( final InboundEvent inputEvent ){
    	
    	LOGGER.debug("MD arrived {}", inputEvent );
    	 
        for( SpreadAlgo strategy : strategyMap.values() ){
            strategy.update( inputEvent );
        }

    }


    protected final SpreadAlgo getStrategy( final String strategyId ){
        SpreadAlgo strategy = strategyMap.get( strategyId );

        if( strategy == null ){
            String message = "Failed to find strategy corresponding to Id:" + strategyId;
            LOGGER.warn( "{}", message );
            //getOutDispatcher().addResponseEvent( nextOutputEventId(), null, message );
        }

        return strategy;

    }


    @Override
    public final void stop(){
        InboundEventDispatcher.deregister( this );
        LOGGER.info("Stopped [{}].", NAME );
    }


}

