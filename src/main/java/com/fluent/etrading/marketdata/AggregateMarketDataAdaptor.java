package com.fluent.etrading.marketdata;

import com.fluent.etrading.framework.events.core.*;
import com.fluent.etrading.framework.market.core.MarketType;
import com.fluent.etrading.framework.events.in.MarketDataEvent;
import com.fluent.etrading.framework.events.in.MarketDataEventProvider;


public final class AggregateMarketDataAdaptor{

    private final MarketType marketType;
    private final FluentInputEventType ieType;
    private final MarketDataEventProvider provider;
    

    public AggregateMarketDataAdaptor( FluentInputEventType ieType, MarketType marketType, MarketDataEventProvider provider ){
        this.ieType         = ieType;
        this.marketType     = marketType;
        this.provider		= provider;
    }

    
    public final void arrived( String instrument, double[] bid, int[] bidSize, double[] ask, int[] askSize ){

        long eventId            = FluentInputEventId.nextId();

        MarketDataEvent event   = new MarketDataEvent(  eventId, ieType, marketType, instrument,
                                                        bid[0],     bid[1],     bid[2],     bid[3],     bid[4],
                                                        bidSize[0], bidSize[1], bidSize[2], bidSize[3], bidSize[4],
                                                        ask[0],     ask[1],     ask[2],     ask[3],     ask[4],
                                                        askSize[0], askSize[1], askSize[2], askSize[3], askSize[4]
                                                        );

        provider.addMarketDataEvent( event );

    }


}
