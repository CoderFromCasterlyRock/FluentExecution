package com.fluent.etrading.requests;

import org.slf4j.*;

import java.util.concurrent.*;

import com.fluent.etrading.framework.core.*;
import com.fluent.etrading.framework.order.*;
import com.fluent.etrading.framework.events.in.*;
import com.fluent.etrading.framework.market.core.*;
import com.fluent.etrading.framework.events.core.*;

import static com.fluent.etrading.framework.utility.ContainerUtil.*;


public final class DummyStrategyEventAdaptor implements Runnable, FluentService{

    private final FluentInputEventType ieType;
    private final RequestEventProvider provider;
    private final ScheduledExecutorService executor;

    private final static String NAME    = DummyStrategyEventAdaptor.class.getSimpleName();
    private final static Logger LOGGER  = LoggerFactory.getLogger( NAME );


    public DummyStrategyEventAdaptor( RequestEventProvider provider ){
        this.provider       = provider;
        this.ieType         = FluentInputEventType.CREATE_STRATEGY;
        this.executor       = Executors.newSingleThreadScheduledExecutor( new FluentThreadFactory(NAME) );

    }


    @Override
    public final String name( ){
        return NAME;
    }


    @Override
    public final void init( ){
        executor.scheduleAtFixedRate( this, FIVE, FOUR*FIVE, TimeUnit.SECONDS );
        LOGGER.warn( "Starting {}, will generate FAKE Strategy requests every 20 seconds.", NAME );
    }


    @Override
    public final void run( ){
        //Real Adaptor would take Trader request as TIBCO message and convert it to an Event
        arrived();
    }


    protected final void arrived(  ){

        long eventId                = FluentInputEventId.nextId();
        String traderName           = "Vicky Singh";
        String[] instrumentArray    = {"2_YEAR", "5_YEAR"};
        MarketType[] marketArray    = {MarketType.BTEC, MarketType.BTEC};
        int[] quantityArray         = {10, 20};
        Side[] sideArray            = {Side.BUY, Side.SELL};
        double spread               = 0.45;
        int meq                     = 2;

        CreateStrategyEvent event   = new CreateStrategyEvent(  eventId, ieType, traderName, "2vs5",
                                                                Side.BUY, 2, spread, instrumentArray, marketArray, quantityArray,
                                                                sideArray, meq  );

        LOGGER.debug( "Request arrived >> [{}]", event.toJSON() );
        provider.addRequestEvent( event );

    }


    @Override
    public final void stop( ){
        executor.shutdown();
    }


}
