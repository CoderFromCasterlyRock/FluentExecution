package com.fluent.etrading.core;

import org.slf4j.*;

import com.fluent.etrading.events.in.NewStrategyEvent;
import com.fluent.etrading.order.Side;
import com.fluent.etrading.strategy.spreader.SpreadAlgoManager;
import com.fluent.framework.admin.AdminClosingEvent;
import com.fluent.framework.admin.StateManager;
import com.fluent.framework.algo.FluentAlgoManager;
import com.fluent.framework.config.ConfigManager;
import com.fluent.framework.core.FluentService;
import com.fluent.framework.core.FluentServices;
import com.fluent.framework.events.in.*;
import com.fluent.framework.events.out.OutEvent;
import com.fluent.framework.events.out.OutEventDispatcher;
import com.fluent.framework.market.adaptor.MarketDataManager;
import com.fluent.framework.market.core.Exchange;
import com.fluent.framework.market.core.InstrumentType;
import com.fluent.framework.persistence.InChroniclePersisterService;
import com.fluent.framework.persistence.OutChroniclePersisterService;
import com.fluent.framework.persistence.PersisterService;

import static com.fluent.framework.util.TimeUtil.*;
import static com.fluent.framework.util.FluentUtil.*;
import static com.fluent.framework.events.in.InType.*;
import static com.fluent.framework.core.FluentContext.FluentState.*;


public final class FluentController implements InListener, FluentService{
	
	private final ConfigManager cfgManager;
	private final FluentServices services;
	
	private final StateManager stateManager;
	private final MarketDataManager mdManager;
	private final PersisterService<InEvent> inPersister;
	private final InEventDispatcher inDispatcher;
	private final PersisterService<OutEvent> outPersister;
	private final OutEventDispatcher outDispatcher;
	private final FluentAlgoManager algoManager;
		
	
	private final static String NAME    = FluentController.class.getSimpleName();
    private final static Logger LOGGER	= LoggerFactory.getLogger( NAME );

    
	public FluentController( String cfgFileName ) throws Exception{
		
		this.cfgManager		= new ConfigManager( cfgFileName );
		this.inPersister	= new InChroniclePersisterService(cfgManager);
		this.inDispatcher	= new InEventDispatcher( );
		this.outPersister	= new OutChroniclePersisterService(cfgManager);
		this.outDispatcher	= new OutEventDispatcher( );
		this.stateManager	= new StateManager( cfgManager, inDispatcher );
		this.mdManager		= new MarketDataManager( cfgManager, inDispatcher );
		
		this.services		= new FluentServices(cfgManager, inDispatcher, outDispatcher, mdManager);
		this.algoManager	= new SpreadAlgoManager( services );
		
	}

	
	@Override
	public final String name( ){
		return NAME;
	}
	
	
	@Override
	public final boolean isSupported( InType type ){
		return CLOSING_EVENT == type;
	}


	@Override
	public final boolean inUpdate( InEvent event ){
		
		AdminClosingEvent closeEvent= (AdminClosingEvent) event;
		boolean isAppClosing		= closeEvent.appClosing();
			
		if( isAppClosing ){
			LOGGER.info("STOPPING as we received [{}].", closeEvent );
			stop();
		}
		
		return isAppClosing;
	}
	

	@Override
	public final void start( ){
				
		try{
		
			long startTime 	= currentMillis( );
			
			StateManager.setState( INITIALIZING );
			LOGGER.debug("Attempting to START {}.", cfgManager.getFrameworkInfo() );
			LOGGER.debug("Configurations {}", cfgManager );
			startServices( );
			
			StateManager.setState( RUNNING );
			long timeTaken 	= currentMillis( ) - startTime;
			
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
	
		
	protected final void startServices( ){
		
		inDispatcher.register( this );
		
		outPersister.start();
		outDispatcher.start();
		inPersister.start();
		inDispatcher.start();
		stateManager.start();
		
		algoManager.start();
		mdManager.start();
	}
	
	
	protected final void stopServices( ){
		
		inDispatcher.stop();
		inPersister.stop();
		mdManager.stop();
		
		algoManager.stop();
		
		outDispatcher.stop();
		outPersister.stop();
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
	    InstrumentType[] legTypes	= {InstrumentType.FUTURES, InstrumentType.FUTURES};
	    	    
		
		InEvent newStratgey 		= new NewStrategyEvent( strategyId, strategyName, strategyTrader, strategySide, strategyLegCount, strategyExchange, strategySpread,
															legQtys, legSides, legInstruments, legWorking, legSlices, legTypes );
		inDispatcher.enqueue( newStratgey );
	
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
