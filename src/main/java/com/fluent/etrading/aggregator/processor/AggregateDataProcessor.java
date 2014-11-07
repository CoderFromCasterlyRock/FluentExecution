package com.fluent.etrading.aggregator.processor;

import org.slf4j.*;
import java.util.*;
import java.util.concurrent.*;

import com.fluent.etrading.framework.core.*;
import com.fluent.etrading.framework.collections.*;
import com.fluent.etrading.framework.events.core.*;
import com.fluent.etrading.framework.events.in.*;
import com.fluent.etrading.framework.market.core.*;
import com.fluent.etrading.aggregator.core.*;
import com.fluent.etrading.aggregator.data.*;
import com.fluent.etrading.framework.dispatcher.core.*;
import com.fluent.etrading.framework.dispatcher.in.*;

import static com.fluent.etrading.framework.utility.ContainerUtil.*;



public final class AggregateDataProcessor implements FluentInputEventListener, FluentService{

    private volatile boolean keepProcessing;

    private final Runnable processThread;
    private final BackoffStrategy backoff;
    private final ExecutorService executor;
    private final OriginalSPSCQueue<MarketDataEvent> priceQueue;

    private final static String NAME    = AggregateDataProcessor.class.getSimpleName();
    private final static Logger LOGGER  = LoggerFactory.getLogger( NAME );


    public AggregateDataProcessor( BackoffStrategy backoff, Aggregator aggregator ){

        this.backoff        = backoff;
        this.processThread  = new AggregationThread( aggregator );
        this.priceQueue     = new OriginalSPSCQueue<MarketDataEvent>( FOUR * SIXTY_FOUR );
        this.executor       = Executors.newSingleThreadExecutor( new FluentThreadFactory( NAME) );

    }


    @Override
    public final String name( ){
        return NAME;
    }


    @Override
    public final boolean isSupported( final FluentInputEventType type ){
        return ( FluentEventCategory.MARKET_CATEGORY == type.getCategory() );
    }


    @Override
    public final void init( ){
        keepProcessing = true;
        executor.execute( processThread );

        FluentInputEventDispatcher.add( this );
        LOGGER.info( "Successfully started Market data aggregation thread." );
    }


    @Override
    public final void update( final FluentInputEvent event ){
        MarketDataEvent mdEvent = (MarketDataEvent) event;
        priceQueue.offer( mdEvent );
    }


    @Override
    public final void stop( ){
        keepProcessing = false;
        LOGGER.info( "Successfully stopped {}.", NAME );
    }



    private final class AggregationThread implements Runnable{

        private final int columnCount;
        private final int pivotIndex;
        private final int validRowCount;

        private final Aggregator aggregator;
        private final AggregateRowOrdinal ordinal;
        private final Map<String, AggregateData> mutableMap;

        public AggregationThread( Aggregator aggregator ){
            this.aggregator     = aggregator;
            this.ordinal        = aggregator.getOrdinal();

            this.columnCount    = aggregator.getColumnCount();
            this.pivotIndex     = aggregator.getPivotIndex();
            this.validRowCount  = ordinal.getValidRowCount();

            this.mutableMap     = new HashMap<String, AggregateData>( aggregator.getCapacity() );
        }


        @Override
        public final void run( ){

            while( keepProcessing ){

                try{

                    MarketDataEvent mdEvent = priceQueue.poll( );
                    boolean nothingPolled   = (mdEvent == null);
                    if( nothingPolled ){
                        backoff.apply();
                        continue;
                    }

                    aggregateMarketData( mdEvent );

                }catch( Exception e ){
                    LOGGER.warn( "Exception while processing market data event." );
                    LOGGER.warn( "Exception:", e );
                }

            }

        }


        protected final void aggregateMarketData( MarketDataEvent mdEvent ){

            String commonId             = EMPTY;

            try{

                String instrumentId     = mdEvent.getInstrumentId();
                commonId                = instrumentId; //Use an instrument Mapper
                if( commonId == null ) return;

                //Should come from Instrument Mapper
                //For Swaps use gcd, for treasury use min
                double gcdPriceTick     = 0.1;

                MarketType marketType   = mdEvent.getMarket();
                int bidRank             = ordinal.getRowIndex( true, marketType );
                int askRank             = ordinal.getRowIndex( false, marketType );

                double bid0             = mdEvent.getBid0();
                double bid1             = mdEvent.getBid1();
                double bid2             = mdEvent.getBid2();
                double bid3             = mdEvent.getBid3();
                double bid4             = mdEvent.getBid4();

                int bid0Size            = mdEvent.getBid0Size();
                int bid1Size            = mdEvent.getBid1Size();
                int bid2Size            = mdEvent.getBid2Size();
                int bid3Size            = mdEvent.getBid3Size();
                int bid4Size            = mdEvent.getBid4Size();

                double ask0             = mdEvent.getAsk0();
                double ask1             = mdEvent.getAsk1();
                double ask2             = mdEvent.getAsk2();
                double ask3             = mdEvent.getAsk3();
                double ask4             = mdEvent.getAsk4();

                int ask0Size            = mdEvent.getAsk0Size();
                int ask1Size            = mdEvent.getAsk1Size();
                int ask2Size            = mdEvent.getAsk2Size();
                int ask3Size            = mdEvent.getAsk3Size();
                int ask4Size            = mdEvent.getAsk4Size();

                AggregateData data      = mutableMap.get( commonId );
                int initialUpdate       = ( data == null ) ? ZERO : ONE;
                boolean isUpdateValid   = !isPriceUpdateInvalid( bid0, bid0Size, ask0, ask0Size );

                double pivotPrice       = (bid0 != ZERO && bid0Size != ZERO) ? bid0 : ask0;

                switch( initialUpdate ){

                    case ZERO:{
                        data    = new MutableAggregateData( commonId, pivotIndex, pivotPrice, validRowCount, columnCount, gcdPriceTick );
                        mutableMap.put( commonId, data );
                    }
                    break;

                    case ONE:{
                        if( isUpdateValid && (pivotPrice != data.getPivotPrice()) ){
                            data.shift( pivotPrice );
                        }
                    }

                    //Sort and store all bids
                    int bidCol0   = getColumnIndex( pivotPrice, gcdPriceTick, bid0, pivotIndex );
                    int bidCol1   = getColumnIndex( pivotPrice, gcdPriceTick, bid1, pivotIndex );
                    int bidCol2   = getColumnIndex( pivotPrice, gcdPriceTick, bid2, pivotIndex );
                    int bidCol3   = getColumnIndex( pivotPrice, gcdPriceTick, bid3, pivotIndex );
                    int bidCol4   = getColumnIndex( pivotPrice, gcdPriceTick, bid4, pivotIndex );

                    //Sort and store all asks
                    int askCol0   = getColumnIndex( pivotPrice, gcdPriceTick, ask0, pivotIndex );
                    int askCol1   = getColumnIndex( pivotPrice, gcdPriceTick, ask1, pivotIndex );
                    int askCol2   = getColumnIndex( pivotPrice, gcdPriceTick, ask2, pivotIndex );
                    int askCol3   = getColumnIndex( pivotPrice, gcdPriceTick, ask3, pivotIndex );
                    int askCol4   = getColumnIndex( pivotPrice, gcdPriceTick, ask4, pivotIndex );

                    data.store( bidRank, bidCol0, bid0, bid0Size, bidCol1, bid1, bid1Size, bidCol2, bid2, bid2Size, bidCol3, bid3, bid3Size, bidCol4, bid4, bid4Size );
                    data.store( askRank, askCol0, ask0, ask0Size, askCol1, ask1, ask1Size, askCol2, ask2, ask2Size, askCol3, ask3, ask3Size, askCol4, ask4, ask4Size );

                    aggregator.setData( commonId, data );

                    LOGGER.debug("[IN] Aggregation finished for [{}]",commonId );
                }

            }catch( Exception e ){
                LOGGER.warn( "Exception while aggregating data for [{}]", commonId );
                LOGGER.warn( "Exception: ", e);
            }

        }


        private final boolean isPriceUpdateInvalid( double bid0, int bid0Size, double ask0, int ask0Size ){
            boolean isValid = (bid0 == ZERO && bid0Size == ZERO) && (ask0 == ZERO && ask0Size == ZERO);
            return isValid;
        }


        /**
        * STEP 1: How "far" is the current price from the pivot price.
        * STEP 2: If the current price is too far away from the pivot price, discard it.
        * STEP 3: Convert the distance between current and pivot price in reference to the PivotIndex.
        * STEP 4: The converted distance is the index of the column in which the size will be stored.
        */
        private final int getColumnIndex( double pivotPrice, double gcdPriceTick, double nextPrice, int pivotIndex ){

            int distanceFromPivot       = getRoundedTickDifference( pivotPrice, nextPrice, gcdPriceTick );

            if( Math.abs(distanceFromPivot) > pivotIndex ) return NEGATIVE_ONE;
            int distanceFromPivotIndex  = distanceFromPivot + pivotIndex;
            return distanceFromPivotIndex;
        }

    }

}
