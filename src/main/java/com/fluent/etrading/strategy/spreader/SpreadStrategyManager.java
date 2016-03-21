package com.fluent.etrading.strategy.spreader;

import java.util.*;
import org.slf4j.*;
import org.cliffc.high_scale_lib.*;

import com.fluent.etrading.events.in.*;
import com.fluent.etrading.events.out.*;
import com.fluent.framework.core.*;
import com.fluent.framework.events.in.*;
import com.fluent.framework.strategy.*;
import com.fluent.framework.admin.core.*;
import com.typesafe.config.Config;

import static com.fluent.framework.util.TimeUtil.currentNanos;


public final class SpreadStrategyManager implements FluentInListener, FluentLifecycle{

	private final FluentServices services;
	private final Set<StrategyType> strategyTypes;
	private final NonBlockingHashMap<String, FluentStrategy> strategyMap;
	
	private final static String SECTION_KEY	= "fluent.strategy";
	private final static String TYPES_KEY	= "types";
	private final static String NAME        = SpreadStrategyManager.class.getSimpleName();
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );


    public SpreadStrategyManager( FluentServices services ){
    	this.services		= services;
    	this.strategyTypes 	= parseTypes( services.getCfgManager() );
    	this.strategyMap	= new NonBlockingHashMap<>( );

    }


	@Override
    public final String name( ){
        return NAME;
    }
    
	
    @Override
    public final boolean isSupported( FluentInType type ){
        return true;
    }
    
    
    public final Set<StrategyType> getStrategyTypes( ){
        return strategyTypes;
    }
    

    @Override
    public final void start( ){
        services.getInDispatcher().register( this );
        LOGGER.info("Started and will support StrategyTypes:{}.", strategyTypes );
    }


    @Override
    public final boolean inUpdate( final FluentInEvent inEvent ){

    	if( !StateManager.isRunning() ){
    		LOGGER.warn("Discarding event [{}] as we are currently in [{}] state!", inEvent, StateManager.getState() );
    		return false;
    	}
    	
    	FluentInType type 	= inEvent.getType( );

        switch( type ){
        	
            case NEW_STRATEGY:
                handleNewStrategy( inEvent );
            break;

            case MODIFY_STRATEGY:
            	handleModifyStrategy( inEvent );
            break;

            case CANCEL_STRATEGY:
            	handleCancelStrategy( inEvent );
            break;

            case EXECUTION_REPORT:
                handleExecutionReport( inEvent );
            break;

            case MARKET_DATA:
                handleMarketMessage( inEvent );
            break;

            case METRONOME_EVENT:
                handleMetronomeEvent( inEvent );
            break;
                        
            default:
                LOGGER.warn( "Input event of type [{}] is unsupported.", type );
        }

        return true;
        
    }


	protected final void handleNewStrategy( FluentInEvent inEvent ){

        try{

        	LOGGER.info( "Received a NEW Strategy [{}].", inEvent );
        	
        	NewStrategyEvent tEvent  	= (NewStrategyEvent) inEvent;
        	String strategyId           = tEvent.getStrategyId();
            SpreadAlgo strategy         = new SpreadAlgo( strategyId, tEvent );

            strategyMap.put( strategyId, strategy );
            strategy.start();
            strategy.update( inEvent );
            
        }catch( Exception e ){
            LOGGER.warn( "Failed to created Strategy Id:" + inEvent, e );
        }

    }

    
    protected final void handleCancelStrategy( FluentInEvent inEvent ){

        try{

        	CancelStrategyEvent tEvent  = (CancelStrategyEvent) inEvent;
        	String strategyId           = tEvent.getStrategyId();
            
        	FluentStrategy strategy		= strategyMap.get( strategyId );
        	if( strategy == null ){
        		LOGGER.warn( "Discarding request to CANCEL as Strategy for StrategyId: [{}] is missing.", strategyId );
        		return;
        	}
        	
            strategy.update( inEvent );
            
        }catch( Exception e ){
            LOGGER.warn( "Failed to cancel Strategy Id:" + inEvent, e );
        }

    }
    
    
    protected final void handleExecutionReport( FluentInEvent inEvent ){
        ExecutionReportEvent eReport    = (ExecutionReportEvent) inEvent;

        String strategyId                = eReport.getEventId();
        FluentStrategy strategy          = getStrategy( strategyId );
        if( strategy == null ) return;

        strategy.update( inEvent );
    }


    
    protected final void handleMarketMessage( FluentInEvent inEvent ){
    	
    	long latencyMicros	= (currentNanos() - inEvent.getCreationTime())/1000;
    	LOGGER.debug("Latency [{}] micros, MD arrived {}", latencyMicros, inEvent );
    	 
        for( FluentStrategy strategy : strategyMap.values() ){
            strategy.update( inEvent );
        }

    }


    protected final FluentStrategy getStrategy( String strategyId ){
    	FluentStrategy strategy = strategyMap.get( strategyId );

        if( strategy == null ){
            String message = "Failed to find strategy corresponding to Id:" + strategyId;
            LOGGER.warn( "{}", message );
            //getOutDispatcher().addResponseEvent( nextOutputEventId(), null, message );
        }

        return strategy;

    }


	protected final void handleModifyStrategy( FluentInEvent inEvent ){
		// TODO Auto-generated method stub
		
	}


	
	protected final void handleMetronomeEvent( FluentInEvent inEvent ){
		// TODO Auto-generated method stub
		
	}
	
	
	protected final void handleShutDownEvent( FluentInEvent inEvent ){
   	 
        for( FluentStrategy strategy : strategyMap.values() ){
            strategy.update( inEvent );
        }
		
	}
    
    
    protected final Set<StrategyType> parseTypes( FluentConfigManager cfgManager ){
    	
    	Set<StrategyType> types 	= new HashSet<>();
    	Config configuration			= cfgManager.getConfig();
    	
    	for( String typeName :  configuration.getConfig(SECTION_KEY).getStringList(TYPES_KEY) ){
			StrategyType type		= StrategyType.getType(typeName);
			if( StrategyType.UNSUPPORTED == type ){
				throw new RuntimeException("Strategy Type: " + type + " is UNSUPPORTED!");
			}
			
			types.add( type );
		}
    	
    	return types;
	}
    
    
    @Override
    public final void stop(){
    	services.getInDispatcher().deregister( this );
        LOGGER.info("Stopped [{}].", NAME );
    }


}

