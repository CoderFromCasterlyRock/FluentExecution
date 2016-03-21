package com.fluent.etrading.core;

//@formatter:off
import org.slf4j.*;

import com.fluent.etrading.strategy.spreader.*;
import com.fluent.framework.core.*;
import com.fluent.framework.events.in.*;
import com.fluent.framework.admin.core.*;
import com.fluent.framework.admin.events.*;

import static com.fluent.framework.util.TimeUtil.*;
import static com.fluent.framework.util.FluentUtil.*;
import static com.fluent.framework.admin.core.FluentState.*;


public final class StrategyController implements FluentInListener, FluentLifecycle{

    private final FluentServices services;
    private final SpreadStrategyManager manager;

    private final static String        NAME   = StrategyController.class.getSimpleName( );
    private final static Logger        LOGGER = LoggerFactory.getLogger( NAME );


    public StrategyController( String configFileName ) throws FluentException{
        this.services   = new FluentServices( configFileName );
        this.manager    = new SpreadStrategyManager( services );
        
    }

    
    @Override
    public final String name( ){
        return NAME;
    }
    

    public final FluentServices getServices( ) {
        return services;
    }


    @Override
    public final boolean isSupported( FluentInType type ) {
        return (FluentInType.APP_STATE_EVENT == type);
    }


    @Override
    public final boolean inUpdate( FluentInEvent event ) {

        ApplicationStateEvent sEvent = (ApplicationStateEvent) event;
        boolean isStopping = sEvent.isStopping( );

        if( isStopping ){
            LOGGER.info( "Received State event to STOP [{}]{}", sEvent, NEWLINE );
            stop( );
        }

        return isStopping;

    }


    @Override
    public final void start( ){

        try{

            long startTime = currentMillis( );

            StateManager.setState( INITIALIZING );
            startServices( );
            StateManager.setState( RUNNING );

            long timeTaken = currentMillis( ) - startTime;
            LOGGER.info( "Successfully STARTED {} in [{}] ms.", name(), timeTaken );
            LOGGER.info( "************************************************************** {}", NEWLINE );

        }catch( Exception e ){
            LOGGER.error( "Fatal error while starting {}.", name() );
            LOGGER.error( "Exception: ", e );
            LOGGER.info( "************************************************************** {}", NEWLINE );

            System.exit( ZERO );

        }

    }


    protected final void startServices( ) throws Exception {

        services.getInDispatcher( ).start( );
        services.getInPersister( ).start( );

        services.getMdManager( ).start( );

        services.getOutPersister( ).start( );
        services.getOutDispatcher( ).start( );
        services.getStateManager( ).start( );

        manager.start();

        services.getInDispatcher( ).register( this );
    }


    protected final void stopServices( ) throws Exception {

        services.getInDispatcher( ).stop( );
        services.getInPersister( ).stop( );
        services.getMdManager( ).stop( );

        manager.stop();
        
        services.getOutDispatcher( ).stop( );
        services.getOutPersister( ).stop( );
        services.getStateManager( ).stop( );
        
        services.getInDispatcher( ).deregister( this );

    }


    @Override
    public void stop( ) {

        try{

            stopServices( );
            LOGGER.debug( "Successfully stopped {}.", NAME );

        }catch( Exception e ){
            LOGGER.warn( "Exception while stopping {}.", NAME );
            LOGGER.warn( "Exception", e );
        }

    }

    @Override
    public final String toString( ){
        return getServices().getCfgManager().getFrameworkInfo( );
    }

    /*
    protected final void sendNewTESTStrategy( ){
    
        try{
            Thread.sleep( 2000 );
        }catch( InterruptedException e ){
            e.printStackTrace();
        }
        
        LOGGER.info(" =================================================");
        LOGGER.info(" Sending TEST Strategy!");
        LOGGER.info(" ================================================={}", NEWLINE);
        
        String strategyId           = "10.1";
        String strategyName         = "EDSpread";
        String strategyTrader       = "visingh";
        Side strategySide           = Side.BUY;
        int strategyLegCount        = 2;
        Exchange strategyExchange   = Exchange.CME;
        double strategySpread       = 0.15;
        
        int[] legQtys               = {100, 200};
        Side[] legSides             = {Side.BUY, Side.SELL};
        String[] legInstruments     = {"EDH6", "EDM6"};
        boolean[] legWorking        = {true, false};
        int[] legSlices             = {10, 20};
        InstrumentType[] legTypes   = {InstrumentType.FUTURES, InstrumentType.FUTURES};
                
        
        InEvent newStratgey         = new NewStrategyEvent( strategyId, strategyName, strategyTrader, strategySide, strategyLegCount, strategyExchange, strategySpread,
                                                            legQtys, legSides, legInstruments, legWorking, legSlices, legTypes );
        inDispatcher.enqueue( newStratgey );
    
    }
    */
    
}
