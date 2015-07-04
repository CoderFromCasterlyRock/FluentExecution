package com.fluent.etrading.core;

import org.slf4j.*;

import com.fluent.etrading.events.in.NewStrategyEvent;
import com.fluent.etrading.market.core.MarketDataManager;
import com.fluent.etrading.order.Side;
import com.fluent.etrading.strategy.spreader.SpreadAlgoManager;
import com.fluent.framework.admin.StateManager;
import com.fluent.framework.admin.MetronomeEvent;
import com.fluent.framework.core.FluentStartable;
import com.fluent.framework.events.in.*;
import com.fluent.framework.events.out.OutboundEventDispatcher;
import com.fluent.framework.market.Exchange;
import com.fluent.framework.market.InstrumentType;
import com.fluent.framework.util.TimeUtil;

import static com.fluent.framework.util.FluentUtil.*;
import static com.fluent.framework.events.in.InboundType.*;
import static com.fluent.framework.core.FluentContext.FluentState.*;


public final class FluentController implements InboundListener, FluentStartable{
	
	private final AlgoConfigManager cfgManager;
	private final StateManager stateManager;
	private final MarketDataManager mdManager;
	
	private final InboundEventDispatcher inDispatcher;
	private final OutboundEventDispatcher outDispatcher;
	
	private final SpreadAlgoManager algoManager;
		
	private final static String NAME    = FluentController.class.getSimpleName();
    private final static Logger LOGGER	= LoggerFactory.getLogger( NAME );

    
	public FluentController( AlgoConfigManager cfgManager ){
		
		this.cfgManager		= cfgManager;
		this.stateManager	= new StateManager( cfgManager );
		
		this.inDispatcher	= new InboundEventDispatcher( );
		this.mdManager		= new MarketDataManager( cfgManager );
		
		this.outDispatcher	= new OutboundEventDispatcher( );
		
		this.algoManager	= new SpreadAlgoManager( cfgManager );
		
	}

	
	@Override
	public final String name( ){
		return NAME;
	}
	
	
	@Override
	public final boolean isSupported( InboundType type ){
		return METRONOME_EVENT == type;
	}


	@Override
	public final boolean update( InboundEvent event ){
		handleMetronomeEvent( event );
		return false;
	}
	

	@Override
	public final void init( ){
				
		try{
		
			long startTime 	= TimeUtil.currentMillis( );
			
			StateManager.setState( INITIALIZING );
			LOGGER.debug("Attempting to START Fluent Framework {}.", StateManager.getFrameworkInfo() );
			LOGGER.debug("Configurations {}", cfgManager );
			primeServices( );
			startServices( );
			
			StateManager.setState( RUNNING );
			long timeTaken 	= TimeUtil.currentMillis( ) - startTime;
			
			LOGGER.info( "Successfully STARTED Fluent Framework in [{}] ms.", timeTaken );
			LOGGER.info( "************************************************************** {}", NEWLINE );

        }catch( Exception e ){
        	LOGGER.error( "Fatal error while starting Fluent Framework." );
            LOGGER.error( "Exception: ", e );
            LOGGER.info( "************************************************************** {}", NEWLINE );
        	
            System.exit( ZERO );
            
        }

		//TEST
		sendNewTESTStrategy( );
				
	}
	
	
	protected final void primeServices( ){
		outDispatcher.prime();
		inDispatcher.prime();
	}

	
	protected final void startServices( ){
		
		InboundEventDispatcher.register( this );
		
		outDispatcher.init();
		inDispatcher.init();
		stateManager.init();
		
		algoManager.init();
		mdManager.init();
	}
	
	
	protected final void stopServices( ){
		
		inDispatcher.stop();
		mdManager.stop();
		
		algoManager.stop();
		
		outDispatcher.stop();
		stateManager.stop();
		
	}
	
	
	protected final void sendNewTESTStrategy( ){
	
		try{
			Thread.sleep( 2000 );
		}catch( InterruptedException e ){
			e.printStackTrace();
		}
		
		LOGGER.info(" =================================================");
		LOGGER.info(" Sending TEST Strategy!");
		LOGGER.info(" ================================================={}", NEWLINE);
		
		String strategyId			= "10.1";
		String strategyName			= "EDSpread";
		String strategyTrader		= "visingh";
		Side strategySide			= Side.BUY;
	    int strategyLegCount		= 2;
	    Exchange strategyExchange	= Exchange.CME;
	    double strategySpread		= 0.15;
	    
	    int[] legQtys				= {100, 200};
	    Side[] legSides				= {Side.BUY, Side.SELL};
	    String[] legInstruments		= {"EDH6", "EDM6"};
	    boolean[] legWorking		= {true, false};
	    int[] legSlices				= {10, 20};
	    InstrumentType[] legTypes	= {InstrumentType.ED_FUTURES, InstrumentType.ED_FUTURES};
	    	    
		
		InboundEvent newStratgey 	= new NewStrategyEvent( strategyId, strategyName, strategyTrader, strategySide, strategyLegCount, strategyExchange, strategySpread,
															legQtys, legSides, legInstruments, legWorking, legSlices, legTypes );
		InboundEventDispatcher.enqueue( newStratgey );
	
	}
	
	
	//TODO: Convert Seconds to close to AfterHours, WorkingHOurs, ClosingHours enum?
	protected final void handleMetronomeEvent( InboundEvent event ){
		
		MetronomeEvent metroEvent 	= (MetronomeEvent) event;
		long secondsToClose			= metroEvent.getSecondsToClose();
		
		if( secondsToClose <= 0 ){
			LOGGER.warn("[{}] is running outside working hours [{}], some features may be unavailable.", cfgManager.getInstance( ), cfgManager.getWorkingHours() );
			return;
		}
		
		if( secondsToClose > 10 ){
			LOGGER.debug("Metronome event arrives, we have [{}] seconds to close.", secondsToClose );
			return;
		}
		
		LOGGER.info("STOPPING as we only have [{}] seconds to close.", secondsToClose );
		stop();
		
	}
	

	@Override
	public void stop( ){
		
		try{
			
			stopServices();
			
			LOGGER.debug("Successfully stopped {}.", NAME);
			
		}catch( Exception e ){
			LOGGER.warn("Exception while stopping {}.", NAME);
			LOGGER.warn("Exception", e);
		}
	
	}

		
}
