package com.fluent.etrading.marketdata;

import java.io.*;
import org.slf4j.*;
import java.util.*;
import java.util.concurrent.*;

import com.fluent.etrading.framework.core.FluentService;
import com.fluent.etrading.framework.core.FluentThreadFactory;
import com.fluent.etrading.framework.events.core.FluentInputEventId;
import com.fluent.etrading.framework.events.core.FluentInputEventType;
import com.fluent.etrading.framework.events.in.MarketDataEvent;
import com.fluent.etrading.framework.events.in.MarketDataEventProvider;
import com.fluent.etrading.framework.market.core.MarketType;

import static com.fluent.etrading.framework.utility.ContainerUtil.*;


public final class DummyMarketDataAdaptor implements Runnable, FluentService{

    private final MarketType marketType;
    private final FluentInputEventType ieType;
    private final MarketDataEventProvider provider;
    private final ScheduledExecutorService executor;

    private final static String NAME    = DummyMarketDataAdaptor.class.getSimpleName();
    private final static Logger LOGGER  = LoggerFactory.getLogger( NAME );
    private final static String[] INS   = { "2_YEAR", "3_YEAR", "5_YEAR", "7_YEAR", "10_YEAR", "30_YEAR" };


    public DummyMarketDataAdaptor( FluentInputEventType ieType,
                                        MarketType marketType,
                                        MarketDataEventProvider provider ) throws IOException{

        this.ieType         = ieType;
        this.marketType     = marketType;
        this.provider       = provider;
        this.executor       = Executors.newSingleThreadScheduledExecutor( new FluentThreadFactory(NAME) );
    }
    

    @Override
    public final String name( ){
        return NAME;
    }

    
    @Override
    public final void init( ){
        executor.scheduleAtFixedRate( this, FIVE, 7, TimeUnit.SECONDS );
        LOGGER.warn( "Starting {}, will publish FAKE prices for {} every 5 seconds.", NAME, Arrays.deepToString( INS ) );
    }


    @Override
    public final void run( ){


        for( String instrument : INS ){

            long eventId            = FluentInputEventId.nextId();

            double bid0             = 100.0;
            double bidTick          = Math.random();

            double ask0             = 101.0;
            double askTick          = Math.random();

            double[] bid            = { bid0, (bid0 - bidTick), (bid0 - 2*bidTick), (bid0 - 3*bidTick), (bid0 - 2*bidTick)};
            int[] bidSize           = { 55, 65, 75, 85, 95 };
            double[] ask            = { ask0, (ask0 + askTick), (ask0 + 2*askTick), (ask0 + 3*askTick), (ask0 + 2*askTick)};
            int[] askSize           = { 50, 60, 70, 80, 90 };

            MarketDataEvent event   = new MarketDataEvent(  eventId, ieType, marketType, instrument,
                                                            bid[0],     bid[1],     bid[2],     bid[3],     bid[4],
                                                            bidSize[0], bidSize[1], bidSize[2], bidSize[3], bidSize[4],
                                                            ask[0],     ask[1],     ask[2],     ask[3],     ask[4],
                                                            askSize[0], askSize[1], askSize[2], askSize[3], askSize[4]
                                                          );

            provider.addMarketDataEvent( event );

        }

    }


    @Override
    public final void stop( ){
        executor.shutdown();
    }


    public final static double between( final double upper, final double lower ){
        return (Math.random() * (upper - lower)) + lower;
    }


}
