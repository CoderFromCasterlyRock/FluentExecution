package com.fluent.etrading.strategy.spreader;

import org.slf4j.*;
import org.cliffc.high_scale_lib.*;

import com.fluent.framework.admin.*;
import com.fluent.framework.core.*;
import com.fluent.etrading.core.*;
import com.fluent.etrading.events.in.*;
import com.fluent.etrading.events.out.*;
import com.fluent.framework.events.in.*;
import com.fluent.framework.events.out.OutEvent;
import com.fluent.framework.events.out.OutEventDispatcher;
import com.fluent.framework.events.core.*;
import com.fluent.framework.persistence.PersisterService;

import static com.fluent.framework.util.TimeUtil.*;


public final class SpreadAlgoManager implements InListener, FluentService{

	private final AlgoConfigManager cfgManager;
	private final InEventDispatcher inDispatcher;
	private final OutEventDispatcher outDispatcher;
    private final NonBlockingHashMap<String, SpreadAlgo> strategyMap;

    private final static String NAME        = SpreadAlgoManager.class.getSimpleName();
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );


    //public SpreadAlgoManager( AlgoConfigManager cfgManager, SpreadAlgoFactory factory ){
    public SpreadAlgoManager( AlgoConfigManager cfgManager, InEventDispatcher inDispatcher, OutEventDispatcher outDispatcher ){
        
        this.cfgManager		= cfgManager;
        this.inDispatcher	= inDispatcher;
        this.outDispatcher	= outDispatcher;
        this.strategyMap	= new NonBlockingHashMap<>( );

    }


    @Override
    public final String name( ){
        return NAME;
    }
    
	
    @Override
    public final boolean isSupported( final InType type ){
        return ( FluentEventCategory.INPUT.contains(type.getCategory()) );
    }
    

    @Override
    public final void start( ){
        inDispatcher.register( this );
        LOGGER.info("[{}] initialized, listening for [{}].", NAME, FluentEventCategory.allInputs() );
    }


    @Override
    public final boolean update( final InEvent inputEvent ){

    	if( !StateManager.isRunning() ){
    		LOGGER.warn("Discarding event [{}] as we are currently in [{}] state!", inputEvent, StateManager.getState() );
    		return false;
    	}
    	
    	InType type 	= inputEvent.getType( );

        switch( type ){

            case METRONOME_EVENT:
                //handleInternal( inputEvent );
            break;

            case NEW_STRATEGY:
                handleNewStrategy( inputEvent );
            break;

            case MODIFY_STRATEGY:
            break;

            case CANCEL_STRATEGY:
            	handleCancelStrategy( inputEvent );
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


    protected final void handleNewStrategy( final InEvent inputEvent ){

        try{

        	LOGGER.info( "Received a NEW Strategy [{}].", inputEvent.toJSON() );
        	
        	NewStrategyEvent tEvent  	= (NewStrategyEvent) inputEvent;
        	String strategyId           = tEvent.getStrategyId();
            SpreadAlgo strategy         = new SpreadAlgo( strategyId, tEvent );

            strategyMap.put( strategyId, strategy );
            strategy.start();
            strategy.update( inputEvent );
            
        }catch( Exception e ){
            LOGGER.warn( "Failed to created Strategy Id:" + inputEvent.toJSON(), e );
        }

    }

    
    protected final void handleCancelStrategy( final InEvent inputEvent ){

        try{

        	CancelStrategyEvent tEvent  = (CancelStrategyEvent) inputEvent;
        	String strategyId           = tEvent.getStrategyId();
            
        	SpreadAlgo strategy			= strategyMap.get( strategyId );
        	if( strategy == null ){
        		LOGGER.warn( "Discarding request to CANCEL as Strategy for StrategyId: [{}] is missing.", strategyId );
        		return;
        	}
        	
            strategy.update( inputEvent );
            
        }catch( Exception e ){
            LOGGER.warn( "Failed to cancel Strategy Id:" + inputEvent.toJSON(), e );
        }

    }
    

    protected final void handleExecutionReport( final InEvent inputEvent ){
        ExecutionReportEvent eReport    = (ExecutionReportEvent) inputEvent;

        String strategyId                = eReport.getEventId();
        SpreadAlgo strategy             = getStrategy( strategyId );
        if( strategy == null ) return;

        strategy.update( inputEvent );
    }


    protected final void handleMarketMessage( final InEvent inputEvent ){
    	
    	long latencyMicros	= (currentNanos() - inputEvent.getCreationTime())/1000;
    	LOGGER.debug("Latency [{}] micros, MD arrived {}", latencyMicros, inputEvent );
    	 
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
        inDispatcher.deregister( this );
        LOGGER.info("Stopped [{}].", NAME );
    }


}

