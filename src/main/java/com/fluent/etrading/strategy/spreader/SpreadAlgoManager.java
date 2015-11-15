package com.fluent.etrading.strategy.spreader;

import org.slf4j.*;
import org.cliffc.high_scale_lib.*;

import com.fluent.framework.algo.*;
import com.fluent.framework.core.*;

import com.fluent.etrading.events.in.*;
import com.fluent.etrading.events.out.*;
import com.fluent.framework.events.in.*;

import static com.fluent.framework.util.TimeUtil.*;


public final class SpreadAlgoManager extends FluentAlgoManager{

    private final NonBlockingHashMap<String, SpreadAlgo> strategyMap;

    private final static String NAME        = SpreadAlgoManager.class.getSimpleName();
    private final static Logger LOGGER      = LoggerFactory.getLogger( NAME );


    public SpreadAlgoManager(  FluentServices services ){
        super( NAME, services );
        this.strategyMap	= new NonBlockingHashMap<>( );

    }


    @Override
    protected final void handleNewStrategy( final InEvent inputEvent ){

        try{

        	LOGGER.info( "Received a NEW Strategy [{}].", inputEvent );
        	
        	NewStrategyEvent tEvent  	= (NewStrategyEvent) inputEvent;
        	String strategyId           = tEvent.getStrategyId();
            SpreadAlgo strategy         = new SpreadAlgo( strategyId, tEvent );

            strategyMap.put( strategyId, strategy );
            strategy.start();
            strategy.update( inputEvent );
            
        }catch( Exception e ){
            LOGGER.warn( "Failed to created Strategy Id:" + inputEvent, e );
        }

    }

    
    @Override
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
            LOGGER.warn( "Failed to cancel Strategy Id:" + inputEvent, e );
        }

    }
    
    
    @Override
    protected final void handleExecutionReport( final InEvent inputEvent ){
        ExecutionReportEvent eReport    = (ExecutionReportEvent) inputEvent;

        String strategyId                = eReport.getEventId();
        SpreadAlgo strategy             = getStrategy( strategyId );
        if( strategy == null ) return;

        strategy.update( inputEvent );
    }


    @Override
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
	protected void handleModifyStrategy(InEvent inputEvent) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void handleMetronomeEvent(InEvent inputEvent) {
		// TODO Auto-generated method stub
		
	}



}

